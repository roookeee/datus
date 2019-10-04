package com.github.roookeee.datus.performance.model.entity;

import java.util.ArrayList;
import java.util.List;

/*
The benchmark and all related data objects are derived variations of the following benchmark suite:
https://github.com/arey/java-object-mapper-benchmark

Special thanks to https://github.com/arey for his work
*/
public class OrderFactory {

    public static Order buildOrder() {
        Order order = new Order();
        Customer customer = new Customer();
        order.setCustomer(customer);
        customer.setName("Joe Smith");
        Address billingAddress = new Address();
        customer.setBillingAddress(billingAddress);
        billingAddress.setStreet("1234 Market Street");
        billingAddress.setCity("San Fran");
        Address shippingAddress = new Address();
        customer.setShippingAddress(shippingAddress);
        shippingAddress.setStreet("1234 West Townsend");
        shippingAddress.setCity("Boston");
        List<Product> products = new ArrayList<Product>(2);
        order.setProducts(products);
        products.add(new Product("socks"));
        products.add(new Product("shoes"));
        return order;
    }
}
