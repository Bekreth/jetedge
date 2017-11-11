package com.rainbowpunch.jtdg.core.falseDomain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Car {
    private int topSpeed;
    private int doorCount;
    private String make;
    private String model;
    private Door door;
}
