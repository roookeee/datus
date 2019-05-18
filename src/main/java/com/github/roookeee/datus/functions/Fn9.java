package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn9<A, B, C, D, E, F, G, H, I, RT> extends DependentApplicable<A, B, Fn8<A, C, D, E, F, G, H, I, RT>> {
    RT apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);

    /**
     * {@inheritDoc}
     */
    default Fn8<A, C, D, E, F, G, H, I, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d, e, f, g, h, i) -> apply(a, fn.apply(a), c, d, e, f, g, h, i);
    }
}
