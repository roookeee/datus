package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn5;
import com.github.roookeee.datus.functions.Fn6;

import java.util.function.Function;

public final class ConstructorBuilder5<In, A, B, C, D, E, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder5<In, A, B, C, D, E, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder4<In, B, C, D, E, Out>> {
    private final Fn6<In, A, B, C, D, E, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder4<In, B, C, D, E, Out> bind(Function<In, A> getter) {
        return new ConstructorBuilder4<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder5<In, A, B, C, D, E, Out> getSelf() {
        return this;
    }

    private Fn5<In, B, C, D, E, Out> applyGetter(Function<In, A> getter) {
        return (in, b, c, d, e) -> constructor.apply(in, getter.apply(in), b, c, d, e);
    }

}
