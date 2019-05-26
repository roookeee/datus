package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn2;
import com.github.roookeee.datus.functions.Fn3;

import java.util.function.Function;

public final class ConstructorBuilder2<In, A, B, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder2<In, A, B, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder1<In, B, Out>> {
    private final Fn3<In, A, B, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder1<In, B, Out> bind(Function<In, A> getter) {
        return new ConstructorBuilder1<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder2<In, A, B, Out> getSelf() {
        return this;
    }

    private Fn2<In, B, Out> applyGetter(Function<In, A> getter) {
        return (in,b) -> constructor.apply(in, getter.apply(in), b);
    }
}
