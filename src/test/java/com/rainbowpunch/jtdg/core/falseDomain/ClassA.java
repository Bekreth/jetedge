package com.rainbowpunch.jtdg.core.falseDomain;

import lombok.Data;

import java.util.List;

/**
 * False pojo for testing
 */
@Data
public class ClassA {
    private String field1;
    private Integer field2;
    private Boolean field3;
    private ClassB objB;
    private List<ClassC> objC;
}
