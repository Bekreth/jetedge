package com.rainbowpunch.jtdg.spi;

import com.rainbowpunch.jtdg.core.FieldDataGenerator;
import com.rainbowpunch.jtdg.core.DefaultPojoAnalyzer;
import com.rainbowpunch.jtdg.core.PojoAnalyzer;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 */
public class PojoGenerator<T> implements Cloneable {

    private Class<T> pojo;
    private Integer randomSeed;

    private PojoAnalyzer<T> pojoAnalyzer;
    private FieldDataGenerator fieldDataGenerator;
    private PojoAttributes<T> pojoAttributes;

    /**
     * Constructor is used internally for cloning purposes.
     */
    private PojoGenerator() {

    }

    public PojoGenerator(Class<T> clazz) {
        this(clazz, new Random().nextInt());
    }

    public PojoGenerator(Class<T> clazz, Integer randomSeed) {
        this.pojo = clazz;
        this.randomSeed = randomSeed;

        pojoAnalyzer = new DefaultPojoAnalyzer<>();
        pojoAttributes = new PojoAttributes<>(clazz, randomSeed);
        fieldDataGenerator = randomSeed == null ? new FieldDataGenerator() : new FieldDataGenerator(randomSeed);
    }

    public PojoGenerator<T> andLimitField(String fieldName, Limiter<?> limiter) {
        pojoAttributes.putFieldLimiter(fieldName, limiter);
        return this;
    }

    public PojoGenerator<T> andLimitField(List<String> fieldNames, Limiter<?> limiter) {
        fieldNames.forEach(name -> this.andLimitField(name, limiter));
        return this;
    }

    public PojoGenerator<T> andLimitAllFieldsOf(Limiter<?> limiter) {
        Class clazz = ((Class) ((ParameterizedType) limiter.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        pojoAttributes.putAllFieldLimiter(clazz, limiter);
        return this;
    }

    public PojoGenerator<T> andIgnoreField(String fieldName) {
        pojoAttributes.ignoreField(fieldName);
        return this;
    }

    /**
     * This method populates the PojoAttributes with the needed fieldSetters and dataGenerators
     *      necessary to create Pojos on demand.
     * @return
     */
    public PojoGenerator<T> analyzePojo() {
        pojoAnalyzer.parsePojo(pojo, pojoAttributes);
        fieldDataGenerator.populateSuppliers(pojoAttributes);
        return this;
    }

    public Stream<T> generatePojoStream() {
        return IntStream.iterate(0, i -> i + 1)
                .mapToObj(this::generatePojo);
    }

    public List<T> generatePojoList(int count) {
        return generatePojoStream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public T generatePojo() {
        return generatePojo(0);
    }

    @Override
    public PojoGenerator<T> clone() {
        PojoGenerator generator = new PojoGenerator();

        try {
            generator.pojo = Class.forName(this.pojo.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone objects: ", e);
        }
        generator.randomSeed = new Integer(this.randomSeed);
        generator.pojoAnalyzer = new DefaultPojoAnalyzer<>();
        generator.pojoAttributes = this.pojoAttributes.clone();
        generator.fieldDataGenerator = new FieldDataGenerator(randomSeed);

        return generator;
    }

    private T generatePojo(int i) {
        try {
            T newInstance = pojo.newInstance();
            pojoAttributes.apply(newInstance);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("Error generating pojo", e);
        }
    }

}
