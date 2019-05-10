package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.testutil.Person;
import com.github.roookeee.datus.api.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;

public class BasicMutableMappingTest {
    private static Mapper<Person, Person> mapper;
    private Person testPerson;

    @BeforeEach
    public void setup() {
        testPerson = new Person();
        testPerson.setAddress("address");
        testPerson.setName("name");
        testPerson.setLastName("lastname");
        testPerson.setBirthDate("birthday");

        mapper = new MutableMappingBuilder<Person, Person>(Person::new)
                .from(Person::getAddress).into(Person::setAddress)
                .from(Person::getName).into(Person::setName)
                .from(Person::getLastName).into(Person::setLastName)
                .from(Person::getBirthDate).into(Person::setBirthDate)
                .build();
    }

    @Test
    public void shouldWorkAsExpected() {
        Person result = mapper.convert(testPerson);

        assertThat("A new instance should have been created", result, not(sameInstance(testPerson)));
        assertThat(
                "The address of the mapped instance should be equal to its origin",
                result.getAddress(),
                is(testPerson.getAddress())
        );
        assertThat(
                "The name of the mapped instance should be equal to its origin",
                result.getName(),
                is(testPerson.getName())
        );
        assertThat(
                "The lastname of the mapped instance should be equal to its origin",
                result.getLastName(),
                is(testPerson.getLastName())
        );
        assertThat(
                "The birthdate of the mapped instance should be equal to its origin",
                result.getBirthDate(),
                is(testPerson.getBirthDate())
        );
    }
}
