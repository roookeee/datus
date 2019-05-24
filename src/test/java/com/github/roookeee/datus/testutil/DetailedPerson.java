package com.github.roookeee.datus.testutil;

public class DetailedPerson {
    private final String uniqueId;
    private final String salutation;
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String addressExtra;
    private final String city;
    private final String occupation;
    private final String country;
    private final String zipCode;
    private final boolean active;
    private final boolean allowedToLogin;

    public DetailedPerson(
            String uniqueId,
            String salutation,
            String firstName,
            String lastName,
            String address,
            String addressExtra,
            String city,
            String occupation,
            String country,
            String zipCode,
            boolean active,
            boolean allowedToLogin
    ) {
        this.uniqueId = uniqueId;
        this.salutation = salutation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.addressExtra = addressExtra;
        this.city = city;
        this.occupation = occupation;
        this.country = country;
        this.zipCode = zipCode;
        this.active = active;
        this.allowedToLogin = allowedToLogin;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSalutation() {
        return salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressExtra() {
        return addressExtra;
    }

    public String getCity() {
        return city;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getCountry() {
        return country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAllowedToLogin() {
        return allowedToLogin;
    }
}