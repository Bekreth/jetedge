package com.rainbowpunch.jtdg.test;

import java.util.List;
import java.util.Map;

public final class Pojos {
    private Pojos() {}

    public static class Extra { }

    public static class Person {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public enum Power {
        FLIGHT,
        XRAY_VISION,
        SPIDER_SENSE,
        MONEY,
        SPEED
    }

    public static class Superhero extends Person {
        private List<Power> superPowers;
        private Person archNemesis;

        public List<Power> getSuperPowers() {
            return superPowers;
        }

        public void setSuperPowers(List<Power> superPowers) {
            this.superPowers = superPowers;
        }

        public Person getArchNemesis() {
            return archNemesis;
        }

        public void setArchNemesis(Person archNemesis) {
            this.archNemesis = archNemesis;
        }
    }

    public enum City {
        TOKYO,
        NEW_YORK,
        LOS_ANGELES,
        GOTHAM
    }

    public static class SuperheroNetwork {
        private String planetName;
        private Map<City, Superhero> protectorMap;

        public String getPlanetName() {
            return planetName;
        }

        public void setPlanetName(String planetName) {
            this.planetName = planetName;
        }

        public Map<City, Superhero> getProtectorMap() {
            return protectorMap;
        }

        public void setProtectorMap(Map<City, Superhero> protectorMap) {
            this.protectorMap = protectorMap;
        }
    }

    public static class Storyline {
        private Superhero superhero;
        private Person archNemesis;

        public Superhero getSuperhero() {
            return superhero;
        }

        public void setSuperhero(Superhero superhero) {
            this.superhero = superhero;
        }

        public Person getArchNemesis() {
            return archNemesis;
        }

        public void setArchNemesis(Person archNemesis) {
            this.archNemesis = archNemesis;
        }
    }

    public static class Vehicle {
        private Integer maxSpeed;
        private Integer numWheels;
        private String name;

        public Integer getMaxSpeed() {
            return maxSpeed;
        }

        public void setMaxSpeed(Integer maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        public Integer getNumWheels() {
            return numWheels;
        }

        public void setNumWheels(Integer numWheels) {
            this.numWheels = numWheels;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
