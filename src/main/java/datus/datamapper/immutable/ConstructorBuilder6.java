package datus.datamapper.immutable;

import datus.datamapper.functions.Fn6;
import datus.datamapper.functions.Fn7;

import java.util.function.Function;

public class ConstructorBuilder6<In, A, B, C, D, E, F, Out> {
    private final Fn7<In, A, B, C, D, E, F, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link datus.datamapper.api.Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link datus.datamapper.api.Datus} alleviates this problem!
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder6(Fn6<A, B, C, D, E, F, Out> constructor) {
        this((in, a, b, c, d, e, f) -> constructor.apply(a, b, c, d, e, f));
    }

    ConstructorBuilder6(Fn7<In, A, B, C, D, E, F, Out> constructor) {
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
    public <Z> ImmutableConstructionStep<In, Z, A, ConstructorBuilder5<In, B, C, D, E, F, Out>> from(Function<In, Z> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder5<>(applyGetter(aGetter))
        );
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param getter the getter to use
     * @return the next constructor builder to further define the building process of the output type
     */
    public ConstructorBuilder5<In, B, C, D, E, F, Out> take(Function<In, A> getter) {
        return new ConstructorBuilder5<>(applyGetter(getter));
    }

    private Fn6<In, B, C, D, E, F, Out> applyGetter(Function<In, A> getter) {
        return (in, b, c, d, e, f) -> constructor.apply(in, getter.apply(in), b, c, d, e, f);
    }
}