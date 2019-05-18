package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn4<A, B, C, D, RT> extends DependentApplicable<A, B, Fn3<A, C, D, RT>> {
    RT apply(A a, B b, C c, D d);

    /**
     * {@inheritDoc}
     */
    default Fn3<A, C, D, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d) -> apply(a, fn.apply(a), c, d);
    }
}
