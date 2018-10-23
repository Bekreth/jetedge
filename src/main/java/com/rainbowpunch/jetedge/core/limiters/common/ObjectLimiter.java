package com.rainbowpunch.jetedge.core.limiters.common;

import com.rainbowpunch.jetedge.core.exception.LimiterConstructionException;
import com.rainbowpunch.jetedge.core.limiters.SimpleAbstractLimiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * This limiter is used to specify a list of objects to get an Object from to populate a field.
 */
public abstract class ObjectLimiter<T> extends SimpleAbstractLimiter<T> {

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

    /**
     * Converts a list of objects into an simple ObjectLimiter.
     */
    public static <U> ObjectLimiter<U> ofObjects(Collection<U> objects) {
        return new ObjectLimiter<U>() {
            @Override
            protected List<U> configureObjectList() {
                if (objects.size() == 0) {
                    throw new LimiterConstructionException("ObjectLimiter can't be constructed with an "
                            + "empty collection");
                }
                return new ArrayList<>(objects);
            }
        };
    }

}
