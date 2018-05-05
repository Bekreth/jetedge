package com.rainbowpunch.jetedge.core.limiters.common;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This limiter allows you to set a single value to be used instead of any random value.  Alternatively, you
 * can also use this to provide your own Supplier or Function for value production.
 *
 * <h3>Basic Constant Value Example</h3>
 * <pre>{@code
 *     // will always return "a value"
 *     ConstantValueLimiter<String> cvl = new ConstantValueLimiter<String>("a value");
 * }</pre>
 *
 * <h3>Supplier Example</h3>
 * <pre>{@code
 *     // will call supplier.get() to get the value
 *     Supplier<String> supplier = new MySupplier();
 *     ConstantValueLimiter<String> cvlFn = new ConstantValueLimiter<String>(supplier);
 *
 *     // use a local method as the value supplier
 *     MyObject o = new MyObject();
 *     o.myMethod(); // yields "abc" or some other value.
 *     // will call o.myMethod() to get the value
 *     ConstantValueLimiter<String> cvlFn = new ConstantValueLimiter<String>(o::myMethod);
 * }</pre>
 *
 * <h3>Function Example</h3>
 * <p>
 *     The benefit to using a Function<Random,?> over a Supplier<?> is realized when you need to use
 *     random data in your supplier.  By accepting the Random as a parameter to your function, you are
 *     able to ensure the stability of your return values (when random is properly seeded).
 * </p>
 * <pre>{@code
 *     Function<Random, Integer> myFn = random -> random.nextInt() * 777;
 *     // will call myFn.apply(<seeded random>) to get the value.
 *     ConstantValueLimiter<String> cvlFn = new ConstantValueLimiter<String>(myFn);
 * }</pre>
 */
public class ConstantValueLimiter<T> extends ObjectLimiter<T> {

    private final Function<Random, T> function;

    /**
     * Construct a new {@link ConstantValueLimiter} which will return the same
     * value every time it's called.
     *
     * @param object the value to return
     */
    public ConstantValueLimiter(T object) {
        this(() -> object);
    }

    /**
     * Construct a new {@link ConstantValueLimiter} with your own {@link Supplier}.  This
     * supplier will be called each time a new value is required when populating the POJOs.
     *
     * @return a new instance configured to use the supplied supplier
     * @param supplier the supplier to use when populating the POJO attributes
     */
    public ConstantValueLimiter(Supplier<T> supplier) {
        this(functionFromSupplier(supplier, "supplier"));
    }

    /**
     * Creates a new {@link ConstantValueLimiter} that will call the supplied function
     * each time a new value is required when populating the POJOs.  The function includes the random used
     * for generating random values.  This allows you to construct consistently random values from the
     * seeded random object.
     *
     * @return a new instance configured to use the supplied function
     * @param function The function to call when
     */
    public ConstantValueLimiter(Function<Random, T> function) {
        this.function = required(function, "function");
    }

    @Override
    protected final List<T> configureObjectList() {
        return null;
    }

    @Override
    public Supplier<T> generateSupplier(Random random) {
        return () -> this.function.apply(random);
    }

    /**
     * @param supplier the supplier to wrap in a function
     * @param name the argument name to return in the error if supplier is null
     * @return a function that accepts a random object and calls {@link Supplier#get()}
     */
    private static final <S> Function<Random,S> functionFromSupplier(Supplier<S> supplier, String name) {
        Supplier<S> s = required(supplier, name);
        return (random -> s.get());
    }

    /**
     * Helper method that throws an {@link IllegalArgumentException} if the supplied value is null
     * @param val the value to check
     * @param name the argument name to include in the exception message
     * @return the value if non-null
     */
    private static final <X> X required(X val, String name) {
        if (val == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
        return val;
    }
}
