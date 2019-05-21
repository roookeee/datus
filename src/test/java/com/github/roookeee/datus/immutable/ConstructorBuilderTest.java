package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstructorBuilderTest {

    @Test
    public void spyingShouldWorkCorrectly() {
        //given
        List<String> target = new ArrayList<>();

        ConstructorBuilder<String, String> constructorBuilder = new ConstructorBuilder<String, String>(s -> s)
                .spy((in, out) -> target.add(in));
        Mapper<String, String> mapper = constructorBuilder.build();

        //when
        String result = mapper.convert("Hello world!");

        //then
        assertThat(target.get(0)).isEqualTo("Hello world!");
    }

    @Test
    public void postProcessingShouldWorkCorrectly() {
        //given
        ConstructorBuilder<String, String> constructorBuilder = new ConstructorBuilder<String, String>(s -> s)
                .process((in, out) -> "process result");
        Mapper<String, String> mapper = constructorBuilder.build();

        //when
        String result = mapper.convert("Hello world!");

        //then
        assertThat(result).isEqualTo("process result");
    }

}
