package com.github.roookeee.datus.api;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Defines a mapping process for a given input type to a given output type.
 *
 * @param <In>  the type of the input object(s)
 * @param <Out> the type of the output object(s)
 */
public interface Mapper<In, Out> {

    /**
     * Converts a given input instance to an output instance
     *
     * @param input the input instance to convert
     * @return the converted output object
     */
    Out convert(In input);

    /**
     * Converts a given collection of input instances to a list of output instances
     * (retains order).
     *
     * @param input the collection of input instances to convert
     * @return a list containing the converted output instances
     */
    default List<Out> convert(Collection<In> input) {
        List<Out> result = new ArrayList<>(input.size());
        for (In in : input) {
            result.add(convert(in));
        }
        return result;
    }

    /**
     * Converts a given collection of input instances to a map relating every input instance
     * to the converted output instance.
     *
     * Any key collisions (e.g. duplicate objects in the input collection) will retain the last
     * processed item (last as defined by the collections iterator). Use {@link #conversionStream(Collection)} with
     * any overload of {@link java.util.stream.Collectors#toMap} if more fine grained control is needed (e.g. use a
     * plain {@link java.util.HashMap} or use the first mapped value on key collisions).
     *
     * @param input the collection of input instances to convert
     * @return a map containing all (input,output) tuples
     */
    default LinkedHashMap<In, Out> convertToMap(Collection<In> input) {
        LinkedHashMap<In, Out> result = new LinkedHashMap<>();
        for (In in : input) {
            result.put(in, convert(in));
        }
        return result;
    }

    /**
     * Converts a given collection of input instances to a map by using the given keyFunction to relate every input instance
     * to the converted output instance.
     *
     * Any key collisions (e.g. duplicate objects in the input collection) will retain the last
     * processed item (last as defined by the collections iterator). Use {@link #conversionStream(Collection)} with
     * any overload of {@link java.util.stream.Collectors#toMap} if more fine grained control is needed (e.g. use a
     * plain {@link java.util.HashMap} or use the first mapped value on key collisions).
     *
     * @param <KeyType> the type the keyFunction returns
     * @param input the collection of input instances to convert
     * @param keyFunction the function to apply to every input instance to generate the corresponding map key
     * @return a map containing all (keyFunction(input),output) tuples
     */
    default <KeyType> LinkedHashMap<KeyType, Out> convertToMap(Collection<In> input, Function<In, KeyType> keyFunction) {
        LinkedHashMap<KeyType, Out> result = new LinkedHashMap<>();
        for (In in : input) {
            result.put(keyFunction.apply(in), convert(in));
        }
        return result;
    }

    /**
     * Creates a stream of converted output instances by the given input instance collection.
     *
     * @param input the collection of input instances to convert
     * @return a (lazy) stream of the converted output instances
     */
    default Stream<Out> conversionStream(Collection<In> input) {
        return input.stream().map(this::convert);
    }

    /**
     * Creates a new mapper that considers a given predicate before applying the conversion process of this mapper.
     *
     * @param predicate the predicate to consider when converting input instances
     * @return a new mapper that expresses the optionality of an output instance because of the given predicate
     */
    default Mapper<In, Optional<Out>> predicateInput(Predicate<In> predicate) {
        Mapper<In, Out> mapper = this;
        return input -> {
            if (!predicate.test(input)) {
                return Optional.empty();
            }
            return Optional.of(mapper.convert(input));
        };
    }

    /**
     * Creates a new mapper that considers a given predicate after applying the conversion process of this mapper.
     *
     * @param predicate the predicate to consider after converting input instances
     * @return a new mapper that expresses the optionality of an output instance because of the given predicate
     */
    default Mapper<In, Optional<Out>> predicateOutput(Predicate<Out> predicate) {
        Mapper<In, Out> mapper = this;
        return input -> {
            Out output = mapper.convert(input);
            if (!predicate.test(output)) {
                return Optional.empty();
            }
            return Optional.of(output);
        };
    }

    /**
     * Creates a new mapper that considers the given predicates before and after applying the conversion process of this mapper.
     *
     * @param inputPredicate  the predicate to consider before converting input instances
     * @param outputPredicate the predicate to consider after converting input instances
     * @return a new mapper that expresses the optionality of an output instance because of the given predicates
     */
    default Mapper<In, Optional<Out>> predicate(Predicate<In> inputPredicate, Predicate<Out> outputPredicate) {
        Mapper<In, Out> mapper = this;
        return input -> {
            if (!inputPredicate.test(input)) {
                return Optional.empty();
            }
            Out output = mapper.convert(input);
            if (!outputPredicate.test(output)) {
                return Optional.empty();
            }
            return Optional.of(output);
        };
    }
}