package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.conditional.ConditionalStart;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConstructorParameterBinding<In, Type, Ctor> {
    private final Ctor ctor;
    private final Function<In, Type> getter;

    ConstructorParameterBinding(Ctor ctor, Function<In, Type> getter) {
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
    public <IntermediateType> ConstructorParameterBinding<In, IntermediateType, Ctor> map(Function<Type, IntermediateType> mapper) {
        return new ConstructorParameterBinding<>(ctor, getter.andThen(mapper));
    }

    /**
     * Binds a parameter binding to its destination, most likely a {@link ConstructorParameter}.
     *
     * @param parameterBinder the function to pass the parameter binding too
     * @param <ResultType> the type of the result, either another parameter binding or the end of the constructor
     * @return the next step in the constructor binding process
     */
    public <ResultType> ResultType to(BiFunction<Ctor, Function<In, Type>, ResultType> parameterBinder) {
        return parameterBinder.apply(ctor, getter);
    }

    /**
     * Starts an adjustment-process for this parameter binding which is used when the given predicate matches (e.g. a fallback)
     *
     * @param predicate the predicate to use
     * @return a builer to configure the handling mechanism when the given predicate matches
     */
    public ConditionalStart<In, Type, ConstructorParameterBinding<In, Type, Ctor>> given(
            Predicate<Type> predicate
    ) {
        return new ConditionalStart<>(
                this,
                predicate,
                this::weaveConditional
        );
    }

    private ConstructorParameterBinding<In, Type, Ctor> weaveConditional(
            ConstructorParameterBinding<In, Type, Ctor> base,
            Predicate<Type> predicate,
            BiFunction<In, Type, Type> matching,
            BiFunction<In, Type, Type> orElse
    ) {
        return new ConstructorParameterBinding<>(
                ctor,
                base.getterWithPredicateHandler(predicate, matching, orElse)
        );
    }

    private Function<In, Type> getterWithPredicateHandler(
            Predicate<Type> predicate,
            BiFunction<In, Type, Type> matching,
            BiFunction<In, Type, Type> orElse
    ) {
        return in -> {
            Type value = getter.apply(in);
            if (predicate.test(value)) {
                return matching.apply(in, value);
            }
            return orElse.apply(in, value);
        };
    }
}
