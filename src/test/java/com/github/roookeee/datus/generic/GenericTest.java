package com.github.roookeee.datus.generic;

import com.github.roookeee.datus.api.Datus;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenericTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test(){

        GenericIn<String> in = new GenericIn<String>()
            .setArray(new String[]{"1"})
            .setElement("2")
            .setList(Collections.singletonList("3"));
        
        GenericOut<Integer> out = Datus.forTypes(GenericIn.class, GenericOut.class)
             .mutable(GenericOut::new)
             .from(xx -> (String[])xx.getArray()).map(x -> Arrays.stream(x).map(Integer::valueOf).toArray(Integer[]::new)).into(GenericOut::setArray)
             .from(xx -> (String)xx.getElement()).map(Integer::valueOf).into(GenericOut::setElement)
             .from(xx -> (List<String>)xx.getList()).map(list -> list.stream().map(Integer::valueOf).collect(Collectors.toList())).into(GenericOut::setList)
             .build()
             .convert(in);

        Assertions.assertNotNull(out);
        Assertions.assertArrayEquals(new Integer[]{1}, out.getArray());
        Assertions.assertIterableEquals(Collections.singletonList(3), out.getList());
        Assertions.assertEquals(2, out.getElement());

    }

}
