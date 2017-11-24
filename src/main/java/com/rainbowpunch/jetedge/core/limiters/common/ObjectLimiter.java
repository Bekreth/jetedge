package com.rainbowpunch.jetedge.core.limiters.common;

import com.rainbowpunch.jetedge.core.limiters.Limiter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * This limiter is used to specify a list of objects to get an Object from to populate a field.
 */
public abstract class ObjectLimiter<T> implements Limiter<T> {

    protected List<T> acceptableObjectList;

    public ObjectLimiter() {
        acceptableObjectList = configureObjectList();
    }

    protected abstract List<T> configureObjectList();

    public void updateObjectList(List<T> newList) {
        acceptableObjectList.clear();
        acceptableObjectList.addAll(newList);
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> {
            int randomObject = random.nextInt(acceptableObjectList.size());
            return acceptableObjectList.get(randomObject); // TODO: 7/30/17
        };
    }

    public static <U> ObjectLimiter<U> ofObjects(U... objects) {
        return ofObjects(Arrays.asList(objects));
    }

    public static <U> ObjectLimiter<U> ofObjects(List<U> objects) {
        return new ObjectLimiter<U>() {
            @Override
            protected List<U> configureObjectList() {
                return objects;
            }
        };
    }

}
