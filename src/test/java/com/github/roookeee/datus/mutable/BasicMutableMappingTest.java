package com.github.roookeee.datus.mutable;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void shouldWorkAsExpected() {
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
}
