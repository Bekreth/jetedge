package com.rainbowpunch.jtdg.spi;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface PojoGenerator<T> {
    T generatePojo();

    default Stream<T> generatePojoStream() {
        return Stream.generate(this::generatePojo);
    }

    default List<T> generatePojoList(int length) {
        return generatePojoStream().limit(length).collect(toList());
    }
}
