package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn8<A, B, C, D, E, F, G, H, RT> extends DependentApplicable<A, B, Fn7<A, C, D, E, F, G, H, RT>> {
    RT apply(A a, B b, C c, D d, E e, F f, G g, H h);

    /**
     * {@inheritDoc}
     */
    default Fn7<A, C, D, E, F, G, H, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d, e, f, g, h) -> apply(a, fn.apply(a), c, d, e, f, g, h);
    }
}
