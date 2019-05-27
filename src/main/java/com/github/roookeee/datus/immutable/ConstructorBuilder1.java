package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn1;
import com.github.roookeee.datus.functions.Fn2;

import java.util.function.Function;

public final class ConstructorBuilder1<In, A, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder1<In, A, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder<In, Out>> {
    private final Fn2<In, A, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder<In, Out> bind(Function<? super In, ? extends A> getter) {
        return new ConstructorBuilder<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder1<In, A, Out> getSelf() {
        return this;
    }

    private Fn1<In, Out> applyGetter(Function<? super In, ? extends A> getter) {
        return in -> constructor.apply(in, getter.apply(in));
    }

}
