package com.github.roookeee.datus.generic;

import java.util.List;

public class GenericIn<T> {

    private T element;

    private T [] array;

    private List<T> list;

    public T getElement() {
        return element;
    }

    public GenericIn<T> setElement(T element) {
        this.element = element;
        return this;
    }

    public T[] getArray() {
        return array;
    }

    public GenericIn<T> setArray(T[] array) {
        this.array = array;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public GenericIn<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

}