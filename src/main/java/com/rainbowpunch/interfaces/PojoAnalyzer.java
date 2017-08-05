package com.rainbowpunch.interfaces;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * This class takes a generic class along with a map of the limiters and creates a list of consumers for that
 *      class.  Creating a new instance of the class and passing the it through the consumer will create the
 *      test data.
 */
public interface PojoAnalyzer<T> {

    /**
     * @param clazz  The class to be analyzed
     * @return
     */
    void parsePojo(Class<T> clazz, PojoAttributes<T> attributes);

}
