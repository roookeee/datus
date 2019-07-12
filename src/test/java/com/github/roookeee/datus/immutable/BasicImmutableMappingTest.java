package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Item;
import com.github.roookeee.datus.testutil.ItemDTO;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicImmutableMappingTest {

    @Test
    public void basicMappingShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId).map(Function.identity()).to(ConstructorParameter::bind)
                .from(Item::getId).map(id -> id+"-extra").to(ConstructorParameter::bind)
                .build();

        //when
        Item source = new Item("1");
        ItemDTO result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getExtendedId()).isEqualTo("1-extra");
    }

    @Test
    public void basicPipingShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                    .from(Item::getId).map(i -> i + "1").to(ConstructorParameter::bind)
                    .from(Item::getId).map(i -> i + "1").map(id -> id+"-extra").to(ConstructorParameter::bind)
                .build();

        Item source = new Item("0");

        //when
        ItemDTO result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("01");
        assertThat(result.getExtendedId()).isEqualTo("01-extra");
    }

    @Test
    public void conditionalMappingWithValueFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull, "-1").orElse(Function.identity())
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull, "-1").orElse(Function.identity())
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(aResult.getExtendedId()).isEqualTo("1-extra");

        assertThat(bResult.getId()).isEqualTo("-1");
        assertThat(bResult.getExtendedId()).isEqualTo("-1-extra");
    }

    @Test
    public void conditionalMappingWithSupplierFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                .given(Objects::isNull, () -> "-1").orElse(Function.identity())
                .to(ConstructorParameter::bind)
                .from(Item::getId)
                .given(Objects::isNull, () -> "-1").orElse(Function.identity())
                .map(id -> id+"-extra")
                .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(aResult.getExtendedId()).isEqualTo("1-extra");

        assertThat(bResult.getId()).isEqualTo("-1");
        assertThat(bResult.getExtendedId()).isEqualTo("-1-extra");
    }

    @Test
    public void conditionalMappingWithFnFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(id -> id.startsWith(" "), String::trim).orElse(Function.identity())
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                .given(id -> id.startsWith(" "), String::trim).orElse(Function.identity())
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item("1 ");
        Item bSource = new Item(" 1");

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1 ");
        assertThat(aResult.getExtendedId()).isEqualTo("1 -extra");

        assertThat(bResult.getId()).isEqualTo("1");
        assertThat(bResult.getExtendedId()).isEqualTo("1-extra");
    }

    @Test
    public void conditionalMappingWithBiFnFallbackShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull, (in,v) -> in.getClass().getSimpleName()).orElse(Function.identity())
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull, (in,v) -> in.getClass().getSimpleName()).orElse(Function.identity())
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item("1");
        Item bSource = new Item(null);

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("1");
        assertThat(aResult.getExtendedId()).isEqualTo("1-extra");

        assertThat(bResult.getId()).isEqualTo("Item");
        assertThat(bResult.getExtendedId()).isEqualTo("Item-extra");
    }

    @Test
    public void conditionalMappingWithValueOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull, "fallback").orElse("none-fallback")
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull,"fallback").orElse("none-fallback")
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("");

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(aResult.getExtendedId()).isEqualTo("fallback-extra");

        assertThat(bResult.getId()).isEqualTo("none-fallback");
        assertThat(bResult.getExtendedId()).isEqualTo("none-fallback-extra");
    }

    @Test
    public void conditionalMappingWithSupplierOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull,"fallback").orElse(() -> "none-fallback")
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull,"fallback").orElse(() -> "none-fallback")
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("");

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(aResult.getExtendedId()).isEqualTo("fallback-extra");

        assertThat(bResult.getId()).isEqualTo("none-fallback");
        assertThat(bResult.getExtendedId()).isEqualTo("none-fallback-extra");
    }

    @Test
    public void conditionalMappingWithFnOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull, "fallback").orElse(v -> v+"-data")
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull, "fallback").orElse(v -> v+"-data")
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("value");

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(aResult.getExtendedId()).isEqualTo("fallback-extra");

        assertThat(bResult.getId()).isEqualTo("value-data");
        assertThat(bResult.getExtendedId()).isEqualTo("value-data-extra");
    }

    @Test
    public void conditionalMappingWithBiFnOrElseShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull, "fallback").orElse((in,v) -> in.getClass().getSimpleName())
                    .to(ConstructorParameter::bind)
                .from(Item::getId)
                    .given(Objects::isNull, "fallback").orElse((in,v) -> in.getClass().getSimpleName())
                    .map(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();

        Item aSource = new Item(null);
        Item bSource = new Item("value");

        //when
        ItemDTO aResult = mapper.convert(aSource);
        ItemDTO bResult = mapper.convert(bSource);

        //then
        assertThat(aResult.getId()).isEqualTo("fallback");
        assertThat(aResult.getExtendedId()).isEqualTo("fallback-extra");

        assertThat(bResult.getId()).isEqualTo("Item");
        assertThat(bResult.getExtendedId()).isEqualTo("Item-extra");
    }

    @Test
    public void conditionalBeforeAndAfterPipingShouldWorkCorrectly() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(Objects::isNull,"-1").orElse(Function.identity())
                    .map(i -> i + "1")
                    .given("-11"::equals, "error").orElse(Function.identity())
                    .to(ConstructorParameter::bind)
                .from(Item::getId).to(ConstructorParameter::bind)
                .build();

        Item source = new Item(null);

        //when
        ItemDTO result = mapper.convert(source);

        //then
        assertThat(result.getId()).isEqualTo("error");
        assertThat(result.getExtendedId()).isNull();
    }

    @Test
    public void orElseNullShouldWokAsExpected() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId)
                    .given(id -> !id.isEmpty(), id -> id.toUpperCase()).orElseNull()
                    .to(ConstructorParameter::bind)
                .from(Item::getId).to(ConstructorParameter::bind)
                .build();

        //when
        Item source = new Item("");
        ItemDTO result = mapper.convert(source);

        //then
        assertThat(result.getId()).isNull();
        assertThat(result.getExtendedId()).isEqualTo("");
    }


    @Test
    public void nullsafeModeShouldWorkAsExpectedWithMap() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId).nullsafe().map(id -> id+"-normal").to(ConstructorParameter::bind)
                .from(Item::getId).nullsafe().map(id -> id+"-extra").to(ConstructorParameter::bind)
                .build();

        Item allNullItem = new Item(null);
        Item allValuesPresentItem = new Item("1");

        //when
        ItemDTO mappedAllNullItem = mapper.convert(allNullItem);
        ItemDTO mappedAllValuesPresentItem = mapper.convert(allValuesPresentItem);

        //then: null values should be propagated
        assertThat(mappedAllNullItem).isNotNull();
        assertThat(mappedAllNullItem.getId()).isNull();
        assertThat(mappedAllNullItem.getExtendedId()).isNull();

        //then: should have mapped upper-cased values
        assertThat(mappedAllValuesPresentItem).isNotNull();
        assertThat(mappedAllValuesPresentItem.getId()).isEqualTo(allValuesPresentItem.getId()+"-normal");
        assertThat(mappedAllValuesPresentItem.getExtendedId()).isEqualTo(allValuesPresentItem.getId()+"-extra");
    }

    @Test
    public void nullsafeModeShouldWorkAsExpectedWithGiven() {
        //given
        Mapper<Item, ItemDTO> mapper = Datus.forTypes(Item.class, ItemDTO.class)
                .immutable(ItemDTO::new)
                .from(Item::getId).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(id -> id+"-normal")
                    .to(ConstructorParameter::bind)
                .from(Item::getId).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(id -> id+"-extra")
                    .to(ConstructorParameter::bind)
                .build();


        Item allNullItem = new Item(null);
        Item allValuesDontMatchPredicate = new Item("1");
        Item allValuesMatchPredicate = new Item("");

        //when
        ItemDTO mappedAllNullItem = mapper.convert(allNullItem);
        ItemDTO mappedAllValuesDontMatchPredicate = mapper.convert(allValuesDontMatchPredicate);
        ItemDTO mappedAllValuesMatchPredicate = mapper.convert(allValuesMatchPredicate);

        //then: null values should be propagated
        assertThat(mappedAllNullItem).isNotNull();
        assertThat(mappedAllNullItem.getId()).isNull();
        assertThat(mappedAllNullItem.getExtendedId()).isNull();

        //then: should have mapped with custom logic
        assertThat(mappedAllValuesDontMatchPredicate).isNotNull();
        assertThat(mappedAllValuesDontMatchPredicate.getId()).isEqualTo(allValuesDontMatchPredicate.getId()+"-normal");
        assertThat(mappedAllValuesDontMatchPredicate.getExtendedId()).isEqualTo(allValuesDontMatchPredicate.getId()+"-extra");

        //then: values matching the predicate should use the fallback
        assertThat(mappedAllValuesMatchPredicate).isNotNull();
        assertThat(mappedAllValuesMatchPredicate.getId()).isEqualTo("fallback");
        assertThat(mappedAllValuesMatchPredicate.getExtendedId()).isEqualTo("fallback");
    }
}
