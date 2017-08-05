package com.rainbowpunch.interfaces.limiters;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by bekreth on 7/30/17.
 */
public abstract class ObjectLimiter<T> implements Limiter<T> {

    private List<T> acceptableObjectList;

    public ObjectLimiter() {
        acceptableObjectList = configureObjectList();
    }

    protected abstract List<T> configureObjectList();

    protected void configureObjectList(List<T> objectList) {
        this.acceptableObjectList = objectList;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> {
            int randomObject = random.nextInt(acceptableObjectList.size());
            return acceptableObjectList.get(randomObject); // TODO: 7/30/17
        };
    }

}
