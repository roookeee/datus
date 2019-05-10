package com.github.roookeee.datus.general;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An intermediate class that defines how to handle a predicate matching on a given construction .when()-step.
 *
 * @param <In>               the input type of the given mapping process (needed for more complex fallbacks)
 * @param <AffectedType>     the type of the object the predicate matched on (needed to provide types fallbacks)
 * @param <ConstructionStep> the construction step this instance has originated from (needed for further chaining)
 */
public class ConditionalHandlerBuilder<In, AffectedType, ConstructionStep> {

    private final Function<Function<In, AffectedType>, ConstructionStep> nextStepProvider;

    /**
     * ***NOTE***: DO NOT INSTANTIATE THIS OBJECT BY YOURSELF! THIS CONSTRUCTOR IS ONLY PUBLIC BECAUSE JAVA
     * DOES NOT HAVE A MORE FINE GRAINED VISIBILITY CONCEPT!
     *
     * @param stepProvider a function providing an instance of the step to return when the predicates handling is finished.
     */
    public ConditionalHandlerBuilder(Function<Function<In, AffectedType>, ConstructionStep> stepProvider) {
        this.nextStepProvider = stepProvider;
    }

    /**
     * Defines a fallback by the given value.
     * <p>
     * Note: The object is taken as is, consider using {@link #fallback(Supplier)} if the given object is stateful /
     * not thread-safe or in any other way mutably shared between output instances.
     *
     * @param value the value to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep fallback(AffectedType value) {
        return nextStepProvider.apply(in -> value);
    }

    /**
     * Defines a fallback by calling the given supplier when a fallback is needed.
     *
     * @param supplier the supplier to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep fallback(Supplier<AffectedType> supplier) {
        return nextStepProvider.apply(in -> supplier.get());
    }

    /**
     * Defines a fallback by calling the given function on the input type providing a fallback of the correct type.
     * <p>
     * Note: Only use this method if you know what you are doing, there is no guarantee about the state of the input
     * object other than that it is (unless you use datus wrong) not null.
     *
     * @param function the function to use
     * @return the construction step this instance has originated from
     */
    public ConstructionStep fallback(Function<In, AffectedType> function) {
        return nextStepProvider.apply(function);
    }
}
