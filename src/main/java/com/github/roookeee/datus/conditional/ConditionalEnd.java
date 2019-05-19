package com.github.roookeee.datus.conditional;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An intermediate class that finishes the handling process of a predicate matching on a constructions .given()-step.
 *
 * @param <In>               the input type of the given mapping process (needed for more complex handling)
 * @param <AffectedType>     the type of the object the predicate matched on (needed to provide typed handling)
 * @param <ConstructionStep> the construction step this instance has originated from (needed for further chaining)
 */
public class ConditionalEnd<In, AffectedType, ConstructionStep> {
    private final ConstructionStep origin;
    private final Predicate<AffectedType> predicate;
    private final ConditionalHandlingWeaver<In, AffectedType, ConstructionStep> nextStepProvider;
    private final BiFunction<In, AffectedType, AffectedType> matchingHandler;

    public ConditionalEnd(
            ConstructionStep origin,
            Predicate<AffectedType> predicate,
            ConditionalHandlingWeaver<In, AffectedType, ConstructionStep> nextStepProvider,
            BiFunction<In, AffectedType, AffectedType> matchingHandler
    ) {
        this.origin = origin;
        this.predicate = predicate;
        this.nextStepProvider = nextStepProvider;
        this.matchingHandler = matchingHandler;
    }

    /**
     * Finish the conditional handling process by not applying any further steps. A non-matching predicate will
     * thus leave the value as is.
     *
     * @return the construction step this instance has originated from
     */
    public ConstructionStep proceed() {
        return orElse((in, v) -> v);
    }

    /**
     * Finish the conditional handling process by using the passed value when the predicate the current handling is
     * based on did <strong>not</strong> match.
     *
     * @param value the value to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(AffectedType value) {
        return orElse((in, v) -> value);
    }

    /**
     * Finish the conditional handling process by using the passed supplier to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     *
     * @param supplier the supplier to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(Supplier<AffectedType> supplier) {
        return orElse((in, v) -> supplier.get());
    }

    /**
     * Finish the conditional handling process by using the passed function to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     * <p>
     * Note: Only use this method if you know what you are doing, there is no guarantee about the state of the input
     * object other than that it is (depending on your usage) probably not null.
     *
     * @param function the supplier to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(Function<In, AffectedType> function) {
        return orElse((in, v) -> function.apply(in));
    }

    /**
     * Finish the conditional handling process by using the passed function to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     * <p>
     * Note: Only use this method if you know what you are doing, there is no guarantee about the state of the input
     * object / the value this handling is based on other than that they are (depending on your usage) probably not null.
     *
     * @param function the supplier to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(BiFunction<In, AffectedType, AffectedType> function) {
        return nextStepProvider.weave(origin, predicate, matchingHandler, function);
    }
}
