package com.rainbowpunch.jetedge.integration;

import com.rainbowpunch.jetedge.core.analyzer.Analyzers;
import com.rainbowpunch.jetedge.core.exception.ConfusedGenericException;
import com.rainbowpunch.jetedge.core.limiters.PojoGeneratorLimiter;
import com.rainbowpunch.jetedge.core.limiters.collections.ListLimiter;
import com.rainbowpunch.jetedge.core.limiters.common.ConstantValueLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jetedge.core.limiters.primitive.StringLimiter;
import com.rainbowpunch.jetedge.core.limiters.special.CorrelationLimiter;
import com.rainbowpunch.jetedge.core.limiters.special.MultiplexLimiter;
import com.rainbowpunch.jetedge.spi.DefaultDataLimiter;
import com.rainbowpunch.jetedge.spi.PojoGenerator;
import com.rainbowpunch.jetedge.spi.PojoGeneratorBuilder;
import com.rainbowpunch.jetedge.test.Pojos;
import com.rainbowpunch.jetedge.test.Pojos.Extra;
import com.rainbowpunch.jetedge.test.Pojos.Person;
import com.rainbowpunch.jetedge.test.Pojos.Superhero;
import com.rainbowpunch.jetedge.test.Pojos.Vehicle;
import com.rainbowpunch.jetedge.util.ReadableCharList;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.rainbowpunch.jetedge.test.Assertions.assertPojosShallowEqual;
import static com.rainbowpunch.jetedge.test.Pojos.City;
import static com.rainbowpunch.jetedge.test.Pojos.ClassExtendsSomeGenerics;
import static com.rainbowpunch.jetedge.test.Pojos.ClassExtendsWithNoGenerics;
import static com.rainbowpunch.jetedge.test.Pojos.ClassExtendsWithSpecificGeneric;
import static com.rainbowpunch.jetedge.test.Pojos.ParameterConstructor;
import static com.rainbowpunch.jetedge.test.Pojos.Power.FLIGHT;
import static com.rainbowpunch.jetedge.test.Pojos.Power.MONEY;
import static com.rainbowpunch.jetedge.test.Pojos.Power.SPEED;
import static com.rainbowpunch.jetedge.test.Pojos.Storyline;
import static com.rainbowpunch.jetedge.test.Pojos.SuperheroNetwork;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PojoGeneratorIntegrationTest {
    private static final int RANDOM_SEED = 42;

    @Test
    public void testGenerateEmptyPojo() {
        Extra generated = new PojoGeneratorBuilder<>(Extra.class)
                .build()
                .generatePojo();

        assertNotNull(generated);
    }

    @Test
    public void testGenerateBasicPojo() {
        Person generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        assertEquals(205897768, generated.getAge());
        assertEquals("<t\"c!>ya,f,0(TDja_(!DkOIfD[$(n", generated.getName());
    }

    @Test
    public void testGeneratePojoWithInheritedFields() {
        Superhero generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        assertEquals(-248792245, generated.getAge());
        assertEquals("t\"c!>ya,f,0(TDja_(!DkOIfD[$(nt", generated.getName());

        // also verify that direct fields are picked up
        assertNotNull(generated.getSuperPowers());
        assertNotNull(generated.getArchNemesis());
    }

    @Test
    public void testGeneratePojoWithListField() {
        Superhero generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        List<Pojos.Power> powers = asList(FLIGHT, FLIGHT, MONEY, SPEED, MONEY);
        assertEquals(powers, generated.getSuperPowers());
    }

    @Test
    public void testGeneratePojoWithNestedLimiterInLists() {
        PojoGenerator<Vehicle> vehicleGenerator = new PojoGeneratorBuilder<>(Vehicle.class)
                .andLimitField("owners.age", new IntegerLimiter(10, 10))
                .andLimitField("salesPerson.age", new IntegerLimiter(10, 10))
                .build();

        Vehicle vehicle = vehicleGenerator.generatePojo();

        for (Person person : vehicle.getOwners()) {
            assertTrue(person.getAge() >= 10 && person.getAge() < 20);
        }
        assertTrue(vehicle.getSalesPerson().getAge() >= 10 && vehicle.getSalesPerson().getAge() < 20);
    }

    @Test
    @Ignore("nested POJO does not currently inherit random seed")
    public void testGeneratePojoWithNestedPojo() {
        Person generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo()
                .getArchNemesis();

        assertEquals(0, generated.getAge());
        assertEquals("", generated.getName());
    }

    @Test
    @Ignore
    public void testPojoGeneratorBuilderClone() {
        PojoGeneratorBuilder<Person> baseGen = new PojoGeneratorBuilder<>(Person.class);

        Person generatedA = baseGen.clone().build().generatePojo();
        Person generatedB = baseGen.clone().build().generatePojo();

        assertPojosShallowEqual(generatedA, generatedB);
    }

    @Test
    public void testLimitFieldByName() {
        int expectedLength = 4;
        Person generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                .andLimitField("name", new StringLimiter(expectedLength))
                .build()
                .generatePojo();

        assertEquals(expectedLength, generated.getName().length());
    }

    @Test
    public void testLimitAllFieldsOfType() {
        int expectedRange = 10;
        Vehicle generated = new PojoGeneratorBuilder<>(Vehicle.class)
                .andUseRandomSeed(RANDOM_SEED)
                .andLimitAllFieldsOf(new IntegerLimiter(expectedRange))
                .build()
                .generatePojo();

        assertTrue(expectedRange >= generated.getMaxSpeed());
        assertTrue(expectedRange >= generated.getNumWheels());
    }

    @Test
    public void testUseCustomAnalyzer() {
        Person generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                // use a custom analyzer that only includes string attributes
                .andUseAnalyzer(classAttributes ->
                        classAttributes.getFields().stream()
                                .filter(f -> f.getType().is(String.class)))
                .build()
                .generatePojo();

        assertEquals("1<t\"c!>ya,f,0(TDja_(!DkOIfD[$(", generated.getName());
        assertEquals(0, generated.getAge());
    }

    @Test
    public void testIgnoreField() {
        Vehicle generated = new PojoGeneratorBuilder<>(Vehicle.class)
                .andIgnoreField("name")
                .andIgnoreField("maxSpeed")
                .build()
                .generatePojo();
        assertNull(generated.getName());
        assertEquals(0, generated.getMaxSpeed());
        assertNotNull(generated.getNumWheels());
    }

    @Test
    public void testNestedIgnoreField() {
        PojoGenerator<Storyline> generator = new PojoGeneratorBuilder<>(Storyline.class)
                .andIgnoreField("archNemesis.name")
                .andIgnoreField("superhero.superPowers")
                .andIgnoreField("superhero.archNemesis.age")
                .build();

        Storyline generated = generator.generatePojo();

        assertNull(generated.getArchNemesis().getName());
        assertNull(generated.getSuperhero().getSuperPowers());
        assertEquals(0, generated.getSuperhero().getArchNemesis().getAge());
    }

    @Test
    public void testPojoGenertorLimiter() {
        PojoGenerator<Person> personGenerator = new PojoGeneratorBuilder<>(Person.class)
                .andLimitField("name", new ConstantValueLimiter<>("Bobby"))
                .andLimitField("age", new ConstantValueLimiter<>(24))
                .lazilyEvaluate()
                .build();

        PojoGenerator<Storyline> storylineGenerator = new PojoGeneratorBuilder<>(Storyline.class)
                .andLimitField("archNemesis", new PojoGeneratorLimiter<>(personGenerator))
                .andLimitField("superhero.archNemesis", new PojoGeneratorLimiter<>(personGenerator))
                .build();

        Storyline storyline = storylineGenerator.generatePojo();

        assertEquals("Bobby", storyline.getArchNemesis().getName());
        assertEquals(24, storyline.getArchNemesis().getAge());
        assertNull(storyline.getArchNemesis().getSecretName());

        assertEquals("Bobby", storyline.getSuperhero().getArchNemesis().getName());
        assertEquals(24, storyline.getSuperhero().getArchNemesis().getAge());
        assertNull(storyline.getSuperhero().getArchNemesis().getSecretName());

    }

    @Test
    public void testNestedLimiter() {
        PojoGenerator<Storyline> generator = new PojoGeneratorBuilder<>(Storyline.class)
                .andLimitField("archNemesis.name", new ConstantValueLimiter<String>("Johnny"))
                .andLimitField("superhero.superPowers", new ListLimiter(3, 12))
                .andLimitField("superhero.archNemesis.age", new IntegerLimiter(100))
                .build();

        Storyline generated = generator.generatePojo();

        assertEquals("Johnny", generated.getArchNemesis().getName());
        assertTrue(generated.getSuperhero().getSuperPowers().size() >= 12);
        assertTrue(generated.getSuperhero().getArchNemesis().getAge() <= 100);
    }

    @Test
    public void testMultiplexLimiter() {
        List<IntegerLimiter> limiters = Arrays.asList(new IntegerLimiter(10), new IntegerLimiter(10, 20), new IntegerLimiter(10, 40));

        PojoGenerator<Person> generator = new PojoGeneratorBuilder<>(Person.class)
                .andLimitField("age", MultiplexLimiter.generateFlatDistribution(limiters))
                .build();

        for (int i = 0; i < 10000; i++) {
            Person person = generator.generatePojo();
            int pAge = person.getAge();
            assertTrue("Bad Value: " + String.valueOf(person.getAge()),
                    (pAge >= 0 && pAge < 10) || (pAge >= 20 && pAge < 30) || (pAge >= 40 && pAge < 50));
        }
    }

    @Test
    public void testLazyEvaluation_noPopulation() {
        PojoGenerator<Vehicle> generator = new PojoGeneratorBuilder<>(Vehicle.class)
                .lazilyEvaluate()
                .build();

        Vehicle vehicle = generator.generatePojo();

        assertNotNull(vehicle);
        assertEquals(0, vehicle.getMaxSpeed());
        assertEquals(0, vehicle.getNumWheels());
        assertNull(vehicle.getName());
        assertNull(vehicle.getEngineType());
    }

    @Test
    public void testLazyEvaluation_partialPopulation() {
        PojoGenerator<Vehicle> generator = new PojoGeneratorBuilder<>(Vehicle.class)
                .lazilyEvaluate()
                .andLimitField("name", new ConstantValueLimiter<>("Hello World"))
                .build();

        Vehicle vehicle = generator.generatePojo();

        assertNotNull(vehicle);
        assertEquals(0, vehicle.getMaxSpeed());
        assertEquals(0, vehicle.getNumWheels());
        assertEquals("Hello World", vehicle.getName());
        assertNull(vehicle.getEngineType());
    }

    @Test
    public void testSetDefaultDataGenerators() {
        DefaultDataLimiter limiters = new DefaultDataLimiter();
        limiters.addDefaultLimiter(new IntegerLimiter(10, 10));
        limiters.addDefaultLimiter(new StringLimiter(ReadableCharList.LIST_OF_ALPHA_CHAR));

        PojoGenerator<Vehicle> generator = new PojoGeneratorBuilder<>(Vehicle.class)
                .setDefaultDataGenerators(limiters)
                .build();

        Vehicle vehicle = generator.generatePojo();

        assertTrue(vehicle.getMaxSpeed() > 9 && vehicle.getMaxSpeed() < 20);
        assertTrue(vehicle.getNumWheels() > 9 && vehicle.getNumWheels() < 20);
        Arrays.asList(vehicle.getName().toCharArray()).forEach(ReadableCharList.LIST_OF_ALL_CHAR::contains);
        assertNotNull(vehicle.getEngineType());
    }

    @Test
    public void testSetDefaultDataGenerators_overWritten() {
        DefaultDataLimiter limiters = new DefaultDataLimiter();
        limiters.addDefaultLimiter(new IntegerLimiter(10, 10));
        limiters.addDefaultLimiter(new StringLimiter(ReadableCharList.LIST_OF_ALPHA_CHAR));

        PojoGenerator<Vehicle> generator = new PojoGeneratorBuilder<>(Vehicle.class)
                .setDefaultDataGenerators(limiters)
                .andLimitAllFieldsOf(new StringLimiter(ReadableCharList.LIST_OF_CHAR_DIGITS))
                .build();

        Vehicle vehicle = generator.generatePojo();

        assertTrue(vehicle.getMaxSpeed() > 9 && vehicle.getMaxSpeed() < 20);
        assertTrue(vehicle.getNumWheels() > 9 && vehicle.getNumWheels() < 20);
        Arrays.asList(vehicle.getName().toCharArray()).forEach(ReadableCharList.LIST_OF_CHAR_DIGITS::contains);
        assertNotNull(vehicle.getEngineType());
    }

    @Test
    public void testGenerateObjectWithoutDefaultConstructor() {
        PojoGenerator<ParameterConstructor> generator = new PojoGeneratorBuilder<>(ParameterConstructor.class)
                .withConstructors(78, "Hello world")
                .andIgnoreField("someNumber")
                .andIgnoreField("someString")
                .build();

        ParameterConstructor constructor = generator.generatePojo();

        assertEquals(78, constructor.getSomeNumber());
        assertEquals("Hello world", constructor.getSomeString());
        assertTrue(constructor.getSomeRandomNumber() != 0);
        assertTrue(!constructor.getSomeRandomString().isEmpty());

    }

    @Test
    public void testGenerateObjectWithoutDefaultConstructor_int() {
        PojoGenerator<ParameterConstructor> generator = new PojoGeneratorBuilder<>(ParameterConstructor.class)
                .withConstructors(78)
                .andIgnoreField("someNumber")
                .build();

        ParameterConstructor constructor = generator.generatePojo();

        assertEquals(78, constructor.getSomeNumber());
        assertTrue(constructor.getSomeRandomNumber() != 0);
        assertTrue(!constructor.getSomeString().isEmpty());
        assertTrue(!constructor.getSomeRandomString().isEmpty());

    }

    @Test
    public void testGenerateObjectWithoutDefaultConstructor_string() {
        PojoGenerator<ParameterConstructor> generator = new PojoGeneratorBuilder<>(ParameterConstructor.class)
                .withConstructors("Hello world")
                .andIgnoreField("someString")
                .build();

        ParameterConstructor constructor = generator.generatePojo();

        assertEquals("Hello world", constructor.getSomeString());
        assertTrue(constructor.getSomeNumber() != 0);
        assertTrue(constructor.getSomeRandomNumber() != 0);
        assertTrue(!constructor.getSomeRandomString().isEmpty());

    }

    // Generic testing on Methods
    @Test
    public void testGenericInterfaceBeingPopulated() {
        PojoGenerator<ClassExtendsWithSpecificGeneric> generator = new PojoGeneratorBuilder<>(ClassExtendsWithSpecificGeneric.class)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .build();
        ClassExtendsWithSpecificGeneric generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test
    public void testGenericPartiallyPopulatedWithHint() {
        PojoGenerator<ClassExtendsSomeGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsSomeGenerics.class)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .withGenericTypes(String.class)
                .build();
        ClassExtendsSomeGenerics generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test(expected = ConfusedGenericException.class)
    public void testGenericPartiallyPopulatedWithHint_throwsException() {
        PojoGenerator<ClassExtendsSomeGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsSomeGenerics.class)
                .andLimitField("j", new ConstantValueLimiter<>(1))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .build();
        ClassExtendsSomeGenerics generated = generator.generatePojo();
        fail("Should have thrown a class cast exception");
    }

    @Test
    public void testGenericWithPopulatedWithHint() {
        PojoGenerator<ClassExtendsWithNoGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsWithNoGenerics.class)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .withGenericTypes(String.class, Integer.class)
                .build();
        ClassExtendsWithNoGenerics generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test(expected = ConfusedGenericException.class)
    public void testGenericPopulatedWithHint_throwsException() {
        PojoGenerator<ClassExtendsWithNoGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsWithNoGenerics.class)
                .andLimitField("j", new ConstantValueLimiter<>(1))
                .withGenericTypes(String.class)
                .build();
        ClassExtendsWithNoGenerics generated = generator.generatePojo();
        fail("Should have thrown a class cast exception");
    }

    // Generic testing on Fields
    @Test
    public void testGenericInterfaceBeingPopulated_fieldAnalyzer() {
        PojoGenerator<ClassExtendsWithSpecificGeneric> generator = new PojoGeneratorBuilder<>(ClassExtendsWithSpecificGeneric.class)
                .andUseAnalyzer(Analyzers.ALL_FIELDS)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .build();
        ClassExtendsWithSpecificGeneric generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test
    public void testGenericPartiallyPopulatedWithHint_fieldAnalyzer() {
        PojoGenerator<ClassExtendsSomeGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsSomeGenerics.class)
                .andUseAnalyzer(Analyzers.ALL_FIELDS)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .withGenericTypes(String.class)
                .build();
        ClassExtendsSomeGenerics generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test(expected = ConfusedGenericException.class)
    public void testGenericPartiallyPopulatedWithHint_throwsException_fieldAnalyzer() {
        PojoGenerator<ClassExtendsSomeGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsSomeGenerics.class)
                .andUseAnalyzer(Analyzers.ALL_FIELDS)
                .andLimitField("j", new ConstantValueLimiter<>(1))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .build();
        ClassExtendsSomeGenerics generated = generator.generatePojo();
        fail("Should have thrown a class cast exception");
    }

    @Test
    public void testGenericWithPopulatedWithHint_fieldAnalyzer() {
        PojoGenerator<ClassExtendsWithNoGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsWithNoGenerics.class)
                .andUseAnalyzer(Analyzers.ALL_FIELDS)
                .andLimitField("j", new ConstantValueLimiter<>("Hello World"))
                .andLimitField("k", new ConstantValueLimiter<>(9876))
                .withGenericTypes(String.class, Integer.class)
                .build();
        ClassExtendsWithNoGenerics generated = generator.generatePojo();
        assertNotNull(generated);
        assertEquals("Hello World", generated.getJ());
        assertEquals((Integer) 9876, (Integer) generated.getK());
    }

    @Test(expected = ConfusedGenericException.class)
    public void testGenericPopulatedWithHint_throwsException_fieldAnalyzer() {
        PojoGenerator<ClassExtendsWithNoGenerics> generator = new PojoGeneratorBuilder<>(ClassExtendsWithNoGenerics.class)
                .andUseAnalyzer(Analyzers.ALL_FIELDS)
                .andLimitField("j", new ConstantValueLimiter<>(1))
                .withGenericTypes(String.class)
                .build();
        ClassExtendsWithNoGenerics generated = generator.generatePojo();
        fail("Should have thrown a class cast exception");
    }

    // Correlation limiter
    @Test
    public void testCorrelationLimiter_constantValue() {
        PojoGenerator<Person> generator = new PojoGeneratorBuilder<>(Person.class)
                .andLimitField("name", new CorrelationLimiter<>((random, age) -> {
                    return "Jimmy is " + age.get();
                }, "age"))
                .andLimitField("age", new ConstantValueLimiter<>(14))
                .build();

        Person person = generator.generatePojo();

        assertEquals(14, person.getAge());
        assertEquals("Jimmy is 14" , person.getName());
    }

    @Test
    public void testCorrelationLimiter_variableData() {
        PojoGenerator<Person> generator = new PojoGeneratorBuilder<>(Person.class)
                .andLimitField("name", new CorrelationLimiter<>((random, age) -> {
                    Integer supplierOutput = (Integer) age.get();
                    String output = "" + supplierOutput * 2;
                    return output;
                }, "age"))
                .andLimitField("age", new IntegerLimiter(10, 10))
                .lazilyEvaluate()
                .build();

        for (int i = 0; i < 100; i ++) {
            Person person = generator.generatePojo();
            int age = person.getAge();
            assertTrue(age >= 10 && age < 20);
            int stringAge = Integer.valueOf(person.getName());
            assertTrue(stringAge == (2 * age));
        }
    }

    @Test
    public void testCorrelationLimiter_multipleDependencies() {
        PojoGenerator<Vehicle> generator = new PojoGeneratorBuilder<>(Vehicle.class)
                .lazilyEvaluate()
                .andLimitField("maxSpeed" , new IntegerLimiter(20, 50))
                .andLimitField("numWheels", MultiplexLimiter.generateFlatDistribution(new ConstantValueLimiter<>(2), new ConstantValueLimiter<>(4)))
                .andLimitField("name", new CorrelationLimiter<String>((random, supplierMap) -> {
                    int wheels = (Integer) supplierMap.get("numWheels").get();
                    int speed = (Integer) supplierMap.get("maxSpeed").get();

                    String vehicleType = wheels == 2 ? "Motorcycle" : "Car";
                    String speedType = speed < 60 ? "Slow " : "Fast ";

                    return speedType + vehicleType;
                }, Arrays.asList("maxSpeed", "numWheels")))
                .build();

        for (int i = 0; i < 100 ; i ++) {
            Vehicle vehicle = generator.generatePojo();
            String output = vehicle.getName();
            if (vehicle.getMaxSpeed() < 60) {
                assertTrue(output.startsWith("Slow "));
            } else {
                assertTrue(output.startsWith("Fast "));
            }
            if (vehicle.getNumWheels() == 2) {
                assertTrue(output.endsWith("Motorcycle"));
            } else {
                assertTrue(output.endsWith("Car"));
            }
        }

    }

    // Map testing
    @Test
    public void testMapGeneration() {
        PojoGenerator<SuperheroNetwork> networkPojoGenerator = new PojoGeneratorBuilder<>(SuperheroNetwork.class)
                .build();

        for (int i = 0; i < 100; i++) {
            SuperheroNetwork network = networkPojoGenerator.generatePojo();
            Map<City, Superhero> protectionMap = network.getProtectorMap();
            for (City city : protectionMap.keySet()) {
                Arrays.asList(City.values()).contains(city);
            }
        }

    }

}
