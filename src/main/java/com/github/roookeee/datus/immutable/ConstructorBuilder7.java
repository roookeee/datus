package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.functions.Fn7;
import com.github.roookeee.datus.functions.Fn8;

import java.util.function.Function;

public final class ConstructorBuilder7<In, A, B, C, D, E, F, G, Out>
        extends AbstractConstructorBuilder<In, ConstructorBuilder7<In, A, B, C, D, E, F, G, Out>>
        implements ConstructorParameter<In, A, ConstructorBuilder6<In, B, C, D, E, F, G, Out>> {
    private final Fn8<In, A, B, C, D, E, F, G, Out> constructor;

    /**
     * ***NOTE***: It is encouraged to use the {@link Datus} class instead of directly
     * instantiating any constructor builder objects by yourself as javas type inference cannot infer the generic types
     * of these objects. The {@link Datus} class alleviates this problem.
     *
     * @param constructor the constructor to generate a builder for
     */
    public ConstructorBuilder7(Fn7<A, B, C, D, E, F, G, Out> constructor) {
        this((in, a, b, c, d, e, f, g) -> constructor.apply(a, b, c, d, e, f, g));
    }

    ConstructorBuilder7(Fn8<In, A, B, C, D, E, F, G, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstructorBuilder6<In, B, C, D, E, F, G, Out> bind(Function<? super In, ? extends A> getter) {
        return new ConstructorBuilder6<>(applyGetter(getter));
    }

    @Override
    ConstructorBuilder7<In, A, B, C, D, E, F, G,Out> getSelf() {
        return this;
    }

    private Fn7<In, B, C, D, E, F, G, Out> applyGetter(Function<? super In, ? extends A> getter) {
        return (in, b, c, d, e, f, g) -> constructor.apply(in, getter.apply(in), b, c, d, e, f, g);
    }

}
