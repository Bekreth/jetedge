package com.rainbowpunch.jtdg.core.falseDomain;

import lombok.EqualsAndHashCode;

/**
 * An entity of all the Pojo's attributes
 */
@EqualsAndHashCode
public class ClassD {

    private String field1;
    private int field2;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }
}
