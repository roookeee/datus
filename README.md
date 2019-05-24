#### Note: datus is still in a pre-release state and will be released at the end of may 2019

<img width="400px" src="https://raw.githubusercontent.com/roookeee/datus/assets/assets/datus-logo.png" >

*datus* enables you to define a mapping-process between two data-structures via a fluent functional API that allows for 
additional processing along the way.

Tired of writing the 100th factory class? Want to focus more on what to map, not how to implement it? Test coverage
feels unnecessary when you actually just copy values from a to b? *datus* has got you covered!

#### Overview
1. [Why use datus?](#why-use-datus)
2. [Examples](#examples)
3. [Conditional mapping](#conditional-mapping)
4. [Drawbacks and when not to use datus](#drawbacks-and-when-not-to-use-datus)
5. [When datus construction builders just won't fit](#when-datus-construction-builders-just-wont-fit)
6. [datus and dependency injection](#dependency-injection-eg-spring)
7. [User guide](#user-guide)
8. [Dependency information (Maven etc.)](#dependency-information)
9. [Development principles](#development-principles)
10. [Supporting datus development](#supporting-datus-development)

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
Two short examples are shown for the immutable and mutable API of *datus*. Please refer to the USAGE.md for an extensive guide on *datus*.
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

## Conditional mapping
There may be reasons why a given input cannot be converted to an output object or why a converted output object is invalid
which can be handled in the following way:
```java
Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class).mutable(PersonDTO::new)
         .from(Person::getFirstName).into(PersonDTO.setFirstName)
         .build();

Mapper<Person, Optional<PersonDTO>> predicatedMapper = mapper.predicateInput(Object::nonNull)
//or .predicateOutput / .predicate(inputPredicate, outputPredicate)
```

*datus* opts for an approach that uses pre- and post-conversion validation instead of allowing
to interrupt the conversion process at any point in time. This simplifies *datus* API and implementation
while also separating this (not directly to conversion related) concern out of *datus* core workflow.

## Drawbacks and when not to use *datus*
*datus* is an abstraction-layer which like all of its kind (e.g. guava, Spring etc.) comes at a certain performance cost that in some scenarios will not justify the outlined benefits of it.
*datus* is rigorously profiled while developing its features which results in the following advice:

If you map a massive amount of objects (**> 40000 objects / ms (millisecond) per thread on an i7 6700k**) whilst not having any computationally significant `.map`-steps you will suffer a performance loss of up to 70% compared to a traditional factory with imperative style mapping code.

Refer to the next section for a possible workaround for this situation or others where *datus* just won't fit.

## When *datus* construction builders just won't fit
Maybe you are using *datus* in your project but there is this one mapping definition that feels awkward or just
isn't possible to express with *datus* immutable / mutable builder APIs. Surely you can just build a factory for this case, but the inconsistency 
isn't feeling too well...

This is were the simplistic approach of the `Mapper`-interface comes in handy: just define your mapping process
via a simple lambda and you are already getting the extended functionality like `.predicate` and the automatic availability
of converting collections:
```java
Mapper<List<String>, List<String>> copyMapper = list -> new ArrayList<>(list);
Mapper<List<String>,Optional<List<String>>> optionalCopyMapper = copyMapper.predicateInput(Object::nonNull);
```

## Dependency injection (e.g. Spring)
*datus* has no explicit code to support dependency injection and its accompanying concepts but is **easily integrated into any dependency injection framework** (e.g Spring):
```java
@Configuration
public class MapperConfiguration {
    @Bean
    public Mapper<Person, PersonDTO> generatePersonMapper() {
        return Datus.forTypes(Person.class, PersonDTO.class).mutable(PersonDTO::new)
            .from(Person::getFirstName).into(PersonDTO.setFirstName)
            .build();
    } 
}

@Component
public class SomeClass {
    
    private final Mapper<Person, PersonDTO> personMapper;
    
    @Autowired
    public SomeClass(Mapper<Person, PersonDTO> personMapper) {
        this.personMapper = personMapper;
    }
}
```

## User guide
Please refer to the USAGE.md for a complete user guide as the readme only serves as a broad overview.

## Dependency information
Maven
```xml
<dependency>
  <groupId>com.github.roookeee</groupId>
  <artifactId>datus</artifactId>
  <version>0.9.3</version>
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
*datus* follows semantic versioning (see https://semver.org/)

#### Licensing
*datus* is licensed under The MIT License (MIT)

Copyright (c) 2019 Nico Heller

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Supporting datus development
Like datus ? Consider buying me a coffee :)

<a href="https://www.buymeacoffee.com/roookeee" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>