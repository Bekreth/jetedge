package com.rainbowpunch.jetedge.spi.pojo;

import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * This interface lays out the different ways that POJOs can be generated.
 * @param <T> The POJO class that you want to generator to produce.
 */
public interface PojoGenerator<T> {

    /**
     * Generates a single POJO of type <code>T</code>
     * @return A single POJO of type <code>T</code>
     */
    T generatePojo();

    ClassAttributes getClassAttributes();

    PojoAttributes<T> getPojoAttributes();

    /**
     * Generates an infinite stream of POJOs of type <code>T</code>
     * @return An infinite stream of <code>T</code> POJOs
     */
    default Stream<T> generatePojoStream() {
        return Stream.generate(this::generatePojo);
    }

    /**
     * Generates a list of POJOs of type <code>T</code>
     * @param length The number of element of <code>T</code> that should be put into the list
     * @return A list of POJOs of type <code>T</code>
     */
    default List<T> generatePojoList(int length) {
        return generatePojoStream().limit(length).collect(toList());
    }
}
