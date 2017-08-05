package com.rainbowpunch.jtdg.core;

import com.rainbowpunch.jtdg.api.DataGenerator;
import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 */
public class PojoGenerator<T> {

    private Class<T> pojo;
    private PojoAnalyzer<T> pojoAnalyzer;
    private DataGenerator dataGenerator;
    private PojoAttributes<T> pojoAttributes;
    private int count;

    public PojoGenerator(Class<T> clazz) {
        this.pojo = clazz;
        pojoAnalyzer = new DefaultPojoAnalyzer<>();
        dataGenerator = new DataGenerator();
        pojoAttributes = new PojoAttributes<>();
    }

    public PojoGenerator andLimitField(String fieldName, Limiter<?> limiter) {
        pojoAttributes.putFieldLimiter(fieldName, limiter);
        return this;
    }

    /**
     * This method populates the PojoAttributes with the needed fieldSetters and dataGenerators
     *      necessary to create Pojos on demand.
     * @return
     */
    public PojoGenerator analyzePojo() {
        pojoAnalyzer.parsePojo(pojo, pojoAttributes);
        dataGenerator.populateSuppliers(pojoAttributes);
        return this;
    }

    public Stream<T> generatePojoStream() {
        return IntStream.iterate(0, i -> i + 1)
                .mapToObj(this::generatePojo);
    }

    public List<T> generatePojoList() {
        return generatePojoStream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public T generatePojo() {
        return generatePojo(0);
    }

    private T generatePojo(int i) {
        try {
            T newInstance = pojo.newInstance();
            pojoAttributes.apply(newInstance);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
