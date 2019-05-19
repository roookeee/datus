package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class MutableMappingBuilderTest {

    @Test
    public void shouldAddSpyCorrectly() {
        List<String> spyList = new ArrayList<>();
        Mapper<String, String> mapper = new MutableMappingBuilder<String, String>(String::new)
                .spy((in, out) -> spyList.add(in))
                .from(Function.identity()).to((out, val) -> val.toUpperCase())
                .spy((in, out) -> spyList.add(out))
                .build();

        mapper.convert("input");

        assertThat(spyList).hasSize(2);
        assertThat(spyList.get(0))
                .withFailMessage("Spy should have been called with the initial value")
                .isEqualTo("input");
        assertThat(spyList.get(1))
                .withFailMessage("Spy should have been called with the transformed  value")
                .isEqualTo("INPUT");
    }

}
