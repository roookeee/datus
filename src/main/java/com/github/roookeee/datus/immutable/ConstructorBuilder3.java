package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn3;
import com.github.roookeee.datus.functions.Fn4;

import java.util.function.Function;

public class ConstructorBuilder3<In, A, B, C, Out> {
    private final Fn4<In, A, B, C, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder3(Fn3<A, B, C, Out> constructor) {
        this((in, a, b, c) -> constructor.apply(a, b, c));
    }

    ConstructorBuilder3(Fn4<In, A, B, C, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * Starts a construction step for the first parameter of the contained constructor starting from the provided getter
     * of the input type.
     *
     * @param <GT> the getters return type
     * @param getter the getter to start the construction process from
     * @return a construction step to define the building process of the constructors first parameter
     */
    public <GT> ImmutableConstructionStep<In, GT, A, ConstructorBuilder2<In, B, C, Out>> from(Function<In, GT> getter) {
        return new ImmutableConstructionStep<>(
                getter,
                (aGetter) -> new ConstructorBuilder2<>(constructor.dependentApply(aGetter))
        );
    }
}
