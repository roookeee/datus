package datus.datamapper.immutable;

import datus.datamapper.functions.Fn2;
import datus.datamapper.functions.Fn3;

import java.util.function.Function;

public class ConstructorBuilder2<In, A, B, Out> {
    private final Fn3<In, A, B, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link datus.datamapper.api.Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link datus.datamapper.api.Datus} alleviates this problem!
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder2(Fn2<A, B, Out> constructor) {
        this((in, a, b) -> constructor.apply(a, b));
    }

    ConstructorBuilder2(Fn3<In, A, B, Out> constructor) {
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
    public <Z> ImmutableConstructionStep<In, Z, A, ConstructorBuilder1<In, B, Out>> from(Function<In, Z> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder1<>(applyGetter(aGetter))
        );
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param getter the getter to use
     * @return the next constructor builder to further define the building process of the output type
     */
    public ConstructorBuilder1<In, B, Out> take(Function<In, A> getter) {
        return new ConstructorBuilder1<>(applyGetter(getter));
    }

    private Fn2<In, B, Out> applyGetter(Function<In, A> getter) {
        return (in, b) -> constructor.apply(in, getter.apply(in), b);
    }
}