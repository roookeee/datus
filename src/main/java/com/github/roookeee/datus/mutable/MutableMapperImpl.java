package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * {@inheritDoc}
 */
class MutableMapperImpl<In, Out> implements Mapper<In, Out> {

    private final BiFunction<? super In, ? super Out, ? extends Out> mappingProcess;
    private final Supplier<? extends Out> generator;

    MutableMapperImpl(BiFunction<? super In, ? super Out, ? extends Out> mappingProcess, Supplier<? extends Out> generator) {
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