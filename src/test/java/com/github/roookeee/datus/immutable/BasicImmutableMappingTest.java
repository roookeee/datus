package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Item;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicImmutableMappingTest {

    @Test
    public void basicMappingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).mapTo(Function.identity())
                .build();

        Item source = new Item("1");
        Item result = mapper.convert(source);

        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    public void conditionalMappingWithValueFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given(Objects::isNull).then("-1").proceed().mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("-1");
    }

    @Test
    public void conditionalMappingWithSupplierFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given(Objects::isNull).then(() -> "-1").proceed().mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("-1");
    }

    @Test
    public void conditionalMappingWithFnFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given("2"::equals).then(in -> in.getId() + "0").proceed().mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item("2");

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("20");
    }

    @Test
    public void basicPipingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).map(i -> i + "1").mapTo(Function.identity())
                .build();

        Item source = new Item("0");
        Item result = mapper.convert(source);

        assertThat(result.getId()).isEqualTo("01");
    }

    @Test
    public void whenBeforeAndAfterPipingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                .given(Objects::isNull).then("-1").proceed()
                .map(i -> i + "1")
                .given("-11"::equals).then("error").proceed()
                .mapTo(Function.identity())
                .build();

        Item source = new Item(null);
        Item result = mapper.convert(source);

        assertThat(result.getId()).isEqualTo("error");
    }
}
