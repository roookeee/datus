package com.github.roookeee.plainjava.person.control;

import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.plainjava.person.boundary.PersonResource;
import com.github.roookeee.plainjava.person.boundary.PersonResourceDataSource;
import com.github.roookeee.plainjava.person.entity.Person;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PersonService {

    private final PersonResourceDataSource dataSource;
    private final Mapper<PersonResource, Person> converter;
    private final Mapper<PersonResource, Optional<Person>> onlyValidConverter;

    public PersonService(PersonResourceDataSource dataSource, Mapper<PersonResource, Person> converter) {
        this.dataSource = dataSource;
        this.converter = converter;
        this.onlyValidConverter =
                converter.predicateInput(resource -> resource.getId() != null && resource.isActive());
    }

    public List<Person> getAllPersons() {
        return converter.convert(dataSource.getAllPersons());
    }

    public List<Person> getAllActivePersons() {
        return onlyValidConverter.conversionStream(dataSource.getAllPersons())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Map<Long, Person> getAllActivePersonsMap() {
        return onlyValidConverter.conversionStream(dataSource.getAllPersons())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        Person::getId,
                        Function.identity()
                ));
    }

    public List<Person> getAllInactivePersons() {
        return converter.conversionStream(dataSource.getAllPersons())
                .filter(person -> !person.isActive())
                .collect(Collectors.toList());
    }
}
