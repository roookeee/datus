package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Person;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class MutableMappingPipingTest {
    @Test
    public void shouldConsiderPrePipePredicate() {
        //given
        Person personInvalid = new Person();
        Person personValid = new Person();
        personInvalid.setAddress(null);
        personValid.setAddress("address");

        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                .given(Objects::isNull, "").orElse(Function.identity())
                .map(String::toUpperCase)
                .into(Person::setAddress)
                .build();

        //when
        Person resultInvalidInput = mapper.convert(personInvalid);
        Person resultValidInput = mapper.convert(personValid);

        //then
        assertThat(resultInvalidInput.getAddress()).isEqualTo("");
        assertThat(resultValidInput.getAddress()).isEqualTo("ADDRESS");
    }

    @Test
    public void basicPipingShouldWork() {
        //given
        Person personInvalid = new Person();
        personInvalid.setAddress("address");

        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).map(String::toUpperCase).into(Person::setAddress)
                .build();

        //when
        Person result = mapper.convert(personInvalid);

        //then
        assertThat(result.getAddress()).isEqualTo("ADDRESS");
    }

    @Test
    public void postPipePredicateShouldBeConsidered() {
        //given
        Person personInvalid = new Person();
        Person personValid = new Person();
        personInvalid.setAddress("");
        personValid.setAddress("address");

        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).map(String::toUpperCase)
                .given(String::isEmpty, "fallback").orElse(Function.identity())
                .into(Person::setAddress)
                .build();

        //when
        Person resultInvalidInput = mapper.convert(personInvalid);
        Person resultValidInput = mapper.convert(personValid);

        //then
        assertThat(resultInvalidInput.getAddress()).isEqualTo("fallback");
        assertThat(resultValidInput.getAddress()).isEqualTo("ADDRESS");
    }
}
