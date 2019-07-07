package com.github.roookeee.sample.spring;

import com.github.roookeee.sample.spring.person.boundary.PersonResourceDataSource;
import com.github.roookeee.sample.spring.person.control.PersonService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleSpringApplication.class, args);
	}

	/*
	Just some dummy bean to show that autowiring and the general application setup is correct.
	Prints the same data as the plain java example.
	 */
	@Bean
	public Void dummyBean(PersonResourceDataSource dataSource, PersonService personService) {
		System.out.printf("Data source resources: %s%n", dataSource.getAllPersons());
		System.out.println("--------------------------------------------");
		System.out.printf("All persons: %s%n", personService.getAllPersons());
		System.out.printf("All active persons: %s%n", personService.getAllActivePersons());
		System.out.printf("All active persons map: %s%n", personService.getAllActivePersonsMap());
		System.out.printf("All inactive persons: %s%n", personService.getAllInactivePersons());

		return null;
	}
}
