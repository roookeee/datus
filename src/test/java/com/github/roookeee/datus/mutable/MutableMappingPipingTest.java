package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.testutil.Person;
import com.github.roookeee.datus.api.Mapper;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MutableMappingPipingTest {
    @Test
    public void shouldConsiderPrePipePredicate() {
        //given
        Person personInvalid = new Person();
        Person personValid = new Person();
        personInvalid.setAddress(null);
        personValid.setAddress("address");

        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).when(Objects::isNull).fallback("").map(String::toUpperCase).into(Person::setAddress)
                .build();

        //when
        Person resultInvalidInput = mapper.convert(personInvalid);
        Person resultValidInput = mapper.convert(personValid);

        //then
        assertThat("address should not have been set, pre-map predicate was false", resultInvalidInput.getAddress(), is(""));
        assertThat("address should have been set, pre-map predicate was true", resultValidInput.getAddress(), is("ADDRESS"));
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
        assertThat("should have used the map", result.getAddress(), is("ADDRESS"));
    }

    @Test
    public void postPipePredicateShouldBeConsidered() {
        //given
        Person personInvalid = new Person();
        Person personValid = new Person();
        personInvalid.setAddress("");
        personValid.setAddress("address");

        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).map(String::toUpperCase).when(String::isEmpty).fallback("fallback").into(Person::setAddress)
                .build();

        //when
        Person resultInvalidInput = mapper.convert(personInvalid);
        Person resultValidInput = mapper.convert(personValid);

        //then
        assertThat("Should have considered post-map when and stopped", resultInvalidInput.getAddress(), is("fallback"));
        assertThat("Should have considered post-map when and proceeded", resultValidInput.getAddress(), is("ADDRESS"));
    }
}
