package com.github.roookeee.datus.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MapperTest {

    @Test
    public void singleConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        String result = idMapper.convert("Hello World!");

        assertThat(result).isEqualTo("Hello World!");
    }

    @Test
    public void collectionConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        List<String> result = idMapper.convert(Arrays.asList("Hello", "World", "!"));

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("Hello");
        assertThat(result.get(1)).isEqualTo("World");
        assertThat(result.get(2)).isEqualTo("!");
    }

    @Test
    public void collectionToMapConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        Map<String, String> result = idMapper.convertToMap(Arrays.asList("Hello", "World", "!"));

        assertThat(result.keySet()).hasSize(3);
        assertThat(result.get("Hello")).isEqualTo("Hello");
        assertThat(result.get("World")).isEqualTo("World");
        assertThat(result.get("!")).isEqualTo("!");
    }

    @Test
    public void streamingConversionShouldWorkAsExpected() {
        AtomicInteger callCount = new AtomicInteger();
        Mapper<String, String> countingMapper = s -> {
            callCount.incrementAndGet();
            return s;
        };

        Stream<String> resultStream = countingMapper.conversionStream(Arrays.asList("Hello", "World", "!"));

        assertThat(callCount.get())
                .withFailMessage("should not have convert any objects when the stream was not (at least partially) consumed")
                .isEqualTo(0);

        List<String> result = resultStream.collect(Collectors.toList());
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("Hello");
        assertThat(result.get(1)).isEqualTo("World");
        assertThat(result.get(2)).isEqualTo("!");
    }

    @Test
    public void anInPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> neverMapper = idMapper.predicateInput(s -> false);
        Mapper<String, Optional<String>> alwaysMapper = idMapper.predicateInput(s -> true);

        Optional<String> noResult = neverMapper.convert("Hello World!");
        Optional<String> withResult = alwaysMapper.convert("Hello World!");

        assertThat(noResult.isPresent()).isEqualTo(false);
        assertThat(withResult.isPresent()).isEqualTo(true);
    }

    @Test
    public void anOutPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> mapper = idMapper.predicateOutput(s -> !s.isEmpty());

        Optional<String> noResult = mapper.convert("");
        Optional<String> withResult = mapper.convert("Hello world!");

        assertThat(noResult.isPresent()).isEqualTo(false);
        assertThat(withResult.isPresent()).isEqualTo(true);
    }

    @Test
    public void anInOutPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> mapper = idMapper.predicate(s -> !s.isEmpty(), s -> s.length() > 1);

        Optional<String> noResultBecauseOfInPredicate = mapper.convert("");
        Optional<String> noResultBecauseOfOutPredicate = mapper.convert("0");
        Optional<String> withResult = mapper.convert("01");

        assertThat(noResultBecauseOfInPredicate.isPresent()).isEqualTo(false);
        assertThat(noResultBecauseOfOutPredicate.isPresent()).isEqualTo(false);
        assertThat(withResult.isPresent()).isEqualTo(true);
    }
}
