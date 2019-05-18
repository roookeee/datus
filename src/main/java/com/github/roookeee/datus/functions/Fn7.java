package com.github.roookeee.datus.functions;

import java.util.function.Function;

public interface Fn7<A, B, C, D, E, F, G, RT> extends DependentApplicable<A, B, Fn6<A, C, D, E, F, G, RT>> {
    RT apply(A a, B b, C c, D d, E e, F f, G g);

    /**
     * {@inheritDoc}
     */
    default Fn6<A, C, D, E, F, G, RT> dependentApply(Function<A, B> fn) {
        return (a, c, d, e, f, g) -> apply(a, fn.apply(a), c, d, e, f, g);
    }
}
