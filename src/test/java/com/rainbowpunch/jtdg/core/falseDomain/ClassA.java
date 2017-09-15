package com.rainbowpunch.jtdg.core.falseDomain;

import lombok.Data;

import java.util.List;

/**
 * False pojo for testing
 */
@Data
public class ClassA {

    private String field1;
    private int field2;
    private ClassB objB;
    private List<ClassC> objC;
    private ClassE objE;
}
