package com.rainbowpunch.jtdg.core.limiters.primitive;

import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.Random;
import java.util.function.Supplier;

/**
 * The default behavior for arrays of objects is to use the ListLimiter and then cast them to an array.  Bytes are an exception to this.
 *      Given that the Random object can easily populate byte[], it was decided that ByteLimiter should be a simplified version of
 *      ByteArrayLimiter instead of using the List cast to array model that the rest use.
 */
public class ByteLimiter implements Limiter<Byte> {

    private ByteArrayLimiter byteArrayLimiter = new ByteArrayLimiter(1);

    @Override
    public Supplier<Byte> generateSupplier(Random random) {
        return () -> {
            return byteArrayLimiter.generateSupplier(random).get()[0];
        };
    }
}
