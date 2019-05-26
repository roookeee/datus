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
public class ConditionalEnd<In, AffectedType, IntermediateType, ConstructionStep> {

    private final Function<In, AffectedType> getter;
    private final Predicate<AffectedType> predicate;
    private final Function<Function<In, IntermediateType>, ConstructionStep> nextStepProvider;
    private final BiFunction<In, AffectedType, IntermediateType> matchingHandler;

    public ConditionalEnd(
            Function<In, AffectedType> getter,
            Predicate<AffectedType> predicate,
            Function<Function<In, IntermediateType>, ConstructionStep> nextStepProvider,
            BiFunction<In, AffectedType, IntermediateType> matchingHandler
    ) {
        this.getter = getter;
        this.predicate = predicate;
        this.nextStepProvider = nextStepProvider;
        this.matchingHandler = matchingHandler;
    }

    /**
     * Finish the conditional handling process by using the passed value when the predicate the current handling is
     * based on did <strong>not</strong> match.
     *
     * @param value the value to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(IntermediateType value) {
        return orElse((in, v) -> value);
    }

    /**
     * Finish the conditional handling process by using the passed supplier to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     *
     * @param supplier the supplier to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(Supplier<IntermediateType> supplier) {
        return orElse((in, v) -> supplier.get());
    }

    /**
     * Finish the conditional handling process by using the passed function to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     *
     * Note: Only use this method if you know what you are doing as there is no guarantee about the state of the input
     * object.
     *
     * @param function the function to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(Function<AffectedType, IntermediateType> function) {
        return orElse((in, v) -> function.apply(v));
    }

    /**
     * Finish the conditional handling process by using the passed function to determine a new value when the predicate
     * the current handling is based on did <strong>not</strong> match.
     *
     * Note: Only use this method if you know what you are doing as there is no guarantee about the state of the input
     * objects.
     *
     * @param function the function to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElse(BiFunction<In, AffectedType, IntermediateType> function) {
        return nextStepProvider.apply(weave(getter, predicate, matchingHandler, function));
    }

    private Function<In, IntermediateType> weave(
            Function<In, AffectedType> getter,
            Predicate<AffectedType> predicate,
            BiFunction<In, AffectedType, IntermediateType> matching,
            BiFunction<In, AffectedType, IntermediateType> orElse
    ) {
        return in -> {
            AffectedType value = getter.apply(in);
            if (predicate.test(value)) {
                return matching.apply(in, value);
            }
            return orElse.apply(in, value);
        };
    }
}
