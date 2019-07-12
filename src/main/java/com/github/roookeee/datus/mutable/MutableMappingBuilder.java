package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.shared.SafetyMode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The base class for defining a mutable mapping process from a given input type to a given output type
 * via getter -&gt; (optional processing) -&gt; setter chains.
 * <p>
 * All mapping steps added to this builder are guaranteed to be executed in the order they were specified.
 *
 * @param <In>  the input type
 * @param <Out> the output type
 */
public final class MutableMappingBuilder<In, Out> {
    private final List<BiFunction<? super In, ? super Out, ? extends Out>> mappers = new ArrayList<>();
    private final Supplier<? extends Out> generator;

    /**
     * Create a mutable mapping builder that uses the provided generator for its {@link Mapper} generation
     * as it needs a way to instantiate the output type.
     *
     * @param generator the generator to later instantiate a {@link Mapper} with
     */
    public MutableMappingBuilder(Supplier<? extends Out> generator) {
        this.generator = generator;
    }

    /**
     * Starts a construction step based on the passed getter which is then configured
     * by the returned {@link MutableConstructionStep} to be applied to an instance of an output type.
     *
     * @param getter the getter to base the construction step on
     * @param <A>    the getters return type
     * @return the construction step to further configure
     */
    public <A> MutableConstructionStep<In, A, Out> from(Function<? super In, ? extends A> getter) {
        return new MutableConstructionStep<>(this, getter, SafetyMode.NONE);
    }

    /**
     * Adds a processing step to the construction process defined by this builder (e.g. handling rare special cases).
     * <p>
     * Note: this processing step is executed in order meaning that any construction steps preceding are not affected
     * and all following construction steps are affected by it.
     *
     * @param processor the function to apply
     * @return the builder instance for chaining
     */
    public MutableMappingBuilder<In, Out> process(BiFunction<? super In, ? super Out, ? extends Out> processor) {
        mappers.add(processor);
        return this;
    }

    /**
     * Adds a spy step to the construction process defined by this builder (e.g. logging).
     * <p>
     * Note: this step is executed in order meaning that any construction steps following it are not "visible"
     * to the provided function.
     *
     * @param consumer the function to apply
     * @return the builder instance for chaining
     */
    public MutableMappingBuilder<In, Out> spy(BiConsumer<? super In, ? super Out> consumer) {
        process((in, out) -> {
            consumer.accept(in, out);
            return out;
        });
        return this;
    }

    /**
     * Generates a {@link Mapper} out of the construction process defined in this builder instance.
     *
     * @return a mapper instance representing the defined construction process
     */
    public Mapper<In, Out> build() {
        BiFunction<? super In, ? super Out, ? extends Out> mappingProcess = MappingOptimizer.flattenAndOptimizeMappings(mappers);
        return new MutableMapperImpl<>(
                mappingProcess,
                generator
        );
    }

    void addMapper(BiFunction<? super In, ? super Out, ? extends Out> mapper) {
        mappers.add(mapper);
    }
}
