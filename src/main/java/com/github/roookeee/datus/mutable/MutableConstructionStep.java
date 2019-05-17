package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.general.ConditionalHandlerBuilder;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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
public class MutableConstructionStep<In, CurrentType, Out> {

    private final MutableMappingBuilder<In, Out> builder;
    private final Function<In, CurrentType> getter;

    MutableConstructionStep(MutableMappingBuilder<In, Out> builder, Function<In, CurrentType> getter) {
        this.builder = builder;
        this.getter = getter;
    }

    /**
     * Maps the current steps type to another type (similar to {@link java.util.stream.Stream#map}
     *
     * @param mapper     the function to apply to
     * @param <NextType> the return type of the converter function which is used for the new construction step
     * @return a new construction step based on the new type
     */
    public <NextType> MutableConstructionStep<In, NextType, Out> map(Function<CurrentType, NextType> mapper) {
        return new MutableConstructionStep<>(
                builder,
                in -> mapper.apply(getter.apply(in))
        );
    }


    /**
     * Finishes the current construction step by generating a function that applies the given setter on the Out object.
     *
     * @param setter the setter to apply
     * @return the builder this step originated from
     */
    public MutableMappingBuilder<In, Out> into(BiConsumer<Out, CurrentType> setter) {
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
    public MutableMappingBuilder<In, Out> to(BiFunction<Out, CurrentType, Out> setter) {
        builder.addMapper((in, out) -> setter.apply(out, getter.apply(in)));
        return builder;
    }

    /**
     * Starts an adjustment-process for this step which is used when the given predicate matches (e.g. a fallback)
     *
     * @param predicate the predicate to use
     * @return a builder to configure the handling mechanism when the given predicate matches
     */
    public ConditionalHandlerBuilder<In, CurrentType, MutableConstructionStep<In, CurrentType, Out>> given(Predicate<CurrentType> predicate) {
        return new ConditionalHandlerBuilder<>(
                fallback -> new MutableConstructionStep<>(builder, getterWithFallback(predicate, fallback))
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