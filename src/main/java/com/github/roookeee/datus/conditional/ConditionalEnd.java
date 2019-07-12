package com.github.roookeee.datus.conditional;

import com.github.roookeee.datus.shared.SafetyMode;

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

    private final Function<? super In, ? extends AffectedType> getter;
    private final Predicate<? super AffectedType> predicate;
    private final Function<Function<? super In, ? extends IntermediateType>, ConstructionStep> nextStepProvider;
    private final BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> matchingHandler;
    private final SafetyMode safetyMode;

    public ConditionalEnd(
            Function<? super In, ? extends AffectedType> getter,
            Predicate<? super AffectedType> predicate,
            Function<Function<? super In, ? extends IntermediateType>, ConstructionStep> nextStepProvider,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> matchingHandler
    ) {
        this(getter, predicate, nextStepProvider, matchingHandler, SafetyMode.NONE);
    }

    public ConditionalEnd(
            Function<? super In, ? extends AffectedType> getter,
            Predicate<? super AffectedType> predicate,
            Function<Function<? super In, ? extends IntermediateType>, ConstructionStep> nextStepProvider,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> matchingHandler,
            SafetyMode safetyMode
    ) {
        this.getter = getter;
        this.predicate = predicate;
        this.nextStepProvider = nextStepProvider;
        this.matchingHandler = matchingHandler;
        this.safetyMode = safetyMode;
    }
    /**
     * Finish the conditional handling process by using null as a value when the predicate the current handling is
     * based on did <strong>not</strong> match.
     *
     * This is a utility function as orElse(null) would be ambiguous for the java compilers overload resolution.
     *
     * @return the construction step this instance has originated from
     */
    public ConstructionStep orElseNull() {
        return orElse((in, v) -> null);
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
    public ConstructionStep orElse(Supplier<? extends IntermediateType> supplier) {
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
    public ConstructionStep orElse(Function<? super AffectedType, ? extends IntermediateType> function) {
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
    public ConstructionStep orElse(BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> function) {
        return nextStepProvider.apply(weave(getter, predicate, matchingHandler, function));
    }

    private Function<In, IntermediateType> weave(
            Function<? super In, ? extends AffectedType> getter,
            Predicate<? super AffectedType> predicate,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> matching,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> orElse
    ) {
        if (safetyMode == SafetyMode.NULL_SAFE) {
            return weaveNullsafe(getter, predicate, matching, orElse);
        }
        return in -> {
            AffectedType value = getter.apply(in);
            if (predicate.test(value)) {
                return matching.apply(in, value);
            }
            return orElse.apply(in, value);
        };
    }

    private Function<In, IntermediateType> weaveNullsafe(
            Function<? super In, ? extends AffectedType> getter,
            Predicate<? super AffectedType> predicate,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> matching,
            BiFunction<? super In, ? super AffectedType, ? extends IntermediateType> orElse
    ) {
        return in -> {
            AffectedType value = getter.apply(in);
            if (value == null) {
                return null;
            }
            if (predicate.test(value)) {
                return matching.apply(in, value);
            }
            return orElse.apply(in, value);
        };
    }
}
