package datus.datamapper.immutable;

import datus.datamapper.functions.Fn5;
import datus.datamapper.functions.Fn6;

import java.util.function.Function;

public class ConstructorBuilder5<In, A, B, C, D, E, Out> {
    private final Fn6<In, A, B, C, D, E, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link datus.datamapper.api.Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link datus.datamapper.api.Datus} alleviates this problem!
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder5(Fn5<A, B, C, D, E, Out> constructor) {
        this((in, a, b, c, d, e) -> constructor.apply(a, b, c, d, e));
    }

    ConstructorBuilder5(Fn6<In, A, B, C, D, E, Out> constructor) {
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
    public <Z> ImmutableConstructionStep<In, Z, A, ConstructorBuilder4<In, B, C, D, E, Out>> from(Function<In, Z> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder4<>(applyGetter(aGetter))
        );
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param getter the getter to use
     * @return the next constructor builder to further define the building process of the output type
     */
    public ConstructorBuilder4<In, B, C, D, E, Out> take(Function<In, A> getter) {
        return new ConstructorBuilder4<>(applyGetter(getter));
    }

    private Fn5<In, B, C, D, E, Out> applyGetter(Function<In, A> getter) {
        return (in, b, c, d, e) -> constructor.apply(in, getter.apply(in), b, c, d, e);
    }
}
