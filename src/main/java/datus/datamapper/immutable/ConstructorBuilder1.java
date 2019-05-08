package datus.datamapper.immutable;

import datus.datamapper.functions.Fn1;
import datus.datamapper.functions.Fn2;

import java.util.function.Function;

public class ConstructorBuilder1<In, A, Out> {
    private final Fn2<In, A, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link datus.datamapper.api.Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link datus.datamapper.api.Datus} alleviates this problem!
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder1(Fn1<A, Out> constructor) {
        this((in, a) -> constructor.apply(a));
    }

    ConstructorBuilder1(Fn2<In, A, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * Starts a construction step for the first parameter of the contained constructor starting from the provided getter
     * of the input type.
     *
     * @param getter the getter to start the construction process from
     * @param <Z>    the getters return type
     * @return a construction step to define the building process of the constructors first parameter
     */
    public <Z> ImmutableConstructionStep<In, Z, A, ConstructorBuilder<In, Out>> from(Function<In, Z> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder<>(applyGetter(aGetter))
        );
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param getter the getter to use
     * @return the next constructor builder to further define the building process of the output type
     */
    public ConstructorBuilder<In, Out> take(Function<In, A> getter) {
        return new ConstructorBuilder<>(applyGetter(getter));
    }

    private Fn1<In, Out> applyGetter(Function<In, A> getter) {
        return in -> constructor.apply(in, getter.apply(in));
    }
}
