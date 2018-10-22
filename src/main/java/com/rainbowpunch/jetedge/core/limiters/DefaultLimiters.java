package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.exception.ConfusedGenericException;
import com.rainbowpunch.jetedge.core.limiters.collections.ArrayLimiter;
import com.rainbowpunch.jetedge.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.DefaultPojoLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.EnumLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.BigDecimalLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.BigIntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.DateLimiter;
import com.rainbowpunch.jetedge.core.limiters.maps.MapLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.BooleanLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.ByteLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.CharacterLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.DoubleLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.FloatLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.LongLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.ShortLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.StringLimiter;
import com.rainbowpunch.jetedge.core.reflection.ClassAttributes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A class containing the default behaviour for data generation.
 */
public final class DefaultLimiters {

    private DefaultLimiters() {

    }

    @SuppressWarnings("unchecked")
    public static Limiter<?> getDefaultLimiter(ClassAttributes classAttributes, PojoAttributes pojoAttributes) {
        Limiter<?> outputLimiter = null;

        if (classAttributes.isArray()) {
            Limiter<?> singleObjectLimiter = multiplePojoLimiter(classAttributes, pojoAttributes,
                    () -> classAttributes.getClazz());
            outputLimiter = ArrayLimiter.createArrayLimiter(singleObjectLimiter);
        } else if (classAttributes.isCollection()) {
            Supplier<Class> classSupplier = () -> {
                List<Class> genericList = classAttributes.getGenericHints();
                if (genericList.size() != 1) {
                    throw new ConfusedGenericException(classAttributes.getClazz().getName());
                }
                return genericList.get(0);
            };
            Limiter<?> singleObjectLimiter = multiplePojoLimiter(classAttributes, pojoAttributes, classSupplier);
            outputLimiter = new ListLimiter(singleObjectLimiter);

        } else if (classAttributes.isMap()) {
            List<Class> genericList = classAttributes.getGenericHints();
            if (genericList.size() != 2) {
                throw new ConfusedGenericException(classAttributes.getClazz().getName());
            }
            Limiter<?> keyLimiter = multiplePojoLimiter(classAttributes, pojoAttributes, () -> genericList.get(0));
            Limiter<?> valueLimiter = multiplePojoLimiter(classAttributes, pojoAttributes, () -> genericList.get(1));

            outputLimiter = new MapLimiter(keyLimiter, valueLimiter);
        } else if (classAttributes.isEnum()) {
            outputLimiter = EnumLimiter.createEnumLimiter(classAttributes.getClazz());
        } else {
            Limiter limiter = LimiterMapper.getDefaultMapping(classAttributes::is);
            if (limiter != null) {
                outputLimiter = limiter;
            } else {
                outputLimiter = new DefaultPojoLimiter<>(classAttributes, classAttributes.getClazz(), pojoAttributes);
            }
        }
        return outputLimiter;
    }

    private static Limiter<?> multiplePojoLimiter(ClassAttributes containingClassAttribute,
                                                  PojoAttributes pojoAttributes, Supplier<Class> classToAccess) {
        ClassAttributes attributes = ClassAttributes.create(containingClassAttribute.getParentClassAttribute(),
                classToAccess.get(), null);
        attributes.setFieldNameOfClass(containingClassAttribute.getFieldNameOfClass());
        return getDefaultLimiter(attributes, pojoAttributes);
    }

    private enum LimiterMapper {
        // Primitives
        INTEGER(Integer.class, new IntegerLimiter()),
        SHORT(Short.class, new ShortLimiter()),
        LONG(Long.class, new LongLimiter()),
        BOOLEAN(Boolean.class, new BooleanLimiter()),
        FLOAT(Float.class, new FloatLimiter()),
        DOUBLE(Double.class, new DoubleLimiter()),
        CHARACTER(Character.class, new CharacterLimiter()),
        BYTE(Byte.class, new ByteLimiter()),
        STRING(String.class, new StringLimiter()),

        // Common Java Lang Limiters
        BIG_INTEGER(BigInteger.class, new BigIntegerLimiter()),
        BIG_DECIMAL(BigDecimal.class, new BigDecimalLimiter()),
        DATE(Date.class, new DateLimiter());

        private Class clazz;
        private Limiter defaultLimiter;

        LimiterMapper(Class clazz, Limiter defaultLimiter) {
            this.clazz = clazz;
            this.defaultLimiter = defaultLimiter;
        }

        static Limiter getDefaultMapping(Predicate<Class> predicate) {
            Limiter outLimiter = null;
            for (LimiterMapper limiter : LimiterMapper.values()) {
                if (predicate.test(limiter.clazz)) {
                    outLimiter = limiter.defaultLimiter;
                    break;
                }
            }
            return outLimiter;
        }
    }

}
