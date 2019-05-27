package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn11;
import com.github.roookeee.datus.functions.Fn12;
import com.github.roookeee.datus.functions.Fn13;

import java.util.function.Function;

public final class ConstructorBuilder12<In, A, B, C, D, E, F, G, H, I, J, K, L, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder12<In, A, B, C, D, E, F, G, H, I, J, K, L, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder11<In, B, C, D, E, F, G, H, I, J, K, L, Out>> {
    private final Fn13<In, A, B, C, D, E, F, G, H, I, J, K, L, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder12(Fn12<A, B, C, D, E, F, G, H, I, J, K, L, Out> constructor) {
        this((in, a, b, c, d, e, f, g, h, i, j, k, l) -> constructor.apply(a, b, c, d, e, f, g, h, i, j, k, l));
    }

    ConstructorBuilder12(Fn13<In, A, B, C, D, E, F, G, H, I, J, K, L, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder11<In, B, C, D, E, F, G, H, I, J, K, L, Out> bind(Function<? super In, ? extends A> getter) {
        return new ConstructorBuilder11<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder12<In, A, B, C, D, E, F, G, H, I, J, K, L, Out> getSelf() {
        return this;
    }

    private Fn12<In, B, C, D, E, F, G, H, I, J, K, L, Out> applyGetter(Function<? super In, ? extends A> getter) {
        return (in, b, c, d, e, f, g, h, i, j, k, l) -> constructor.apply(in, getter.apply(in), b, c, d, e, f, g, h, i, j, k, l);
    }
}
