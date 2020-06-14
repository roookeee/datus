package com.github.roookeee.datus.generics;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.immutable.ConstructorParameter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericsTest {

    @Test
    public void shouldSupportGenericTypesInTheImmutableApi() {
        //given
        ImmutableContainer<Integer> input = new ImmutableContainer<>(4, new Integer[]{5, 6}, Arrays.asList(7, 8));
        Mapper<ImmutableContainer<Integer>, ImmutableContainer<String>> mapper =
                Datus.<ImmutableContainer<Integer>, ImmutableContainer<String>>forTypes()
                        // type inference hates us a little here, the generic type of immutable is inferred by the passed lambda
                        // which is generic and thus object -> have to specify type although it is a bit ugly.
                        // Alternatively you can use a lambda with typed parameters.
                        .<String, String[], List<String>>immutable(ImmutableContainer::new)
                        .from(ImmutableContainer::getElement).map(Object::toString).to(ConstructorParameter::bind)
                        .from(ImmutableContainer::getElements)
                            .map(array -> Arrays.stream(array).map(Object::toString).toArray(String[]::new))
                            .to(ConstructorParameter::bind)
                        .from(ImmutableContainer::getElementList)
                            .map(list -> list.stream().map(Object::toString).collect(Collectors.toList()))
                            .to(ConstructorParameter::bind)
                        .build();

        //when
        ImmutableContainer<String> result = mapper.convert(input);

        //then
        assertThat(result.getElement()).isEqualTo("4");
        assertThat(result.getElements()).isEqualTo(new String[]{"5", "6"});
        assertThat(result.getElementList()).isEqualTo(Arrays.asList("7", "8"));
    }

    @Test
    public void shouldSupportGenericTypesInTheMutableApi() {
        //given
        MutableContainer<Integer> input = new MutableContainer<>();
        input.setElement(4);
        input.setElements(new Integer[]{5, 6});
        input.setElementList(Arrays.asList(7, 8));
        Mapper<MutableContainer<Integer>, MutableContainer<String>> mapper =
                Datus.<MutableContainer<Integer>, MutableContainer<String>>forTypes()
                        .mutable(MutableContainer::new)
                        .from(MutableContainer::getElement).map(Object::toString).into(MutableContainer::setElement)
                        .from(MutableContainer::getElements)
                            .map(array -> Arrays.stream(array).map(Object::toString).toArray(String[]::new))
                            .into(MutableContainer::setElements)
                        .from(MutableContainer::getElementList)
                            .map(list -> list.stream().map(Object::toString).collect(Collectors.toList()))
                            .into(MutableContainer::setElementList)
                        .build();

        //when
        MutableContainer<String> result = mapper.convert(input);

        //then
        assertThat(result.getElement()).isEqualTo("4");
        assertThat(result.getElements()).isEqualTo(new String[]{"5", "6"});
        assertThat(result.getElementList()).isEqualTo(Arrays.asList("7", "8"));
    }

    private static class ImmutableContainer<T> {
        private final T element;
        private final T[] elements;
        private final List<T> elementList;

        public ImmutableContainer(T element, T[] elements, List<T> elementList) {
            this.element = element;
            this.elements = elements;
            this.elementList = elementList;
        }

        public T getElement() {
            return element;
        }

        public T[] getElements() {
            return elements;
        }

        public List<T> getElementList() {
            return elementList;
        }
    }

    private static class MutableContainer<T> {
        private T element;
        private T[] elements;
        private List<T> elementList;

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public T[] getElements() {
            return elements;
        }

        public void setElements(T[] elements) {
            this.elements = elements;
        }

        public List<T> getElementList() {
            return elementList;
        }

        public void setElementList(List<T> elementList) {
            this.elementList = elementList;
        }
    }
}
