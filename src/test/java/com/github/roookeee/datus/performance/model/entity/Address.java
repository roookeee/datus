package com.github.roookeee.datus.performance.model.entity;

/*
The benchmark and all related data objects are derived variations of the following benchmark suite:
https://github.com/arey/java-object-mapper-benchmark

Special thanks to https://github.com/arey for his work
*/
public class Address {
    private String street;
    private String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}