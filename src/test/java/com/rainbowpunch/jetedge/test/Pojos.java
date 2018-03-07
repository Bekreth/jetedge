package com.rainbowpunch.jetedge.test;

import lombok.Data;

import java.util.List;
import java.util.Map;

public final class Pojos {
    private Pojos() {}

    public static class Extra { }

    @Data
    public static class Person {
        private String name;
        private int age;
        private char[] secretName;

    }

    public enum Power {
        FLIGHT,
        XRAY_VISION,
        SPIDER_SENSE,
        MONEY,
        SPEED
    }

    @Data
    public static class Superhero extends Person {
        private List<Power> superPowers;
        private Person archNemesis;

    }

    public enum City {
        TOKYO,
        NEW_YORK,
        LOS_ANGELES,
        GOTHAM
    }

    @Data
    public static class SuperheroNetwork {
        private String planetName;
        private Map<City, Superhero> protectorMap;

    }

    @Data
    public static class Storyline {
        private Superhero superhero;
        private Person archNemesis;
    }

    @Data
    public static class A<J> {
        J j;
    }

    @Data
    public static class B extends A<String> {

    }

    public enum Powerplant {
        ELECTRIC, GASOLINE
    }

    @Data
    public static class Vehicle {
        private int maxSpeed;
        private int numWheels;
        private String name;
        private Powerplant engineType; // Does not have public accessor
        public boolean hasTintedWindows; // Public, but does not have accessor

        // Has setter, but no underlying field
        public void setHasChrome(boolean hasChrome) { }
    }

    @Data
    public static class ParameterConstructor {

        private int someNumber;
        private int someRandomNumber;
        private String someString;
        private String someRandomString;

        public ParameterConstructor() {

        }

        public ParameterConstructor(String someString) {
            this.someString = someString;
        }

        public ParameterConstructor(int someNumber) {
            this.someNumber = someNumber;
        }

        public ParameterConstructor(int someNumber, String someString) {
            this.someNumber = someNumber;
            this.someString = someString;
        }
    }

}
