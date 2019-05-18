package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn6<A, B, C, D, E, F, RT> extends DependentApplicable<A, B, Fn5<A, C, D, E, F, RT>> {
    RT apply(A a, B b, C c, D d, E e, F f);

    /**
     * {@inheritDoc}
     */
    default Fn5<A, C, D, E, F, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d, e, f) -> apply(a, fn.apply(a), c, d, e, f);
    }
}
