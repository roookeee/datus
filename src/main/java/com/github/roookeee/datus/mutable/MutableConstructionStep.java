package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.conditional.ConditionalEnd;
import com.github.roookeee.datus.shared.SafetyMode;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a construction step from a given input to an output type while holding a reference
 * to a getter on the input type that is currently being used to further construct the output type object.
 * <p>
 * This class enables the user to {@link #map} the current getters type or define fallbacks via {@link #given} before
 * finishing the current construction step with a {@link #to} or {@link #into}-call.
 *
 * @param <In>          the input type
 * @param <CurrentType> the getters type
 * @param <Out>         the output type
 */
public final class MutableConstructionStep<In, CurrentType, Out> {

    private final MutableMappingBuilder<In, Out> builder;
    private final Function<? super In, ? extends CurrentType> getter;
    private final SafetyMode safetyMode;

    MutableConstructionStep(MutableMappingBuilder<In, Out> builder, Function<? super In, ? extends CurrentType> getter, SafetyMode safetyMode) {
        this.builder = builder;
        this.getter = getter;
        this.safetyMode = safetyMode;
    }

    /**
     * Activates the null safe mode of this construction step: subsequent {@link #map} or {@link #given} steps won't be
     * considered if their input is null - null values are directly propagated instead.
     * Subsequent {@link #given} steps lose the ability to null check as their entire predicate handling is skipped
     * when a null value is provided.
     *
     * @return a nullsafe variant of the current construction step
     */
    public MutableConstructionStep<In, CurrentType, Out> nullsafe() {
        return new MutableConstructionStep<>(builder, getter, SafetyMode.NULL_SAFE);
    }

    /**
     * Maps the current steps type to another type (similar to {@link java.util.stream.Stream#map}
     *
     * @param mapper     the function to apply to
     * @param <NextType> the return type of the converter function which is used for the new construction step
     * @return a new construction step based on the new type
     */
    public <NextType> MutableConstructionStep<In, NextType, Out> map(Function<? super CurrentType, ? extends NextType> mapper) {
        return new MutableConstructionStep<>(
                builder,
                getter.andThen(handleSafetyMode(mapper)),
                safetyMode
        );
    }

    /**
     * Finishes the current construction step by generating a function that applies the given setter on the Out object.
     *
     * @param setter the setter to apply
     * @return the builder this step originated from
     */
    public MutableMappingBuilder<In, Out> into(BiConsumer<? super Out, ? super CurrentType> setter) {
        builder.addMapper((in, out) -> {
            setter.accept(out, getter.apply(in));
            return out;
        });
        return builder;
    }

    /**
     * Finishes the current construction step by generating a function that applies the given setter on the Out object
     * which is added to the builder this step originated from.
     * <p>
     * Note: A setter of this kind may or may not return a new Out object instance when called. All steps that follow
     * the current step will use the returned Out object.
     *
     * @param setter the setter to apply
     * @return the builder this step originated from
     */
    public MutableMappingBuilder<In, Out> to(BiFunction<? super Out, ? super CurrentType, ? extends Out> setter) {
        builder.addMapper((in, out) -> setter.apply(out, getter.apply(in)));
        return builder;
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param value              the value to use when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, MutableConstructionStep<In, IntermediateType, Out>> given(
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
     * @return a builder to configure the handling mechanism when the given predicate does not
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, MutableConstructionStep<In, IntermediateType, Out>> given(
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
     * @return a builder to configure the handling mechanism when the given predicate does not
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, MutableConstructionStep<In, IntermediateType, Out>> given(
            Predicate<? super CurrentType> predicate,
            Function<? super CurrentType, ? extends IntermediateType> mapper
    ) {
        return given(predicate, (in, v) -> mapper.apply(v));
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate          the predicate to use
     * @param mapper             the function to map the current type with when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not
     */
    public <IntermediateType> ConditionalEnd<In, CurrentType, IntermediateType, MutableConstructionStep<In, IntermediateType, Out>> given(
            Predicate<? super CurrentType> predicate,
            BiFunction<? super In, ? super CurrentType, ? extends IntermediateType> mapper
    ) {
        return new ConditionalEnd<>(
                getter,
                predicate,
                newGetter -> new MutableConstructionStep<>(builder, newGetter, safetyMode),
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