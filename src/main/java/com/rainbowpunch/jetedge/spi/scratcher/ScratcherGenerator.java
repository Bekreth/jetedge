package com.rainbowpunch.jetedge.spi.scratcher;

import java.util.List;
import java.util.stream.Stream;

/**
 * This interface lays out the different ways that POJOs can be scratched.
 * @param <T> The POJO class that you want to generator to perform scratches on.
 */
public interface ScratcherGenerator<T> {

    /**
     * Scratches a provided POJO of type <code>T</code>
     * @return A single POJO of type <code>T</code>
     */
    T scratchPojo(T object);

    /**
     * Scratches a list of POJOs of type <code>T</code> based on the initial pojo
     * @param length The number of element of <code>T</code> that should be put into the list
     * @return A list of POJOs of type <code>T</code>
     */
    List<T> scratchPojoList(T object, int length);

    /**
     * Generates an infinite stream of POJOs of type <code>T</code>
     * @return An infinite stream of <code>T</code> POJOs
     */
    Stream<T> scratchedPojoStream(T object);
}
