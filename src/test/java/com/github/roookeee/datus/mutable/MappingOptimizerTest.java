package com.github.roookeee.datus.mutable;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingOptimizerTest {

    @Test
    public void shouldRetainOrderAndProduceWorkingCode() {
        for (List<String> parts : sublists(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"))) {
            //given
            List<BiFunction<String, String, String>> functions = parts.stream()
                    .map(this::toConcatStringFn)
                    .collect(Collectors.toList());

            //when
            BiFunction<String, String, String> function = MappingOptimizer.flattenAndOptimizeMappings(functions);
            String result = function.apply("", "");

            //then
            String expectedInOrder = String.join("", parts);
            assertThat(result).isEqualTo(expectedInOrder);
        }

    }

    private List<List<String>> sublists(List<String> list) {
        return IntStream.range(0, list.size() - 1)
                .mapToObj(i -> list.subList(0, list.size() - i))
                .collect(Collectors.toList());
    }

    private BiFunction<String, String, String> toConcatStringFn(String p) {
        return (a, b) -> b + p;
    }
}
