package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn10;
import com.github.roookeee.datus.functions.Fn11;
import com.github.roookeee.datus.functions.Fn9;

import java.util.function.Function;

public final class ConstructorBuilder10<In, A, B, C, D, E, F, G, H, I, J, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder10<In, A, B, C, D, E, F, G, H, I, J, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder9<In, B, C, D, E, F, G, H, I, J, Out>> {
    private final Fn11<In, A, B, C, D, E, F, G, H, I, J, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder10(Fn10<A, B, C, D, E, F, G, H, I, J, Out> constructor) {
        this((in, a, b, c, d, e, f, g, h, i, j) -> constructor.apply(a, b, c, d, e, f, g, h, i, j));
    }

    ConstructorBuilder10(Fn11<In, A, B, C, D, E, F, G, H, I, J, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder9<In, B, C, D, E, F, G, H, I, J, Out> bind(Function<? super In, A> getter) {
        return new ConstructorBuilder9<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder10<In, A, B, C, D, E, F, G, H, I, J, Out> getSelf() {
        return this;
    }

    private Fn10<In, B, C, D, E, F, G, H, I, J, Out> applyGetter(Function<? super In, A> getter) {
        return (in, b, c, d, e, f, g, h, i, j) -> constructor.apply(in, getter.apply(in), b, c, d, e, f, g, h, i, j);
    }
}
