package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn6;
import com.github.roookeee.datus.functions.Fn7;

import java.util.function.Function;

public final class ConstructorBuilder6<In, A, B, C, D, E, F, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder6<In, A, B, C, D, E, F, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder5<In, B, C, D, E, F, Out>> {
    private final Fn7<In, A, B, C, D, E, F, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder5<In, B, C, D, E, F, Out> bind(Function<? super In, A> getter) {
        return new ConstructorBuilder5<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder6<In, A, B, C, D, E, F, Out> getSelf() {
        return this;
    }

    private Fn6<In, B, C, D, E, F, Out> applyGetter(Function<? super In, A> getter) {
        return (in, b, c, d, e, f) -> constructor.apply(in, getter.apply(in), b, c, d, e, f);
    }

}
