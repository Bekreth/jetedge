package com.rainbowpunch.jtdg.core.falseDomain;

import java.util.List;

/**
 * False pojo for testing
 */
public class ClassA {

    private String field1;
    private Integer field2;
    private Boolean field3;
    private ClassB objB;
    private List<ClassC> objC;

    public String getField1() {
        return field1;
    }

    private void setField1(String field1) {
        this.field1 = field1;
    }

    public Integer getField2() {
        return field2;
    }

    private void setField2(Integer field2) {
        this.field2 = field2;
    }

    public boolean isField3() {
        return field3;
    }

    public void setField3(boolean field3) {
        this.field3 = field3;
    }

    public ClassB getObjB() {
        return objB;
    }

    private void setObjB(ClassB objB) {
        this.objB = objB;
    }

    public List<ClassC> getObjC() {
        return objC;
    }

    private void setObjC(List<ClassC> objC) {
        this.objC = objC;
    }
}
