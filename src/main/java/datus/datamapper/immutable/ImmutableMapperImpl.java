package datus.datamapper.immutable;

import datus.datamapper.api.Mapper;
import datus.datamapper.functions.Fn1;

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
