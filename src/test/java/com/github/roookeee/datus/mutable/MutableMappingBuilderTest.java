package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class MutableMappingBuilderTest {

    @Test
    public void shouldAddSpyCorrectly() {
        //given
        List<String> spyList = new ArrayList<>();
        Mapper<String, String> uppercaseMapper = new MutableMappingBuilder<String, String>(String::new)
                .spy((in, out) -> {
                    spyList.add(in);
                    spyList.add(out);
                })
                .from(Function.identity()).to((out, val) ->val.toUpperCase())
                .spy((in, out) -> {
                    spyList.add(in);
                    spyList.add(out);
                })
                .build();

        //when
        String result = uppercaseMapper.convert("input");

        //then
        assertThat(result).isNotNull();
        assertThat(spyList).hasSize(4);
        assertThat(spyList.get(0))
                .withFailMessage("Spy should have been called with the input value")
                .isEqualTo("input");
        assertThat(spyList.get(1))
                .withFailMessage("Spy should have been called with the initial output value")
                .isEqualTo("");

        assertThat(spyList.get(2))
                .withFailMessage("Spy should have been called with the input value")
                .isEqualTo("input");
        assertThat(spyList.get(3))
                .withFailMessage("Spy should have been called with the transformed output value")
                .isEqualTo("INPUT");
    }


    @Test
    public void shouldProcessCorrectly() {
        //given
        List<String> spyList = new ArrayList<>();
        Mapper<String, String> prefixMapper = new MutableMappingBuilder<String, String>(() -> "prefix-")
                .from(Function.identity()).to((out,in) -> out+in)
                .process((in, out) -> {
                    spyList.add(in);
                    spyList.add(out);
                    return out+":processed";
                })
                .build();

        //when
        String result = prefixMapper.convert("input");

        //then
        assertThat(result).isEqualTo("prefix-input:processed");
        assertThat(spyList).hasSize(2);

        assertThat(spyList.get(0))
                .withFailMessage("Process should have been called with the input value")
                .isEqualTo("input");
        assertThat(spyList.get(1))
                .withFailMessage("Process should have been called with the transformed output value")
                .isEqualTo("prefix-input");
    }
}
