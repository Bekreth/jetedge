package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

public class ByteArrayLimiter implements Limiter<byte[]> {

    private int size;

    public ByteArrayLimiter() {
        this(60);
    }

    public ByteArrayLimiter(int size) {
        this.size = size;
    }

    @Override
    public Supplier<byte[]> generateSupplier(Random random) {
        return () -> {
            byte[] bytes = new byte[size];
            random.nextBytes(bytes);
            return bytes;
        };
    }
}
