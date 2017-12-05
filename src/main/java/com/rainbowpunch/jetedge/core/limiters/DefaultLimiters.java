package com.rainbowpunch.jetedge.core.limiters;

import com.rainbowpunch.jetedge.core.PojoAttributes;
import com.rainbowpunch.jetedge.core.limiters.collections.ArrayLimiter;
import com.rainbowpunch.jetedge.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.DefaultPojoLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.EnumLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.BigDecimalLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.BigIntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.java.DateLimiter;
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

public class DefaultLimiters {

    @SuppressWarnings("unchecked")
    public static Limiter<?> getDefaultLimiter(ClassAttributes classAttributes, PojoAttributes pojoAttributes, String entryName) {
        if (classAttributes.isSubclassOf(List.class) || classAttributes.isArray()) {
            // TODO: 12/5/17 Add more useful Exception
            Limiter limiter = getDefaultLimiter(classAttributes.getElementType().orElseThrow(RuntimeException::new), pojoAttributes, entryName);

            if (classAttributes.isSubclassOf(List.class)) return ListLimiter.createListLimiter(limiter);
            else if (classAttributes.isArray()) return ArrayLimiter.createArrayLimiter(limiter);
        }

        else if (classAttributes.isEnum()) return EnumLimiter.createEnumLimiter(classAttributes.getClazz());
        Limiter limiter = LimiterMapper.getDefaultMapping(classAttributes::is);
        if (limiter != null) return limiter;

        return new DefaultPojoLimiter<>(classAttributes, classAttributes.getClazz(), pojoAttributes, entryName);
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
            for (LimiterMapper limiter : LimiterMapper.values()) {
                if (predicate.test(limiter.clazz)) {
                    return limiter.defaultLimiter;
                }
            }
            return null;
        }
    }

}
