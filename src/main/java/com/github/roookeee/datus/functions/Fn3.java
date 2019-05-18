package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn3<A, B, C, RT> extends DependentApplicable<A, B, Fn2<A, C, RT>> {
    RT apply(A a, B b, C c);

    /**
     * {@inheritDoc}
     */
    default Fn2<A, C, RT> dependentApply(Function<A, B> fn) {
        return (a, c) -> apply(a, fn.apply(a), c);
    }
}
