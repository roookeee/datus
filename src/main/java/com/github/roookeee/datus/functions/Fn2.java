package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn2<A, B, RT> extends DependentApplicable<A, B, Fn1<A, RT>> {
    RT apply(A a, B b);

    /**
     * {@inheritDoc}
     */
    default Fn1<A, RT> dependentApply(Function<A, B> fn) {
        return a -> apply(a, fn.apply(a));
    }
}
