package com.rainbowpunch.jetedge.core.analyzer;

import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;
import com.rainbowpunch.jetedge.core.reflection.FieldAttributes;

import java.util.stream.Stream;

/**
 * This class takes a generic class along with a map of the limiters and creates a list of consumers for that
 *      class.  Creating a new instance of the class and passing the it through the consumer will create the
 *      test data.
 */
@FunctionalInterface
public interface PojoAnalyzer {
    Stream<FieldAttributes> extractFields(ClassAttributes classAttributes);
}
