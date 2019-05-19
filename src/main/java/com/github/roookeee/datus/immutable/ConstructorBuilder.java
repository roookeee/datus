package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.functions.Fn1;
import com.github.roookeee.datus.mutable.MutableMappingBuilder;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ConstructorBuilder<In, Out> {
    private Fn1<In, Out> constructor;

    ConstructorBuilder(Fn1<In, Out> constructor) {
        this.constructor = constructor;
    }

    /**
     * Adds a processing step to the construction process defined by this constructor builder (e.g. handling rare special cases).
     * <p>
     * Note: this processing step is executed after the original constructor has run as the output types object only
     * exists after construction (in contrast to the {@link MutableMappingBuilder#process(BiFunction)}.
     *
     * @param processor the function to apply
     * @return a new constructor builder including the given post-processing
     */
    public ConstructorBuilder<In, Out> process(BiFunction<In, Out, Out> processor) {
        return new ConstructorBuilder<>(
                in -> processor.apply(in, constructor.apply(in))
        );
    }

    /**
     * Adds a spy step to the construction process defined by this constructor builder (e.g. logging).
     * <p>
     * Note: this processing step is executed after the original constructor has run as the output types object only
     * exists after construction (in contrast to the {@link MutableMappingBuilder#process(BiFunction)}.
     *
     * @param consumer the function to apply
     * @return a new constructor builder including the given spy-processing
     */
    public ConstructorBuilder<In, Out> spy(BiConsumer<In, Out> consumer) {
        return new ConstructorBuilder<>(
                in -> {
                    Out result = constructor.apply(in);
                    consumer.accept(in, result);
                    return result;
                }
        );
    }

    /**
     * Constructs a mapper out of this constructor builder.
     *
     * @return said mapper
     */
    public Mapper<In, Out> build() {
        return new ImmutableMapperImpl<>(constructor);
    }
}
