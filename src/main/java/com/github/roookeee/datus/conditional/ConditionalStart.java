package com.github.roookeee.datus.conditional;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An intermediate class that acts as a starting point on how to handle a predicate matching on a constructions .given()-step.
 *
 * @param <In>               the input type of the given mapping process (needed for more complex handling)
 * @param <AffectedType>     the type of the object the predicate matched on (needed to provide typed handling)
 * @param <ConstructionStep> the construction step this instance has originated from (needed for further chaining)
 */
public class ConditionalStart<In, AffectedType, ConstructionStep> {

    private final ConstructionStep origin;
    private final Predicate<AffectedType> predicate;
    private final ConditionalHandlingWeaver<In, AffectedType, ConstructionStep> nextStepProvider;

    public ConditionalStart(
            ConstructionStep origin,
            Predicate<AffectedType> predicate,
            ConditionalHandlingWeaver<In, AffectedType, ConstructionStep> nextStepProvider
    ) {
        this.origin = origin;
        this.predicate = predicate;
        this.nextStepProvider = nextStepProvider;
    }

    /**
     * Handle the matching predicate by using given value.
     * <p>
     * Note: The object is taken as is, consider using {@link #then(Supplier)} if the given object is stateful /
     * not thread-safe or in any other way mutably shared between output instances.
     *
     * @param value the value to use
     * @return the construction step this instance has originated from
     */
    public ConditionalEnd<In, AffectedType, ConstructionStep> then(AffectedType value) {
        return then((in,v) -> value);
    }

    /**
     * Handle the matching predicate by using the given supplier to generate a value.
     *
     * @param supplier the supplier to use
     * @return an object to complete the conditional handling process
     */
    public ConditionalEnd<In, AffectedType, ConstructionStep> then(Supplier<AffectedType> supplier) {
        return then((in,v) -> supplier.get());
    }


    /**
     * Handle the matching predicate by using the given function on the input type to generate a value.
     * <p>
     * Note: Only use this method if you know what you are doing, there is no guarantee about the state of the input
     * object other than that it is (depending on your usage) probably not null.
     *
     * @param function the function to use
     * @return an object to complete the conditional handling process
     */
    public ConditionalEnd<In, AffectedType, ConstructionStep> then(Function<In, AffectedType> function) {
        return then((in,v) -> function.apply(in));
    }

    /**
     * Handle the matching predicate by using the given function on the input type to generate a value.
     * <p>
     * Note: Only use this method if you know what you are doing, there is no guarantee about the state of the input
     * object / the value this handling is based on other than that they are (depending on your usage) probably not null.
     *
     * @param function the function to use
     * @return an object to complete the conditional handling process
     */
    public ConditionalEnd<In, AffectedType, ConstructionStep> then(BiFunction<In, AffectedType, AffectedType> function) {
        return new ConditionalEnd<>(
                origin,
                predicate,
                nextStepProvider,
                function
        );
    }
}
