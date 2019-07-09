package com.github.roookeee.sample.plainjava.conversion;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.immutable.ConstructorParameter;
import com.github.roookeee.sample.plainjava.person.boundary.PersonResource;
import com.github.roookeee.sample.plainjava.person.entity.Person;

import java.util.function.Function;

public class Converters {
    public static final Mapper<PersonResource, Person> STANDARD_PERSON_CONVERTER =
            Datus.forTypes(PersonResource.class, Person.class).immutable(Person::new)
                    .from(PersonResource::getId).to(ConstructorParameter::bind)
                    .from(PersonResource::getFirstName).to(ConstructorParameter::bind)
                    .from(PersonResource::getLastName).to(ConstructorParameter::bind)
                    .from(PersonResource::isActive).to(ConstructorParameter::bind)
                    //consider the active flag for newsletter sending
                    .from(Function.identity())
                        .map(resource -> resource.isActive() && resource.hasAcceptedNewsletter())
                        .to(ConstructorParameter::bind)
                    .build();

    private Converters() {

    }
}
