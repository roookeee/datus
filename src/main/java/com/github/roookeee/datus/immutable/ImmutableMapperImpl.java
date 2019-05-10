package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.functions.Fn1;

/**
 * {@inheritDoc}
 */
class ImmutableMapperImpl<In, Out> implements Mapper<In, Out> {
    private final Fn1<In, Out> constructor;

    ImmutableMapperImpl(Fn1<In, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Out convert(In input) {
        return constructor.apply(input);
    }
}
