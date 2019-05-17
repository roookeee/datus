package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Item;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BasicImmutableMappingTest {

    @Test
    public void basicMappingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).mapTo(Function.identity())
                .build();

        Item source = new Item("1");
        Item result = mapper.convert(source);

        assertThat("Should have mapped correctly", result.getId(), is("1"));
    }

    @Test
    public void conditionalMappingWithValueFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given(Objects::isNull).fallback("-1").mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat("Should have mapped correctly", aResult.getId(), is("1"));
        assertThat("Should have mapped correctly", bResult.getId(), is("-1"));
    }

    @Test
    public void conditionalMappingWithSupplierFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given(Objects::isNull).fallback(() -> "-1").mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat("Should have mapped correctly", aResult.getId(), is("1"));
        assertThat("Should have mapped correctly", bResult.getId(), is("-1"));
    }

    @Test
    public void conditionalMappingWithFnFallbackShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given("2"::equals).fallback(in -> in.getId() + "0").mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item("2");

        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat("Should have mapped correctly", aResult.getId(), is("1"));
        assertThat("Should have mapped correctly", bResult.getId(), is("20"));
    }

    @Test
    public void basicPipingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).map(i -> i + "1").mapTo(Function.identity())
                .build();

        Item source = new Item("0");
        Item result = mapper.convert(source);

        assertThat("Should have mapped correctly", result.getId(), is("01"));
    }

    @Test
    public void whenBeforeAndAfterPipingShouldWorkCorrectly() {
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                .given(Objects::isNull).fallback("-1")
                .map(i -> i + "1")
                .given("-11"::equals).fallback("error")
                .mapTo(Function.identity())
                .build();

        Item source = new Item(null);
        Item result = mapper.convert(source);

        assertThat("Should have mapped correctly", result.getId(), is("error"));
    }
}
