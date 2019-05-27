package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.conditional.ConditionalEnd;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ConstructorParameterBinding<In, Type, Ctor> {
    private final Ctor ctor;
    private final Function<? super In, ? extends Type> getter;

    ConstructorParameterBinding(Ctor ctor, Function<? super In, ? extends Type> getter) {
        this.ctor = ctor;
        this.getter = getter;
    }

    /**
     * Maps the current parameter binding type to another type (similar to {@link java.util.stream.Stream#map}
     *
     * @param mapper             the function to apply to
     * @param <IntermediateType> the return type of the converter function
     * @return a new parameter binding based based on the given mapper
     */
    public <IntermediateType> ConstructorParameterBinding<In, IntermediateType, Ctor> map(Function<? super Type, ? extends IntermediateType> mapper) {
        return new ConstructorParameterBinding<>(ctor, getter.andThen(mapper));
    }

    /**
     * Binds a parameter binding to its destination, should always be a {@link ConstructorParameter#bind} reference.
     *
     * @param parameterBinder the function to pass the parameter binding too
     * @param <ResultType>    the type of the result, either another parameter binding or the end of the constructor
     * @return the next step in the constructor binding process
     */
    public <ResultType> ResultType to(BiFunction<Ctor, Function<? super In, ? extends Type>, ResultType> parameterBinder) {
        return parameterBinder.apply(ctor, getter);
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate the predicate to use
     * @param value the value to use when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, Type, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super Type> predicate,
            IntermediateType value
    ) {
        return given(predicate, (in,v) -> value);
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate the predicate to use
     * @param supplier the supplier to use when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, Type, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super Type> predicate,
            Supplier<? extends IntermediateType> supplier
    ) {
        return given(predicate, (in,v) -> supplier.get());
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate for the current type.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate the predicate to use
     * @param mapper the function to map the current type with when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, Type, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super Type> predicate,
            Function<? super Type, ? extends IntermediateType> mapper
    ) {
        return given(predicate, (in,v) -> mapper.apply(v));
    }

    /**
     * Starts a conditional mapping process in regards to the given predicate.
     *
     * @param <IntermediateType> the resulting type of the conditional mapping process
     * @param predicate the predicate to use
     * @param mapper the function to map the current type with when the provided predicate matches
     * @return a builder to configure the handling mechanism when the given predicate does not match
     */
    public <IntermediateType> ConditionalEnd<In, Type, IntermediateType, ConstructorParameterBinding<In, IntermediateType, Ctor>> given(
            Predicate<? super Type> predicate,
            BiFunction<? super In, ? super Type, ? extends IntermediateType> mapper
    ) {
        return new ConditionalEnd<>(
                getter,
                predicate,
                newGetter -> new ConstructorParameterBinding<>(ctor, newGetter),
                mapper
        );
    }
}
