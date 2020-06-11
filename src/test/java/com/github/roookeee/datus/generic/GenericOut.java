package com.github.roookeee.datus.generic;

import java.util.List;

public class GenericOut<R> {

    private R element;

    private R [] array;

    private List<R> list;

    public R getElement() {
        return element;
    }

    public void setElement(R element) {
        this.element = element;
    }

    public R[] getArray() {
        return array;
    }

    public void setArray(R[] array) {
        this.array = array;
    }

    public List<R> getList() {
        return list;
    }

    public void setList(List<R> list) {
        this.list = list;
    }

}
