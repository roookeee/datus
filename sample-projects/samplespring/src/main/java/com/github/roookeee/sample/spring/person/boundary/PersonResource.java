package com.github.roookeee.sample.spring.person.boundary;

public class PersonResource {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final boolean active;
    private final boolean acceptedNewsletter;

    public PersonResource(Long id, String firstName, String lastName, boolean active, boolean acceptedNewsletter) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.acceptedNewsletter = acceptedNewsletter;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean hasAcceptedNewsletter() {
        return acceptedNewsletter;
    }

    @Override
    public String toString() {
        return "PersonResource{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                ", acceptedNewsletter=" + acceptedNewsletter +
                "}\n";
    }
}
