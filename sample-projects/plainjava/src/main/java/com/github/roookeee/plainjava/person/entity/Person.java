package com.github.roookeee.plainjava.person.entity;

public class Person {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final boolean isActive;
    private final boolean receiveNewsletter;

    public Person(Long id, String firstName, String lastName, boolean isActive, boolean receiveNewsletter) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
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

    public boolean shouldReceiveNewsletter() {
        return receiveNewsletter;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                ", shouldReceiveNewsletter=" + receiveNewsletter +
                "}\n";
    }
}
