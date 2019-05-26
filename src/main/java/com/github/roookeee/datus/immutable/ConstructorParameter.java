package com.github.roookeee.datus.immutable;

import java.util.function.Function;

/**
 * A ConstructorParameter allows binding a constructor parameters value to a getter of a given input type.
 *
 * @param <In> the input type the constructor is being related to
 * @param <GetterReturnType> the getters return type to bind
 * @param <Result> the result type of the bind operation, usually another
 *                ConstructorParameter to bind the next constructor parameter
 */
public interface ConstructorParameter<In, GetterReturnType, Result> {
    /**
     * Binds the associated constructor parameter to the given function of the input type.
     *
     * @param getter the getter to bind to
     * @return the result of the bind operation, usually another
     *  *                ConstructorParameter to bind the next constructor parameter
     */
    Result bind(Function<? super In, GetterReturnType> getter);
}
