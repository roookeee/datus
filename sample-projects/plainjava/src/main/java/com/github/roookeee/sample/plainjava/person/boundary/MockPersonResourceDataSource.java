package com.github.roookeee.sample.plainjava.person.boundary;

import java.util.Arrays;
import java.util.List;

public class MockPersonResourceDataSource implements PersonResourceDataSource {

    public List<PersonResource> getAllPersons() {
        return Arrays.asList(
                new PersonResource(1L, "Tim", "ActiveNoNewsletter", true, false),
                new PersonResource(2L, "Max", "NotActiveNoNewsletter", false, false),
                new PersonResource(3L, "Jane", "NotActiveWithNewsletter", false, true),
                new PersonResource(4L, "Lisa", "ActiveWithNewsletter", true, true),
                new PersonResource(null, "Anna", "NoIdActiveWithNewsletter", true, true),
                new PersonResource(null, "", "IdMissingFirstNameEmpty", true, true)
        );
    }
}
