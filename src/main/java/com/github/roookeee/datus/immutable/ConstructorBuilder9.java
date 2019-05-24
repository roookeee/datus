package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn10;
import com.github.roookeee.datus.functions.Fn8;
import com.github.roookeee.datus.functions.Fn9;

import java.util.function.Function;

public class ConstructorBuilder9<In, A, B, C, D, E, F, G, H, I, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder9<In, A, B, C, D, E, F, G, H, I, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder8<In, B, C, D, E, F, G, H, I, Out>> {
    private final Fn10<In, A, B, C, D, E, F, G, H, I, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder9(Fn9<A, B, C, D, E, F, G, H, I, Out> constructor) {
        this((in, a, b, c, d, e, f, g, h, i) -> constructor.apply(a, b, c, d, e, f, g, h, i));
    }

    ConstructorBuilder9(Fn10<In, A, B, C, D, E, F, G, H, I, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder8<In, B, C, D, E, F, G, H, I, Out> bind(Function<In, A> getter) {
        return new ConstructorBuilder8<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder9<In, A, B, C, D, E, F, G, H, I, Out> getSelf() {
        return this;
    }

    private Fn9<In, B, C, D, E, F, G, H, I, Out> applyGetter(Function<In, A> getter) {
        return (in, b, c, d, e, f, g, h, i) -> constructor.apply(in, getter.apply(in), b, c, d, e, f, g, h, i);
    }
}
