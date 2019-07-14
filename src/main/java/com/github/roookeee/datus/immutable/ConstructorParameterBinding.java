package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.conditional.ConditionalEnd;
import com.github.roookeee.datus.shared.SafetyMode;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An intermediate class to handle the binding of a constructor parameter. Holds a reference to a getter on the
 * input type which serves as the foundation for any additional steps before finalizing the parameter binding.
 * <p>
 * This class enables the user to {@link #map} the current getters type or define fallbacks via {@link #given} before
 * finishing the binding with a {@link #to}-call.
 *
 * @param <In>          the input type
 * @param <CurrentType> the getters type
 * @param <Ctor>        the current constructors type which will receive the finalized binding
 */
public final class ConstructorParameterBinding<In, CurrentType, Ctor> {
    private final Ctor ctor;
    private final Function<? super In, ? extends CurrentType> getter;
    private final SafetyMode safetyMode;

    ConstructorParameterBinding(Ctor ctor, Function<? super In, ? extends CurrentType> getter, SafetyMode safetyMode) {
        this.ctor = ctor;
        this.getter = getter;
        this.safetyMode = safetyMode;
    }

    /**
     * Activates the null safe mode of this parameter binding: subsequent {@link #map} or {@link #given} steps won't be
     * considered if their input is null - null values are directly propagated instead.
     * Subsequent {@link #given} steps lose the ability to null check as their entire predicate handling is skipped
     * when a null value is provided.
     *
     * @return a nullsafe variant of the current parameter binding
     */
    public ConstructorParameterBinding<In, CurrentType, Ctor> nullsafe() {
        return new ConstructorParameterBinding<>(ctor, getter, SafetyMode.NULL_SAFE);
    }

    /**
     * Maps the current parameter binding type to another type (similar to {@link java.util.stream.Stream#map}
     *
     * @param mapper             the function to apply to
     * @param <IntermediateType> the return type of the converter function
     * @return a new parameter binding based based on the given mapper
     */
    public <IntermediateType> ConstructorParameterBinding<In, IntermediateType, Ctor> map(Function<? super CurrentType, ? extends IntermediateType> mapper) {
        return new ConstructorParameterBinding<>(
                ctor,
                getter.andThen(handleSafetyMode(mapper)),
                safetyMode
        );
    }

    /**
     * Binds a parameter binding to its destination, should always be a {@link ConstructorParameter#bind} reference.
     *
     * @param parameterBinder the function to pass the parameter binding too
     * @param <ResultType>    the type of the result, either another parameter binding or the end of the constructor
     * @return the next step in the constructor binding process
     */
    public <ResultType> ResultType to(BiFunction<Ctor, Function<? super In, ? extends CurrentType>, ResultType> parameterBinder) {
        return parameterBinder.apply(ctor, getter);
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param value              the value to use when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super CurrentType> predicate,
            IntermediateType value
    ) {
        return given(predicate, (in, v) -> value);
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param supplier           the supplier to use when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super CurrentType> predicate,
            Supplier<? extends IntermediateType> supplier
    ) {
        return given(predicate, (in, v) -> supplier.get());
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param mapper             the function to map the current type with when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super CurrentType> predicate,
            Function<? super CurrentType, ? extends IntermediateType> mapper
    ) {
        return given(predicate, (in, v) -> mapper.apply(v));
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param mapper             the function to map the current type with when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super CurrentType> predicate,
            BiFunction<? super In, ? super CurrentType, ? extends IntermediateType> mapper
    ) {
        return new ConditionalEnd<>(
                getter,
                predicate,
                newGetter -> new ConstructorParameterBinding<>(ctor, newGetter, safetyMode),
                mapper,
                safetyMode
        );
    }

    private <T,U> Function<T,U> handleSafetyMode(Function<T,U> mapper) {
        if (safetyMode == SafetyMode.NONE) {
            return mapper;
        }
        return value -> value != null ? mapper.apply(value) : null;
    }
}
