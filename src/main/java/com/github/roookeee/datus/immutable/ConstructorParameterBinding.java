package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.conditional.ConditionalEnd;

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
    private final boolean nullsafe;

    ConstructorParameterBinding(Ctor ctor, Function<? super In, ? extends CurrentType> getter) {
        this(ctor, getter, false);
    }

    private ConstructorParameterBinding(Ctor ctor, Function<? super In, ? extends CurrentType> getter, boolean nullsafe) {
        this.ctor = ctor;
        this.getter = getter;
        this.nullsafe = nullsafe;
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
        return new ConstructorParameterBinding<>(ctor, getter, true);
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
                getter.andThen(handleNullability(mapper)),
                nullsafe
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
        /*
        Some thoughts on handling the nullsafe flag here as it seems quite complicated but is actually easy to handle:
        Instead of handling the nullsafety in the predicate and the true+false handlers we just take the result
        of the ConditionalEnd and don't call it when we receive a null value via handleNullability().

        This means we lose the ability to handle nulls in a given() when nullsafe() was called but that seems like
        a strange use-case which is not implemented for now.
         */
        return new ConditionalEnd<>(
                getter,
                predicate,
                newGetter -> new ConstructorParameterBinding<>(ctor, handleNullability(newGetter), nullsafe),
                mapper
        );
    }

    private <T,U> Function<T,U> handleNullability(Function<T,U> mapper) {
        if (!nullsafe) {
            return mapper;
        }
        return value -> value != null ? mapper.apply(value) : null;
    }
}
