package com.github.roookeee.datus.api;

/**
 * A wrapper object that implements the {@link Mapper} interface by using another mapper instance.
 * This is useful when defining a mapping for recursive data structures (e.g. categories with subcategories) which would
 * have to reference the mapper that is still under construction which is disallowed by the Java compiler (referencing
 * an uninitialized variable). Using a {@link MapperProxy} circumvents this problem as it can be instantiated
 * and referenced before the internal mapper is created and assigned to it.
 *
 * @param <In>  the type of the input object(s) of the mapper
 * @param <Out> the type of the output object(s) of the mapper
 */
public class MapperProxy<In, Out> implements Mapper<In, Out> {
    private Mapper<? super In, ? extends Out> mapper;

    @Override
    public Out convert(In input) {
        return mapper.convert(input);
    }

    /**
     * Sets the internal mapper of this proxy.
     *
     * @param mapper the mapper to use
     */
    public void setMapper(Mapper<? super In, ? extends Out> mapper) {
        this.mapper = mapper;
    }

}
