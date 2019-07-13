package com.github.roookeee.sample.spring.conversion;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.immutable.ConstructorParameter;
import com.github.roookeee.sample.spring.person.boundary.PersonResource;
import com.github.roookeee.sample.spring.person.control.PersonNewsletterHelper;
import com.github.roookeee.sample.spring.person.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.function.Function;

@Configuration
public class Converters {

    @Bean
    public Mapper<PersonResource, Person> personStandardConverter(PersonNewsletterHelper personNewsletterHelper) {
        return Datus.forTypes(PersonResource.class, Person.class).immutable(Person::new)
                .from(PersonResource::getId)
                    .given(Objects::nonNull, Function.identity()).orElse(-1L)
                    .to(ConstructorParameter::bind)
                .from(PersonResource::getFirstName).nullsafe()
                    .given(String::isEmpty, "<missing>").orElse(Function.identity())
                    .to(ConstructorParameter::bind)
                .from(PersonResource::getLastName).to(ConstructorParameter::bind)
                .from(PersonResource::isActive).to(ConstructorParameter::bind)
                //consider the active flag for newsletter sending
                .from(Function.identity())
                    .map(resource -> resource.isActive() && resource.hasAcceptedNewsletter())
                    .to(ConstructorParameter::bind)
                .build();
    }
}
