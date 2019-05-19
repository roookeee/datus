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
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).mapTo(Function.identity())
                .build();

        //when
        Item source = new Item("1");
        Item result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    public void conditionalMappingWithValueFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("-1").proceed()
                .mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("-1");
    }

    @Test
    public void conditionalMappingWithSupplierFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then(() -> "-1").proceed()
                    .mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("-1");
    }

    @Test
    public void conditionalMappingWithFnFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).given("2"::equals).then((in,v) -> in.getId() + "0").proceed().mapTo(Function.identity())
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item("2");

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(bResult.getId()).isEqualTo("20");
    }

    @Test
    public void conditionalMappingWithValueOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("fallback").orElse("none-fallback")
                .mapTo(Function.identity())
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("");

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(bResult.getId()).isEqualTo("none-fallback");
    }

    @Test
    public void conditionalMappingWithSupplierOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("fallback").orElse(() -> "none-fallback")
                .mapTo(Function.identity())
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("");

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(bResult.getId()).isEqualTo("none-fallback");
    }

    @Test
    public void conditionalMappingWithFnOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("fallback").orElse(v -> v+"-data")
                .mapTo(Function.identity())
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("value");

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(bResult.getId()).isEqualTo("value-data");
    }

    @Test
    public void conditionalMappingWithBiFnOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("fallback").orElse((in,v) -> in.getClass().getSimpleName())
                .mapTo(Function.identity())
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("value");

        //when
        Item aResult = mapper.convert(aSource);
        Item bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(bResult.getId()).isEqualTo("Item");
    }

    @Test
    public void basicPipingShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId).map(i -> i + "1").mapTo(Function.identity())
                .build();

        Item source = new Item("0");

        //when
        Item result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("01");
    }

    @Test
    public void whenBeforeAndAfterPipingShouldWorkCorrectly() {
        //given
        Mapper<Item, Item> mapper = new ConstructorBuilder1<Item, String, Item>(Item::new)
                .from(Item::getId)
                    .given(Objects::isNull).then("-1").proceed()
                    .map(i -> i + "1")
                    .given("-11"::equals).then("error").proceed()
                .mapTo(Function.identity())
                .build();

        Item source = new Item(null);

        //when
        Item result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("error");
    }
}
