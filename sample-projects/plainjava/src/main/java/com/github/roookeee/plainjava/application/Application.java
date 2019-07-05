package com.github.roookeee.plainjava.application;

import com.github.roookeee.plainjava.conversion.Converters;
import com.github.roookeee.plainjava.person.boundary.MockPersonResourceDataSource;
import com.github.roookeee.plainjava.person.boundary.PersonResourceDataSource;
import com.github.roookeee.plainjava.person.control.PersonService;

public class Application {
    public static void main(String[] args) {
        PersonResourceDataSource dataSource = new MockPersonResourceDataSource();
        PersonService personService = new PersonService(
                dataSource,
                Converters.STANDARD_PERSON_CONVERTER
        );

        System.out.printf("Data source resources: %s%n", dataSource.getAllPersons());
        System.out.println("--------------------------------------------");
        System.out.printf("All persons: %s%n", personService.getAllPersons());
        System.out.printf("All active persons: %s%n", personService.getAllActivePersons());
        System.out.printf("All active persons map: %s%n", personService.getAllActivePersonsMap());
        System.out.printf("All inactive persons: %s%n", personService.getAllInactivePersons());
    }
}
