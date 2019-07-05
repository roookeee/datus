package com.github.roookeee.plainjava.person.boundary;

public class PersonResource {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final boolean active;
    private final boolean receiveNewsletter;

    public PersonResource(Long id, String firstName, String lastName, boolean active, boolean receiveNewsletter) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.receiveNewsletter = receiveNewsletter;
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

    public boolean isReceiveNewsletter() {
        return receiveNewsletter;
    }

    @Override
    public String toString() {
        return "PersonResource{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                ", receiveNewsletter=" + receiveNewsletter +
                "}\n";
    }
}
