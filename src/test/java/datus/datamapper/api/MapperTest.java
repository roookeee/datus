package datus.datamapper.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class MapperTest {

    @Test
    public void singleConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        String result = idMapper.convert("Hello World!");

        assertThat("Result should have matched input", result, is("Hello World!"));
    }

    @Test
    public void collectionConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        List<String> result = idMapper.convert(Arrays.asList("Hello", "World", "!"));

        assertThat("result length should have matched input length", result, hasSize(3));
        assertThat("result[0] should have matched input", result.get(0), is("Hello"));
        assertThat("result[1] should have matched input", result.get(1), is("World"));
        assertThat("result[2] should have matched input", result.get(2), is("!"));
    }

    @Test
    public void collectionToMapConversionShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;

        Map<String, String> result = idMapper.convertToMap(Arrays.asList("Hello", "World", "!"));

        assertThat("result length should have matched input length", result.keySet(), hasSize(3));
        assertThat("result['Hello'] should have matched input", result.get("Hello"), is("Hello"));
        assertThat("result['World'] should have matched input", result.get("World"), is("World"));
        assertThat("result['!'] should have matched input", result.get("!"), is("!"));
    }

    @Test
    public void streamingConversionShouldWorkAsExpected() {
        AtomicInteger callCount = new AtomicInteger();
        Mapper<String, String> countingMapper = s -> {
            callCount.incrementAndGet();
            return s;
        };

        Stream<String> resultStream = countingMapper.conversionStream(Arrays.asList("Hello", "World", "!"));

        assertThat(
                "should not have convert any objects when the stream was not (at least partially) consumed",
                callCount.get(),
                is(0)
        );

        List<String> result = resultStream.collect(Collectors.toList());
        assertThat("result length should have matched input length", result, hasSize(3));
        assertThat("result[0] should have matched input", result.get(0), is("Hello"));
        assertThat("result[1] should have matched input", result.get(1), is("World"));
        assertThat("result[2] should have matched input", result.get(2), is("!"));
    }

    @Test
    public void anInPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> neverMapper = idMapper.predicateInput(s -> false);
        Mapper<String, Optional<String>> alwaysMapper = idMapper.predicateInput(s -> true);

        Optional<String> noResult = neverMapper.convert("Hello World!");
        Optional<String> withResult = alwaysMapper.convert("Hello World!");

        assertThat("Result not have produced a result", noResult.isPresent(), is(false));
        assertThat("Result have produced a result", withResult.isPresent(), is(true));
    }

    @Test
    public void anOutPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> mapper = idMapper.predicateOutput(s -> !s.isEmpty());

        Optional<String> noResult = mapper.convert("");
        Optional<String> withResult = mapper.convert("Hello world!");

        assertThat("Result not have produced a result", noResult.isPresent(), is(false));
        assertThat("Result have produced a result", withResult.isPresent(), is(true));
    }

    @Test
    public void anInOutPredicationOfAMapperShouldWorkAsExpected() {
        Mapper<String, String> idMapper = s -> s;
        Mapper<String, Optional<String>> mapper = idMapper.predicate(s -> !s.isEmpty(), s -> s.length() > 1);

        Optional<String> noResultBecauseOfInPredicate = mapper.convert("");
        Optional<String> noResultBecauseOfOutPredicate = mapper.convert("0");
        Optional<String> withResult = mapper.convert("01");

        assertThat("Result not have produced a result", noResultBecauseOfInPredicate.isPresent(), is(false));
        assertThat("Result not have produced a result", noResultBecauseOfOutPredicate.isPresent(), is(false));
        assertThat("Result have produced a result", withResult.isPresent(), is(true));
    }
}
