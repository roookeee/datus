package datus.datamapper.mutable;

import datus.datamapper.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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

        assertThat("Spy should have added two elements", spyList, hasSize(2));
        assertThat("Spy should have been called on initial value", spyList.get(0), is("input"));
        assertThat("Spy should have been called on transformed value", spyList.get(1), is("INPUT"));
    }

}
