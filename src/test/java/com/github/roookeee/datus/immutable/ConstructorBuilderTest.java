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
                .spy((in, out) -> {
                    target.add(in);
                    target.add(out);
                });
        Mapper<String, String> mapper = constructorBuilder.build();

        //when
        String result = mapper.convert("Hello world!");

        //then
        assertThat(target).hasSize(2);
        assertThat(target.get(0)).isEqualTo("Hello world!");
        assertThat(target.get(1)).isEqualTo("Hello world!");

        assertThat(result).isEqualTo("Hello world!");
    }

    @Test
    public void postProcessingShouldWorkCorrectly() {
        //given
        List<String> target = new ArrayList<>();
        ConstructorBuilder<String, String> constructorBuilder = new ConstructorBuilder<String, String>(s -> s)
                .process((in, out) -> {
                    target.add(in);
                    target.add(out);
                    return "process result";
                });
        Mapper<String, String> mapper = constructorBuilder.build();

        //when
        String result = mapper.convert("Hello world!");

        //then
        assertThat(target).hasSize(2);
        assertThat(target.get(0)).isEqualTo("Hello world!");
        assertThat(target.get(1)).isEqualTo("Hello world!");

        assertThat(result).isEqualTo("process result");
    }

}
