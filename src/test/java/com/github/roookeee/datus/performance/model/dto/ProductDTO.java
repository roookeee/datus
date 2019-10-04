package com.github.roookeee.datus.performance.model.dto;

/*
The benchmark and all related data objects are derived variations of the following benchmark suite:
https://github.com/arey/java-object-mapper-benchmark

Special thanks to https://github.com/arey for his work
*/
public class ProductDTO {
    
	private String name;

    public ProductDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}