package com.github.roookeee.sample.spring.person.control;

import com.github.roookeee.sample.spring.person.boundary.PersonResource;
import org.springframework.stereotype.Component;

@Component
public class PersonNewsletterHelper {
    public boolean shouldReceiveNewsletter(PersonResource resource) {
        return resource.isActive() && resource.hasAcceptedNewsletter();
    }
}
