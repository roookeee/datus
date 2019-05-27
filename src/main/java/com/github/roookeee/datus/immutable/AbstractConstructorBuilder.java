package com.github.roookeee.datus.immutable;

import java.util.function.Function;

abstract class AbstractConstructorBuilder<In, T extends AbstractConstructorBuilder<In,T>> {

    /**
     * Helper function to return the correct type when using recursive generic type definitions
     * @return always "this" in the extending class
     */
    abstract T getSelf();

    /**
     * Starts a parameter binding process for the first parameter of the contained constructor
     * starting from the provided getter of the input type.
     *
     * @param <IntermediateType> the getters return type
     * @param getter the getter to start the parameter binding process from
     * @return a parameter binding process to bind the constructors first parameter
     */
    public <IntermediateType> ConstructorParameterBinding<In, IntermediateType, T> from(
            Function<? super In, ? extends IntermediateType> getter
    ) {
        return new ConstructorParameterBinding<>(getSelf(), getter);
    }

}
