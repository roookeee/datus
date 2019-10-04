package com.github.roookeee.datus.performance.model.entity;

/*
The benchmark and all related data objects are derived variations of the following benchmark suite:
https://github.com/arey/java-object-mapper-benchmark

Special thanks to https://github.com/arey for his work
*/
public class Customer {
    private String name;
    private Address shippingAddress;
    private Address billingAddress;

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}