package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn5<A, B, C, D, E, RT> extends DependentApplicable<A, B, Fn4<A, C, D, E, RT>> {
    RT apply(A a, B b, C c, D d, E e);

    /**
     * {@inheritDoc}
     */
    default Fn4<A, C, D, E, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d, e) -> apply(a, fn.apply(a), c, d, e);
    }
}
