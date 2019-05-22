## *datus* usage guide

This document is still under construction and will be filled before releasing *datus* 1.0.

I apologize for the current state of this document and would like to point you to the currently existing
JavaDoc in the source code.

####Overview
1. [Getting started](#getting-started)
1. [Basics](#basics)
1. [Immutable API](#immutable-api)
1. [Mutable API](#mutable-api)
1. [Extended usage](#immutable-api)

#### Getting started
*datus* is available at Maven Central:
```xml
<dependency>
  <groupId>com.github.roookeee</groupId>
  <artifactId>datus</artifactId>
  <version>0.9.1</version>
</dependency>
```

*datus* supports any JDK (Hotspot, OpenJDK, J9 etc.) which supports Java 8 or newer.
#### Basics
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

The immutable API expects exactly zero constructor parameters whereas the immutable API supports
up to 8 constructor parameters (consider opening an issue if you need *datus* to support more constructor parameters).

Even though the two APIs internally differ significantly *datus* strives to unify the experience of both workflows.
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

`given(Object::nonNull, String::trim).orElse("fallback")`: Like `map` using `given` is entirely optional. `given`
is similar to `map` but considers a given predicate to determine which function/supplier/value to use. As the type of the current 
mapping step may change through the provided function/supplier/value in the `given`-call an `orElse` is
mandatory to ensure both branches result in the same type (if the type does not change consider using `Function.identity()` in
cases where one branch should not modify the value in any way).

`to(OutputType::someSetter OR ConstructorParameter::bind)`: The `to/into` operations of *datus* finalize
the preceding mapping step definition by binding its definition to the current constructor parameter (immutable API)
or a given setter (mutable API). Any type conversion (e.g. a nested `Address` has to be transformed to an 
`AddressDTO`) has to happen in preceding `map` steps. A type mismatch will always result in a compilation error.

#### Immutable API
TODO
#### Mutable API
TODO
#### Extended usage
TODO
