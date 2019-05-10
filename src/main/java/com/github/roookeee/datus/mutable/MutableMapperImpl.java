package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * {@inheritDoc}
 */
class MutableMapperImpl<In, Out> implements Mapper<In, Out> {

    private final BiFunction<In, Out, Out> mappingProcess;
    private final Supplier<Out> generator;

    MutableMapperImpl(BiFunction<In, Out, Out> mappingProcess, Supplier<Out> generator) {
        this.mappingProcess = mappingProcess;
        this.generator = generator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Out convert(In input) {
        return mappingProcess.apply(input, generator.get());
    }
}