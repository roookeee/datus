package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn4;
import com.github.roookeee.datus.functions.Fn5;

import java.util.function.Function;

public class ConstructorBuilder4<In, A, B, C, D, Out> {
    private final Fn5<In, A, B, C, D, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder4(Fn4<A, B, C, D, Out> constructor) {
        this((in, a, b, c, d) -> constructor.apply(a, b, c, d));
    }

    ConstructorBuilder4(Fn5<In, A, B, C, D, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param <GT> the getters return type
     * @param getter the getter to use
     * @return the next constructor builder to further define the building process of the output type
     */
    public <GT> ImmutableConstructionStep<In, GT, A, ConstructorBuilder3<In, B, C, D, Out>> from(Function<In, GT> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder3<>(applyGetter(aGetter))
        );
    }

    /**
     * Directly binds the contained constructors first parameter to the provided getter of the input type.
     * (Utility function that works like {@link #from}.to(Function.identity()))
     *
     * @param getter the getter to use
     * @return the next constructor to further define the building process of the output type
     */
    public ConstructorBuilder3<In, B, C, D, Out> take(Function<In, A> getter) {
        return new ConstructorBuilder3<>(applyGetter(getter));
    }

    private Fn4<In, B, C, D, Out> applyGetter(Function<In, A> getter) {
        return (in, b, c, d) -> constructor.apply(in, getter.apply(in), b, c, d);
    }
}
