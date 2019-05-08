package datus.datamapper.immutable;

import datus.datamapper.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConstructorBuilderTest {

    @Test
    public void spyingShouldWorkCorrectly() {
        List<String> target = new ArrayList<>();

        ConstructorBuilder<String, String> constructorBuilder = new ConstructorBuilder<String, String>(s -> s)
                .spy((in, out) -> target.add(in));
        Mapper<String, String> mapper = constructorBuilder.build();

        String result = mapper.convert("Hello world!");

        assertThat("Spy should have capture value", target.get(0), is("Hello world!"));
    }

    @Test
    public void postProcessingShouldWorkCorrectly() {
        ConstructorBuilder<String, String> constructorBuilder = new ConstructorBuilder<String, String>(s -> s)
                .process((in, out) -> "process result");
        Mapper<String, String> mapper = constructorBuilder.build();

        String result = mapper.convert("Hello world!");

        assertThat("Spy should have capture value", result, is("process result"));
    }

}
