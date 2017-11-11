package com.rainbowpunch.jtdg.core.analyzer;

import com.rainbowpunch.jtdg.core.falseDomain.Car;
import com.rainbowpunch.jtdg.core.falseDomain.Door;
import com.rainbowpunch.jtdg.core.limiters.ConstantValueLimiter;
import com.rainbowpunch.jtdg.core.limiters.NestedLimiter;
import com.rainbowpunch.jtdg.core.limiters.RegexLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jtdg.spi.PojoGenerator;
import com.rainbowpunch.jtdg.spi.PojoGeneratorBuilder;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FieldPojoAnalyzerTest {

    @Test
    public void test() {
        PojoGenerator<Car> generator = new PojoGeneratorBuilder<>(Car.class, new FieldPojoAnalyzer())
                .andLimitField("topSpeed", new IntegerLimiter(10))
                .andLimitField("make", new ConstantValueLimiter<String>("Ford"))
                .andLimitField("scuffs", new NestedLimiter<>(Door.class, new RegexLimiter("\\d{4}")))
                .build();

        Car car = generator.generatePojo();
        assertTrue(car.getTopSpeed() < 10);
        assertTrue(car.getDoorCount() != 0);
        assertTrue(car.getMake().equals("Ford"));
        assertTrue(car.getModel() != null);

        assertTrue(car.getDoor().getLocks() != 0);
        assertTrue(car.getDoor().getScuffs().length() == 4);
        System.out.println(car.toString());
    }

}