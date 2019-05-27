package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn4;
import com.github.roookeee.datus.functions.Fn5;

import java.util.function.Function;

public final class ConstructorBuilder4<In, A, B, C, D, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder4<In, A, B, C, D, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder3<In, B, C, D, Out>> {
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
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder3<In, B, C, D, Out> bind(Function<? super In, A> getter) {
        return new ConstructorBuilder3<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder4<In, A, B, C, D, Out> getSelf() {
        return this;
    }

    private Fn4<In, B, C, D, Out> applyGetter(Function<? super In, A> getter) {
        return (in, b, c, d) -> constructor.apply(in, getter.apply(in), b, c, d);
    }
}
