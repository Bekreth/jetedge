package com.rainbowpunch.jetedge.core.falseDomain;

import lombok.Data;

import java.util.List;

/**
 * False pojo for testing
 */
@Data
public class ClassB {

    private String field1;
    private List<String> field2;
    private ClassD objD;
    private Character character;
    private EnumZ enumZ;

}
