package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.mutable.MutableConstructionStep;
import com.github.roookeee.datus.general.ConditionalHandlerBuilder;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a construction step from a given input to an output type while holding a reference
 * to a getter on the input type that is currently being used to further construct the output type object.
 * <p>
 * In contrast to the mutable API ({@link MutableConstructionStep )} an immutable construction step
 * specializes the mapping process of exactly one constructor parameter starting with a getter of the input type
 * which's type may or may not already match the constructor parameters type. {@link #map} and {@link #when} are the
 * main tools of an immutable construction step to reach the desired constructor parameters type and handling edge
 * cases.
 *
 * @param <In>              the input type
 * @param <CurrentType>     the getters type
 * @param <TargetType>      the desired constructor parameter type
 * @param <NextConstructor> the constructor object which specializes the next constructor parameter
 */
public class ImmutableConstructionStep<In, CurrentType, TargetType, NextConstructor> {

    private Function<In, CurrentType> getter;
    private final Function<Function<In, TargetType>, NextConstructor> nextConstructorGetter;

    ImmutableConstructionStep(Function<In, CurrentType> getter, Function<Function<In, TargetType>, NextConstructor> nextConstructorGetter) {
        this.getter = getter;
        this.nextConstructorGetter = nextConstructorGetter;
    }

    /**
     * Finishes the current construction step by providing a finalizing mapping that guarantees a conversion from
     * this steps current type to the constructor parameters needed type.
     *
     * @param mapper the mapper to convert this steps current type to the constructor parameters needed type
     * @return a constructor object which specializes the next output types constructor parameter
     */
    public NextConstructor mapTo(Function<CurrentType, TargetType> mapper) {
        return nextConstructorGetter.apply(getter.andThen(mapper));
    }

    /**
     * Maps the current steps type to another type (similar to {@link java.util.stream.Stream#map}
     *
     * @param mapper             the function to apply to
     * @param <IntermediateType> the return type of the converter function which is used for the new construction step
     * @return a new construction step based on the new type
     */
    public <IntermediateType> ImmutableConstructionStep<In, IntermediateType, TargetType, NextConstructor> map(
            Function<CurrentType, IntermediateType> mapper
    ) {
        return new ImmutableConstructionStep<>(
                getter.andThen(mapper),
                nextConstructorGetter
        );
    }

    /**
     * Starts an adjustment-process for this step which is used when the given predicate matches (e.g. a fallback)
     *
     * @param predicate the predicate to use
     * @return a builer to configure the handling mechanism when the given predicate matches
     */
    public ConditionalHandlerBuilder<In, CurrentType, ImmutableConstructionStep<In, CurrentType, TargetType, NextConstructor>> when(
            Predicate<CurrentType> predicate
    ) {
        return new ConditionalHandlerBuilder<>(
                fallback -> new ImmutableConstructionStep<>(getterWithFallback(predicate, fallback), nextConstructorGetter)
        );
    }

    private Function<In, CurrentType> getterWithFallback(Predicate<CurrentType> predicate, Function<In, CurrentType> fallback) {
        return in -> {
            CurrentType value = getter.apply(in);
            if (predicate.test(value)) {
                return fallback.apply(in);
            }
            return value;
        };
    }
}
