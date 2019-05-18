package com.github.roookeee.datus.functions;

import java.util.function.Function;

/**
 * DependentApplicable for the types A, B allows binding the value of B to a function {@link java.util.function.Function<A,B>}
 * to eliminate the need to pass a B value. This is a form of partial function application that is dependent on the preceding
 * parameters value.
 *
 * @param <A> the type to base the partial function application on
 * @param <B> the type to be erased by partial function application
 * @param <C> the resulting type representing the dependent applicable after binding B
 */
public interface DependentApplicable<A, B, C> {
    /**
     * Binds this DependentApplicable's B value to the passed function.
     *
     * @param fn the function to bind B with
     * @return the resulting type representing the dependent applicable after binding B
     */
    C dependentApply(Function<A, B> fn);
}
