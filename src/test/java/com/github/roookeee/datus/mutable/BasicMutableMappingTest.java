package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

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
    public void basicConditionalUsageShouldWorkAsExpected() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                    .given(Objects::isNull).then("fallback-address").proceed()
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
    public void basicTwoCasedConditionalUsageShouldWorkAsExpected() {
        //given
        Mapper<Person, Person> mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress)
                    .given(Objects::isNull).then("fallback-address").orElse(addr -> addr.toUpperCase())
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
}
