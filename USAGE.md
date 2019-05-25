## *datus* usage guide

This document is still under construction and will be filled before releasing *datus* 1.0.

I apologize for the current state of this document and would like to point you to the currently existing
JavaDoc in the source code.

### Overview
1. [Prerequisites](#prerequisites)
1. [Getting started](#getting-started)
1. [Basics](#basics)
1. [Immutable API](#immutable-api)
1. [Mutable API](#mutable-api)
1. [Examples](#examples)
1. [Advanced usage / FAQ](#advanced-usage--faq)

### Prerequisites 

*datus* requires at least basic knowledge about functional programming and fluent APIs. A basic understanding of Java 8 `Optional` and/or
`Stream` classes and their APIs (`map`, `filter`, `collect`, `orElse` etc.) should suffice to understand all concepts of *datus* that are needed to make use of its whole feature set.

Always consider if everyone on your team fulfills the outlined prerequisites before deciding to use *datus* for your project.

### Getting started
*datus* is available at Maven Central:
```xml
<dependency>
  <groupId>com.github.roookeee</groupId>
  <artifactId>datus</artifactId>
  <version>0.9.3</version>
</dependency>
```
[![Maven Central](https://img.shields.io/maven-central/v/com.github.roookeee/datus.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.roookeee%22%20AND%20a:%22datus%22)

*datus* supports any JDK (Hotspot, OpenJDK, J9 etc.) which supports Java 8 or newer.
### Basics
*datus* core workflow revolves around the `Datus` and `Mapper<Input,Output>` classes.

The `Datus` class serves as the main entry point for starting a mapping definition and provides both
the immutable and mutable API:

```java
Datus.forTypes(Person.class, PersonDTO.class)
```
The first step is to define which input and output type *datus* has to consider. Now it is time to choose
the immutable or mutable API of *datus*:
```java
Datus.forTypes(Person.class, PersonDTO.class).mutable(PersonDTO::new)
Datus.forTypes(Person.class, PersonDTO.class).immutable(PersonDTO::new)
```

The mutable API expects exactly zero constructor parameters whereas the immutable API supports
up to 12 constructor parameters (consider opening an issue if you need *datus* to support more constructor parameters).

Even though the two APIs internally differ significantly, *datus* strives to unify the experience of both workflows.
Fundamentally both the immutable and mutable API define their mapping steps on a field-by-field/parameter-by-parameter basis:
```java
.from(InputType::someGetter)
.map(Logic::someProcessing)
.given(Object::nonNull, String::trim).orElse("fallback")
.to(OutputType::someSetter OR ConstructorParameter::bind)
```
`from(Input::someGetter)`: The first step is to supply a data source from which the current mapping step receives its data. This data source is naturally 
related to the input type and most likely a simple getter (`InputType::someGetter` here).

`map(Logic::someProcessing)`: Mapping the value of the data-source is purely optional and can be chained as much as needed. 
`map` in *datus* is similar to `Optional.map` or `Stream.map` and may change the type of the
mapping step as needed.

`given(Object::nonNull, String::trim).orElse("fallback")`: Like `map`, using `given` is entirely optional. `given`
is similar to `map` but considers a given predicate to determine which function/supplier/value to use. As the type of the current 
mapping step may change through the provided function/supplier/value in the `given`-call an `orElse` is
mandatory to ensure both branches result in the same type (if the type does not change consider using `Function.identity()` in
cases where one branch should not modify the value in any way).

`to(OutputType::someSetter OR ConstructorParameter::bind)`: The `to/into` operations of *datus* finalize
the preceding mapping step definition by binding its definition to the current constructor parameter (immutable API)
or a given setter (mutable API). Any type conversion (e.g. an `Address` field in `Person` has to be transformed to an 
`AddressDTO` for the `PersonDTO`) has to happen in preceding `map` steps. A type mismatch will always result in a compilation error.

Once all necessary mapping steps are completed, calling `build()` will finalize the mapping definition and
generate a `Mapper<Input, Output>` object. Most features of the `Mapper`-Interface are about the conversion from input to output:
```java
//the only function that is actually implemented by the given mapping steps 
//all other functions are based on it:
Output convert(Input input) 
List<Output> convert(Collection<Input> input)
Stream<Output> conversionStream(Collection<Input> input)

Map<Input, Output> convert(Collection<Input> input)
Map<MapKeyType, Output> convert(Collection<Input> input, Function<Input, MapKeyType> keyMapper)
```
Other functions allow predicating a given `Mapper<Input, Output>` in regards to the input object (e.g. input must not be null), the generated output (e.g. some business-logic validation) object or both:
```java
Mapper<Input, Optional<Output>> predicateInput(Predicate<Input> predicate)
Mapper<Input, Optional<Output>> predicateOutput(Predicate<Output> predicate)
Mapper<Input, Optional<Output>> predicate(Predicate<Input> inputPredicate, Predicate<Output> outputPredicate)
```

Some last basic side notes on how to program with and what to expect from *datus* before moving on to more advanced topics:

Both the immutable and mutable API are statically type checked and thus won't compile if an invalid mapping
definition is given (e.g. type mismatches).
Finally as *datus* is about mapping an input object to an output object, **it is strongly discouraged to change the input
object in any way when defining a mapping process in one of *datus* APIs**.

### Immutable API

The immutable API of *datus* works by defining the mapping process of each constructor parameter in the order
they occur in their constructor definition. 
Every `.from(...).(...).to(ConstructorParameter::bind)` definition automatically moves to the next constructor parameter
until every constructor parameter is bound to a mapping process.

*datus* immutable API provides additional functionality once every constructor parameter received its mapping process:

`spy(BiConsumer<In, Out>)`: `spy` is used to notify a given function about a successfully applied mapping process. The main
use case of `spy` is logging or other cross-cutting concerns. It is strongly discouraged to change the input object in any way.

`process(BiFunction<Input, Output, Output>`: `process` enables additional post-processing after a given input object
has been converted to an output object. `process` should only be used when other facilities of *datus* won't suffice or
become too verbose. It is strongly discouraged to change the input object in any way.

Finally, a `build()`-call finishes the mapping process definition by generating a `Mapper<Input,Output>` which internally 
uses all preceding mapping definitions.

### Mutable API

The mutable API of *datus* works by defining a set of getter-setter chains. Every `.from(...).(...).to(Output::someSetter)` adds
a mapping definition to the later generated `Mapper<Input, Output>`. There are no checks for exhaustiveness or
duplicate mappings as there is no proper way to implement it (e.g. lambdas and/or function references have no reference
equality guarantees in the Java specification).

The mutable API offers two terminal operations to finalize a given mapping definition - `to` and `into`. `into` accepts a 
simple setter on the output type whereas `to` accepts a function which takes the current mapping definition and applies it to the 
later affected output object by returning a new instance of the output type. So why is `to` needed? Consider the following
setter:
```java
public Output setSomeStuff(String value) {
    return new Output(value);
}
```
`to` is needed/more elegant for cases in which setters return a new instance of the output type or for some reason return the
object for chaining. This is especially useful for [Kotlins data classes](https://kotlinlang.org/docs/reference/data-classes.html).
`into` would not suffice in this context as it does not allow to replace the whole output object or support functions which have a return value.

*datus* mutable API provides additional functions which are not directly related to mapping a single value from the input to the output
object:

`spy(BiConsumer<In, Out>)`: `spy` is used to notify a given function about the current state of the input and output objects. The main
use case of `spy` is logging or other cross-cutting concerns. Compared to the immutable API, `spy` can be inserted before and after
every mapping process definition. It is strongly discouraged to change the input object in any way.

`process(BiFunction<Input, Output, Output>`: `process` enables additional post-processing of a given output object. `process` should only be used when other facilities of *datus* won't suffice or
become too verbose. Compared to the immutable API, `process` can be inserted before and after every mapping process definition.
Extensive use of `process` is discouraged as it is hard to reason about what fields of the output object are affected
 (e.g. maybe it overrides a field for which you have just defined a mapping process). It is recommended to only use `process`
 after all the getter-setter chains have been defined (which clearly signals some form of post-processing).
 It is strongly discouraged to change the input object in any way.

Finally, a `build()`-call finishes the mapping process definition by generating a `Mapper<Input,Output>` which internally 
uses all preceding mapping definitions.

### Examples

This section shows some basic usage-scenarios for datus and most of its features. The following two simple objects
are the foundation of this section:
```java
class Person {
    //constructor + getters omitted for brevity
    private final String firstName;
    private final String lastName;
    private final boolean active;
    private final boolean canLogin;
}

class PersonDTO {
    //getters omitted for brevity
    private final String firstName;
    private final String lastName;
    private final boolean active;
    private final boolean canLogin;
    
    public PersonDTO(String firstName, String lastName, boolean active, boolean canLogin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.canLogin = canLogin;
    }
}
```
The following examples focus on the immutable API of *datus* but every `ConstructorParameter::bind` could be
directly replaced by a setter on `PersonDTO` to accomplish the same task in the mutable API without changing
anything else besides the initial `.immutable(PersonDTO::new)`-call.

Let's start with a simple copying mapper and some predicated variations:
```java
class MapperDefinitions {
    private Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class)
        .immutable(PersonDTO::new)
        .from(Person::getFirstName).to(ConstructorParameter::bind)
        .from(Person::getLastName).to(ConstructorParameter::bind)
        .from(Person::isActive).to(ConstructorParameter::bind)
        .from(Person::isCanLogin).to(ConstructorParameter::bind)
        .build();
    
    //let's not try to convert null inputs
    private Mapper<Person, Optional<PersonDTO>> inputCheckedMapper = 
        mapper.predicateInput(Object::nonNull);
    
    //let's not try to convert null inputs and only output active users
    private Mapper<Person, Optional<PersonDTO>> onlyActiveResults = 
        mapper.predicate(Object::nonNull, PersonDTO::isActive);
}
```
Let's assume a `PersonDTO's` `canLogin` respects the `Person's` `isActive` flag and a `Person's` `firstName` and `lastName`
may contain unnecessary whitespaces that need to be trimmed: 
```java
class MapperDefinitions {
    private Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class)
        .immutable(PersonDTO::new)
        .from(Person::getFirstName).map(String::trim).to(ConstructorParameter::bind)
        .from(Person::getLastName).map(String::trim).to(ConstructorParameter::bind)
        .from(Person::isActive).to(ConstructorParameter::bind)
        .from((Function.identity())
            .map(person -> person.isActive() && person.isCanLogin())
            .to(ConstructorParameter::bind)
        .build();
}
```

Maybe some parts of the mapping logic are businessful or too complex to express in a simple lamdba:
```java
class PersonNameCleaner {
    public String cleanupFirstName(String firstName) { ... }
    public String cleanupLastName(String firstName) { ... }
}

class PersonValidator {
    public boolean shouldBeActive(Person person) { ... }
}

class MapperDefinitions {
    //maybe get these instances via dependency injection 
    //or a parameter when using a function to generate the mapper
    private PersonNameCleaner personNameCleaner = new PersonNameCleaner();
    private PersonValidator personValidator = new PersonValidator();
    
    private Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class)
        .immutable(PersonDTO::new)
        .from(Person::getFirstName).map(personNameCleaner::cleanupFirstName).to(ConstructorParameter::bind)
        .from(Person::getLastName).map(personNameCleaner::cleanupLastName).to(ConstructorParameter::bind)
        .from(Person::isActive).map(personValidator::shouldBeActive).to(ConstructorParameter::bind)
        .from((Function.identity())
            .map(person -> personValidator.shouldBeActive(person) && person.isCanLogin())
            .to(ConstructorParameter::bind)
        .build();
}
```

Some changes were done and the `Person's` `firstName` and `lastName` are now nullable, let's integrate that
before we pass `null` to the functions of `PersonNameCleaner`:
```java
class PersonNameCleaner {
    public String cleanupFirstName(String firstName) { ... }
    public String cleanupLastName(String firstName) { ... }
}

class PersonValidator {
    public boolean shouldBeActive(Person person) { ... }
}

class MapperDefinitions {
    //maybe get these instances via dependency injection
    //or a parameter when using a function to generate the mapper
    private PersonNameCleaner personNameCleaner = new PersonNameCleaner();
    private PersonValidator personValidator = new PersonValidator();
    
    private Mapper<Person, PersonDTO> mapper = Datus.forTypes(Person.class, PersonDTO.class)
        .immutable(PersonDTO::new)
        .from(Person::getFirstName)
            .given(Object::nonNull, personNameCleaner::cleanupFirstName).orElse(null)
            .to(ConstructorParameter::bind)
        .from(Person::getLastName)
            .given(Object::nonNull, personNameCleaner::cleanupLastName).orElse(null)
            .to(ConstructorParameter::bind)
        .from(Person::isActive).map(personValidator::shouldBeActive).to(ConstructorParameter::bind)
        .from((Function.identity())
            .map(person -> personValidator.shouldBeActive(person) && person.isCanLogin())
            .to(ConstructorParameter::bind)
        .build();
}
```
### Advanced usage / FAQ
This section is focused on use cases of *datus* that are either not directly supported via *datus* classes or
represent a problem that is frequently occurring in *datus* issue tracker.

#### Mapping multiple inputs into one output object
*datus* by design only supports mapping one input object into one output object.
Converting input objects sometimes requires additional information on a per input object basis
which makes using *datus* for these kind of conversions unpleasant, badly performing or even impossible. 

The best way to handle multiple input objects when using *datus* is to use `Pair<Input1, Input2>`, `Triple<Input1, Input2, Input3>`
(, ...) alike container objects. Please note that *datus* does not supply these container types as
these kind of objects lie outside of *datus* scope of mapping from an input to an output object. Feel
free to use any other library which implement said container types but please reconsider if *datus* is really the
right solution for aggregating multiple input objects into one output object as these kind of mappings often imply a 
great amount of logic which might justify implementing it in a standalone factory class.

#### I cannot express X with the immutable / mutable API (but want consistency by using the Mapper<Input,Output> interface)
(Preamble: Feel free to open an issue if you think *datus* could be improved or extended)

Some mapping scenarios are cumbersome or even impossible to express in *datus* immutable / mutable API.
This is where the simplistic `Mapper<Input, Output>` interface comes in handy.
It only requires one function, `Output convert(Input input)`, to provide all the mapper functionality
as outlined [at the end of basics](#basics).

Here is a simple list copying mapper for example:
```java
Mapper<List<String>, List<String>> copyMapper = list -> new ArrayList<>(list)
```
You can always implement the `Mapper<Input, Output>` interface to gain a more consistent usage of
factories / converters across your library / application which would also allow for a more
sophisticated implementation of e.g. `Collection<Output> convert(Collection<Input>)` (e.g. batch some operations to a
helper class).

#### Mapping recursive data structures
Consider the following class:
```java
class Node {
    //getter + setter omitted for brevity
    private Node parent;
    private Object someData;
}
```
At first glance it seems like it is impossible to define a mapping process for such a self-recursive data structure as 
the `Mapper<Anything, Node>` cannot be referenced while still under construction: the java compiler will complain about referencing
an uninitialized variable. But there is a way to use *datus* immutable / mutable API to generate a mapper for self-recursive 
data structures with the following helper class:
```java
class MapperDelegate<Input, Output> implements Mapper<Input, Output> {
    private Mapper<Input, Output> mapper;
    
    @Override
    public Output convert(Input input) {
        return mapper.convert(input);
    }
    
    public void setMapper(Mapper<Input, Output> mapper) {
        this.mapper = mapper;
    }
}

public Mapper<Node, Node> generateMapper() {
    MapperDelegate<Node, Node> delegate = new MapperDelegate<>();
    Mapper<Node, Node> mapper = Datus.forTypes(Node.class, Node.class)
        .mutable(Node::new)
        .from(Node::someData).to(Node::setSomeData)
        .from(Node::getParent)
            .given(Objects::nonNull, parent -> delegate.convert(parent)).orElse(null)
            .to(Node::setParent)
        .build();
    delegate.setMapper(mapper);
    return mapper;
}
```