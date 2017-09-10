package com.rainbowpunch.jtdg.core.falseDomain;

import java.util.List;

/**
 * False pojo for testing
 */
public class ClassA {

    private String field1;
    private int field2;
    private ClassB objB;
    private List<ClassC> objC;

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

    public ClassB getObjB() {
        return objB;
    }

    public void setObjB(ClassB objB) {
        this.objB = objB;
    }

    public List<ClassC> getObjC() {
        return objC;
    }

    public void setObjC(List<ClassC> objC) {
        this.objC = objC;
    }
}
