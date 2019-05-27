package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn3;
import com.github.roookeee.datus.functions.Fn4;

import java.util.function.Function;

public final class ConstructorBuilder3<In, A, B, C, Out>
    extends AbstractConstructorBuilder<In, ConstructorBuilder3<In, A, B,C, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder2<In, B, C, Out>> {
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder2<In, B, C, Out> bind(Function<? super In, ? extends A> getter) {
        return new ConstructorBuilder2<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder3<In, A, B, C, Out> getSelf() {
        return this;
    }

    private Fn3<In, B, C, Out> applyGetter(Function<? super In, ? extends A> getter) {
        return (in,b,c) -> constructor.apply(in, getter.apply(in), b, c);
    }

}
