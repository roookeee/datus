package com.github.roookeee.datus.conditional;

import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Integrates the handling of a given predicate into a construction step.
 *
 * @param <In>               the input type of the given mapping process
 * @param <AffectedType>     the type of the object the predicate matched on
 * @param <ConstructionStep> the construction steps type to generate
 */
public interface ConditionalHandlingWeaver<In, AffectedType, ConstructionStep> {
    /**
     * Weaves in functions that handle a given predicates outcome by generating a new construction step that uses the given
     * handling functions internally.
     *
     * @param base      the construction step that serves as a basis
     * @param predicate the predicate to consider
     * @param matching  the function to use when the predicate matches
     * @param orElse    the function tu use when the predicate does not match
     * @return a construction step that considers the handling functions according to the passed predicate
     */
    ConstructionStep weave(
            ConstructionStep base,
            Predicate<AffectedType> predicate,
            BiFunction<In, AffectedType, AffectedType> matching,
            BiFunction<In, AffectedType, AffectedType> orElse
    );
}
