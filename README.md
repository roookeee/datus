#### Note: datus is still in a pre-release state and will be released at the end of may 2019

<img width="400px" src="https://raw.githubusercontent.com/roookeee/datus/assets/assets/datus-logo.png" >

*datus* enables you to define a mapping-process between two data-structures via a fluent functional API that allows for 
additional processing along the way - even for immutable objects.

Tired of writing the 100th factory class? Want to focus more on what to map, not how to implement it? Test coverage
feels unnecessary when you actually just copy values from a to b? *datus* has got you covered!

#### Overview
1. [Why use datus?](#why-use-datus)
2. [Examples](#examples)
3. [User guide](#user-guide)
4. [Dependency information (Maven etc.)](#dependency-information)
5. [Development principles](#development-principles)
6. [Supporting datus development](#supporting-datus-development)

## Why use *datus*?
Using *datus* has the following benefits:
- no more 'dumb'/businesslogic-less `*Factory`-classes that you have to unit-test
- separating mapping-logic from business-logic of certain conversion steps (**single-responsibility**)*
- enabling your business-logic to only operate on parts of a data-structure instead of depending on the whole object (e.g. upper-casing a persons name in a `.map`-step) (**reducing dependencies**)
- programming against an `interface` instead of concrete classes (**cleaner dependencies**)
- focus on `what` to map, not `how` to do it (**functional-programming approach**)
- easily add logging or other cross-cutting-concerns via `spy` or `process`(see below for more information about the full API)
- compared to [Lombok](https://projectlombok.org/) and [Apache MapStruct](http://mapstruct.org/): no additional IDE or build system plugins are needed (**simplicity**)
- no black magic - you define what to map and how (compile-time checked), not some naming conventions, annotations or heuristics (**explicitness**)
- leave your POJO/data classes 'as is' - no need for annotations or any other modifications (**low coupling**)

Other minor benefits include:
- define the mapping process from `A -> B` and get `Collection<A> -> Collection<B>`, `Collection<A> -> Map<A, B>` and more for free
- no need to unit-test trivial but necessary logic (e.g. `null`-checking, which once fixed won't be a problem at the given location again)
- (subjectively) more self-documenting code

\* = when following the single responsibility pattern business logic (e.g. businessful `.map`-steps) should be extracted to a class instead of an inline lamda

## Examples
The following examples outline the general usage of both the immutable and mutable API of *datus*.
Please refer to the [USAGE.md](https://github.com/roookeee/datus/blob/master/USAGE.md) for an extensive guide on *datus*.
#### Immutable object API example
```java

class Person {
    //getters + constructor omitted for brevity
    private final String firstName;
    private final String lastName;
   
}

class PersonDTO {
    //getters omitted for brevity
    private final String firstName;
    private final String lastName;
    
    public PersonDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

//define a building process for each constructor parameter, step by step
//the immutable API defines constructor parameters in their declaration order
Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class).immutable(PersonDTO::new)
    .from(Person::getFirstName).to(ConstructorParameter::bind)
    .from(Person::getLastName)
        .given(Objects::nonNull, ln -> ln.toUpperCase()).orElse("fallback")
        .to(ConstructorParameter::bind)
    .build();
    
Person person = new Person();
person.setFirstName("firstName");
person.setLastName(null);
PersonDTO personDto = mapper.convert(person);
/*
    personDto = PersonDTO [
        firstName = "firstName",
        lastName = "fallback"
    ]
*/
person.setLastName("lastName");
personDto = mapper.convert(person);
/*
    personDto = PersonDTO [
        firstName = "firstName",
        lastName = "LASTNAME"
    ]
*/
```

#### Mutable object API example
```java
class Person {
    //getters + setters omitted for brevity
    private String firstName;
    private String lastName;
}

class PersonDTO {
    //getters + setters + empty constructor omitted for brevity
    private String firstName;
    private String lastName;
}

//the mutable API defines a mapping process by multiple getter-setter steps
Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class).mutable(PersonDTO::new)
    .from(Person::getFirstName).into(PersonDTO.setFirstName)
    .from(Person::getLastName)
        .given(Objects::nonNull, ln -> ln.toUpperCase()).orElse("fallback")
        .into(PersonDTO::setLastName)
    .from(/*...*/).into(/*...*/)
    .build();
    
Person person = new Person();
person.setFirstName("firstName");
person.setLastName(null);
PersonDTO personDto = mapper.convert(person);
/*
    personDto = PersonDTO [
        firstName = "firstName",
        lastName = "fallback"
    ]
*/
person.setLastName("lastName");
personDto = mapper.convert(person);
/*
    personDto = PersonDTO [
        firstName = "firstName",
        lastName = "LASTNAME"
    ]
*/
```
## User guide
Please refer to the [USAGE.md](https://github.com/roookeee/datus/blob/master/USAGE.md) for a complete user guide as the readme only serves as a broad overview.
The user guide is designed to take **at most 15 minutes** to get you covered on everything about *datus* and how to use it
in different scenarios.

## Dependency information
Maven
```xml
<dependency>
  <groupId>com.github.roookeee</groupId>
  <artifactId>datus</artifactId>
  <version>0.9.4</version>
</dependency>
```
or any other build system via Maven Central

[![Maven Central](https://img.shields.io/maven-central/v/com.github.roookeee/datus.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.roookeee%22%20AND%20a:%22datus%22)
## Development principles 
This section is about the core principles *datus* is developed with.

#### Branching
The `master` branch always matches the latest release of *datus* while the `develop` branch houses the next version of datus
that is still under development.

#### Semver
*datus* follows semantic versioning (see https://semver.org/) starting with the 1.0 release.

#### Licensing
*datus* is licensed under The MIT License (MIT)

Copyright (c) 2019 Nico Heller

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Supporting datus development
Like *datus* ? Consider buying me a coffee :)

<a href="https://www.buymeacoffee.com/roookeee" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>