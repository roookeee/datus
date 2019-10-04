package com.github.roookeee.datus.shared;

import com.github.roookeee.datus.immutable.ConstructorParameterBinding;
import com.github.roookeee.datus.mutable.MutableConstructionStep;
import com.github.roookeee.datus.mutable.MutableMappingBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public final class LambdaHelper {

    static final Set<String> WHITELISTED_CALLEES = new HashSet<>(
            Arrays.asList(
                    OptimizationInitializer.class.getName(),
                    MutableConstructionStep.class.getName(),
                    ConstructorParameterBinding.class.getName(),
                    MutableMappingBuilder.class.getName()
            )
    );

    static boolean OPTIMIZE;

    static {
        //don't event attempt to do dynamic class loading in SubstrateVM
        boolean isNativeImage = System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        boolean disableOptimization = System.getProperty("com.github.roookeee.datus.optimization.disable") != null;
        if (!isNativeImage && !disableOptimization) {
            OptimizationInitializer.init();
        }
    }

    public static <T, U, R> BiFunction<T, R, R> toBiFunction(
            Function<? super T, ? extends U> getter,
            BiConsumer<? super R, ? super U> setter
    ) {
        checkPrivileges();
        if (!OPTIMIZE) {
            return new SetterBiFn<>(getter, setter);
        }

        return optimize(SetterBiFn.class, BiFunction.class, getter, setter);
    }

    public static <T, U, V, R> BiFunction<T, U, R> andThen(
            BiFunction<? super T, ? super U, ? extends V> first,
            BiFunction<? super T, ? super V, ? extends R> second
    ) {
        checkPrivileges();
        if (!OPTIMIZE) {
            return new BiFunctionCombinator<>(first, second);
        }

        return optimize(BiFunctionCombinator.class, BiFunction.class, first, second);
    }

    public static <T, U, R> Function<T, R> andThen(
            Function<? super T, ? extends U> first,
            Function<? super U, ? extends R> second
    ) {
        checkPrivileges();
        if (!OPTIMIZE) {
            return new FunctionCombinator<>(first, second);
        }

        return optimize(FunctionCombinator.class, Function.class, first, second);
    }

    public static <T, R> Function<T, R> nullsafe(Function<T, R> fn) {
        checkPrivileges();
        if (!OPTIMIZE) {
            return new NullSafeFunction<>(fn);
        }

        return optimize(NullSafeFunction.class, Function.class, fn);
    }

    private static <T, U extends T> T optimize(Class<U> clazz, Class<T> interfaceType, Object... ctorArgs) {
        URL classLocation = clazz.getProtectionDomain()
                .getCodeSource().getLocation();
        try (URLClassLoader cl = new URLClassLoader(new URL[]{classLocation}, null)) {
            /*
            As the passed ctorArgs may be from another classloader from previous optimize(...) calls
            we have to reduce their types to their functional bootstrap interface (Function, BiFunction etc.)
            or else getConstructor(...) won't work
             */
            Class[] parameterInterfaceTypes = Arrays.stream(ctorArgs)
                    .map(Object::getClass)
                    .map(c -> c.getInterfaces()[0])
                    .toArray(Class[]::new);
            Constructor<? extends T> ctor = cl.loadClass(clazz.getName())
                    .asSubclass(interfaceType)
                    .getConstructor(parameterInterfaceTypes);
            /*
            as the static helper classes passed to this function are package private we have to make their constructors
            explicitly visible to make them callable
             */
            ctor.setAccessible(true);
            return ctor.newInstance(ctorArgs);
        } catch (IOException | ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static void checkPrivileges() {
        Optional<String> firstExternalCaller = Arrays.stream(Thread.currentThread().getStackTrace())
                //skip getStackTrace() stack trace element
                .skip(1)
                .map(StackTraceElement::getClassName)
                .filter(className -> !className.equals(LambdaHelper.class.getName()))
                .findFirst();

        //should never happen (e.g. a test still call LambdaHelper from another class) but just to be sure
        //-> this is an error case as LambdaHelper should not call its own public functions
        if (!firstExternalCaller.isPresent()) {
            throw new IllegalStateException("Using datus internal functionality is forbidden." +
                    " These APIs are highly unstable / brittle and thus not included in datus public API / versioning scheme!");
        }

        String caller = firstExternalCaller.get();
        if (!WHITELISTED_CALLEES.contains(caller)) {
            throw new IllegalStateException("Using datus internal functionality is forbidden." +
                    " These APIs are highly unstable / brittle and thus not included in datus public API / versioning scheme!");
        }
    }

    static final class BiFunctionCombinator<T, U, V, R> implements BiFunction<T, U, R> {
        private final BiFunction<? super T, ? super U, ? extends V> first;
        private final BiFunction<? super T, ? super V, ? extends R> second;

        //has to be public to be accessible for reflective lookups
        public BiFunctionCombinator(
                BiFunction<? super T, ? super U, ? extends V> first,
                BiFunction<? super T, ? super V, ? extends R> second
        ) {
            this.first = first;
            this.second = second;
        }

        @Override
        public R apply(T t, U u) {
            return second.apply(t, first.apply(t, u));
        }
    }


    static final class FunctionCombinator<T, U, R> implements Function<T, R> {
        private final Function<? super T, ? extends U> first;
        private final Function<? super U, ? extends R> second;

        //has to be public to be accessible for reflective lookups
        public FunctionCombinator(
                Function<? super T, ? extends U> first,
                Function<? super U, ? extends R> second
        ) {
            this.first = first;
            this.second = second;
        }

        @Override
        public R apply(T t) {
            return second.apply(first.apply(t));
        }
    }

    static class NullSafeFunction<T, R> implements Function<T, R> {
        private final Function<? super T, ? extends R> fn;

        //has to be public to be accessible for reflective lookups
        public NullSafeFunction(Function<? super T, ? extends R> fn) {
            this.fn = fn;
        }

        @Override
        public R apply(T t) {
            if (t == null) {
                return null;
            }
            return fn.apply(t);
        }

    }

    static final class SetterBiFn<T, U, R> implements BiFunction<T, R, R> {
        private final Function<? super T, ? extends U> getter;
        private final BiConsumer<? super R, ? super U> setter;

        //has to be public to be accessible for reflective lookups
        public SetterBiFn(
                Function<? super T, ? extends U> getter,
                BiConsumer<? super R, ? super U> setter
        ) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public R apply(T t, R r) {
            setter.accept(r, getter.apply(t));
            return r;
        }
    }

    //Use a separate class for the static initialization so we can whitelist the dummy calls via normal means
    private static final class OptimizationInitializer {
        static void init() {
            /*
            the basic idea is to try optimize some dummy routines and fallback to unoptimized versions if any error occurs.

            This approach shouldn't leak as the synthesized class goes out of scope immediately which should make itself
            and the creating ClassLoader (garbage) collectible
             */
            LambdaHelper.OPTIMIZE = true;
            try {
                boolean allNonNull = Stream.of(
                        toBiFunction(a -> null, (a, b) -> { }),
                        andThen((a, b) -> null, (a, b) -> null),
                        andThen(a -> null, a -> null),
                        nullsafe(a -> null)
                ).allMatch(Objects::nonNull);

                //safe-guard against implementation errors in the future
                if (!allNonNull) {
                    throw new RuntimeException("datus mapper optimization did not work as expected - please file an issue at https://github.com/roookeee/datus." +
                            " You can set the system property 'com.github.roookeee.datus.optimization.disable' to avoid this message for now");
                }
            } catch (Exception ex) {
                //don't catch any implementation error related non-fatal exceptions (e.g. constructor not found)
                throw ex;
            } catch (Throwable t) {
                //deoptimize on any fatal errors (e.g. dynamic class loading is impossible)
                System.out.println("WARNING: datus tried to optimize the mapper code generation but failed to do so because a fatal error occured." +
                        " This most likely means that your JVM does not support dynamic class loading." +
                        " Set the system property 'com.github.roookeee.datus.optimization.disable' to avoid this warning in future executions." +
                        " datus is falling back to unoptimized routines");
                LambdaHelper.OPTIMIZE = false;
            }
        }
    }

    private LambdaHelper() {

    }

}

