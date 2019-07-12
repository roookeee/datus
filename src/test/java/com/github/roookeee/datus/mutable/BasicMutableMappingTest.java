package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicMutableMappingTest {
    private Person testPerson;

    @BeforeEach
    public void setup() {
        testPerson = new Person();
        testPerson.setAddress("address");
        testPerson.setName("name");
        testPerson.setLastName("lastname");
        testPerson.setBirthDate("birthday");
    }

    @Test
    public void basicUsageShouldWorkAsExpected() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).into(Person::setAddress)
                .from(Person::getName).into(Person::setName)
                .from(Person::getLastName).into(Person::setLastName)
                .from(Person::getBirthDate).into(Person::setBirthDate)
                .build();

        //when
        Person result = mapper.convert(testPerson);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo(testPerson.getAddress());
        assertThat(result.getName()).isEqualTo(testPerson.getName());
        assertThat(result.getLastName()).isEqualTo(testPerson.getLastName());
        assertThat(result.getBirthDate()).isEqualTo(testPerson.getBirthDate());
    }

    @Test
    public void basicConditionalUsageShouldWorkAsExpectedWithValue() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                    .given(Objects::isNull, "fallback-address").orElse(Function.identity())
                    .into(Person::setAddress)
                .build();
        Person person = new Person();
        person.setAddress(null);

        //when
        Person result = mapper.convert(person);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo("fallback-address");
    }

    @Test
    public void basicConditionalUsageShouldWorkAsExpectedWithSupplier() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                .given(Objects::isNull, () -> "fallback-address").orElse(Function.identity())
                .into(Person::setAddress)
                .build();
        Person person = new Person();
        person.setAddress(null);

        //when
        Person result = mapper.convert(person);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo("fallback-address");
    }

    @Test
    public void basicConditionalUsageShouldWorkAsExpectedWithFn() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                .given("address"::equals, address -> address+"-extras").orElse(Function.identity())
                .into(Person::setAddress)
                .build();
        Person person = new Person();
        person.setAddress("address");

        //when
        Person result = mapper.convert(person);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo("address-extras");
    }

    @Test
    public void basicTwoCasedConditionalUsageShouldWorkAsExpected() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                    .given(Objects::isNull, "fallback-address").orElse(addr -> addr.toUpperCase())
                .into(Person::setAddress)
                .build();
        Person person = new Person();
        person.setAddress("address");

        //when
        Person result = mapper.convert(person);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo("ADDRESS");
    }

    @Test
    public void orElseNullShouldWokAsExpected() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                .given(address -> !address.isEmpty(), address -> address.toUpperCase()).orElseNull()
                .into(Person::setAddress)
                .build();
        Person person = new Person();
        person.setAddress("");

        //when
        Person result = mapper.convert(person);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isNull();
    }

    @Test
    public void nullsafeModeShouldWorkAsExpectedWithMap() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).nullsafe().map(s -> s.toUpperCase()).into(Person::setAddress)
                .from(Person::getName).nullsafe().map(s -> s.toUpperCase()).into(Person::setName)
                .from(Person::getLastName).nullsafe().map(s -> s.toUpperCase()).into(Person::setLastName)
                .from(Person::getBirthDate).nullsafe().map(s -> s.toUpperCase()).into(Person::setBirthDate)
                .build();
        Person allNullPerson = new Person();
        Person allValuesPresentPerson = testPerson;

        //when
        Person mappedAllNullperson = mapper.convert(allNullPerson);
        Person mappedAllValuesPresentperson = mapper.convert(allValuesPresentPerson);

        //then: null values should be propagated
        assertThat(mappedAllNullperson).isNotNull();
        assertThat(mappedAllNullperson.getAddress()).isNull();
        assertThat(mappedAllNullperson.getName()).isNull();
        assertThat(mappedAllNullperson.getLastName()).isNull();
        assertThat(mappedAllNullperson.getBirthDate()).isNull();

        //then: should have mapped upper-cased values
        assertThat(mappedAllValuesPresentperson).isNotNull();
        assertThat(mappedAllValuesPresentperson.getAddress()).isEqualTo(testPerson.getAddress().toUpperCase());
        assertThat(mappedAllValuesPresentperson.getName()).isEqualTo(testPerson.getName().toUpperCase());
        assertThat(mappedAllValuesPresentperson.getLastName()).isEqualTo(testPerson.getLastName().toUpperCase());
        assertThat(mappedAllValuesPresentperson.getBirthDate()).isEqualTo(testPerson.getBirthDate().toUpperCase());
    }

    @Test
    public void nullsafeModeShouldWorkAsExpectedWithGiven() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(Function.identity())
                    .into(Person::setAddress)
                .from(Person::getName).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(Function.identity())
                    .into(Person::setName)
                .from(Person::getLastName).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(Function.identity())
                    .into(Person::setLastName)
                .from(Person::getBirthDate).nullsafe()
                    .given(String::isEmpty, "fallback").orElse(Function.identity())
                    .into(Person::setBirthDate)
                .build();

        Person allNullPerson = new Person();
        Person allValuesDontMatchPredicate = testPerson;

        Person allValuesMatchPredicate = new Person();
        allValuesMatchPredicate.setAddress("");
        allValuesMatchPredicate.setName("");
        allValuesMatchPredicate.setLastName("");
        allValuesMatchPredicate.setBirthDate("");

        //when
        Person mappedAllNullperson = mapper.convert(allNullPerson);
        Person mappedAllValuesDontMatchPredicate = mapper.convert(allValuesDontMatchPredicate);
        Person mappedAllValuesMatchPredicate = mapper.convert(allValuesMatchPredicate);

        //then: null values should be propagated
        assertThat(mappedAllNullperson).isNotNull();
        assertThat(mappedAllNullperson.getAddress()).isNull();
        assertThat(mappedAllNullperson.getName()).isNull();
        assertThat(mappedAllNullperson.getLastName()).isNull();
        assertThat(mappedAllNullperson.getBirthDate()).isNull();

        //then: values not matching the predicates should be forwarded
        assertThat(mappedAllValuesDontMatchPredicate).isNotNull();
        assertThat(mappedAllValuesDontMatchPredicate.getAddress()).isEqualTo(testPerson.getAddress());
        assertThat(mappedAllValuesDontMatchPredicate.getName()).isEqualTo(testPerson.getName());
        assertThat(mappedAllValuesDontMatchPredicate.getLastName()).isEqualTo(testPerson.getLastName());
        assertThat(mappedAllValuesDontMatchPredicate.getBirthDate()).isEqualTo(testPerson.getBirthDate());

        //then: values matching the predicates should use the fallback
        assertThat(mappedAllValuesMatchPredicate).isNotNull();
        assertThat(mappedAllValuesMatchPredicate.getAddress()).isEqualTo("fallback");
        assertThat(mappedAllValuesMatchPredicate.getName()).isEqualTo("fallback");
        assertThat(mappedAllValuesMatchPredicate.getLastName()).isEqualTo("fallback");
        assertThat(mappedAllValuesMatchPredicate.getBirthDate()).isEqualTo("fallback");
    }
}
