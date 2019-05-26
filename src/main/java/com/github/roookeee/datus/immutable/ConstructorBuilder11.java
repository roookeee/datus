package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn10;
import com.github.roookeee.datus.functions.Fn11;
import com.github.roookeee.datus.functions.Fn12;

import java.util.function.Function;

public class ConstructorBuilder11<In, A, B, C, D, E, F, G, H, I, J, K, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder11<In, A, B, C, D, E, F, G, H, I, J, K, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder10<In, B, C, D, E, F, G, H, I, J, K, Out>> {
    private final Fn12<In, A, B, C, D, E, F, G, H, I, J, K, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder11(Fn11<A, B, C, D, E, F, G, H, I, J, K, Out> constructor) {
        this((in, a, b, c, d, e, f, g, h, i, j, k) -> constructor.apply(a, b, c, d, e, f, g, h, i, j, k));
    }

    ConstructorBuilder11(Fn12<In, A, B, C, D, E, F, G, H, I, J, K, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder10<In, B, C, D, E, F, G, H, I, J, K, Out> bind(Function<? super In, A> getter) {
        return new ConstructorBuilder10<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder11<In, A, B, C, D, E, F, G, H, I, J, K, Out> getSelf() {
        return this;
    }

    private Fn11<In, B, C, D, E, F, G, H, I, J, K, Out> applyGetter(Function<? super In, A> getter) {
        return (in, b, c, d, e, f, g, h, i, j, k) -> constructor.apply(in, getter.apply(in), b, c, d, e, f, g, h, i, j, k);
    }
}
