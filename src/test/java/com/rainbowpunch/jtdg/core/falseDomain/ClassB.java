package com.rainbowpunch.jtdg.core.falseDomain;

import java.util.List;

/**
 * False pojo for testing
 */
public class ClassB {

    private String field1;
    private List<String> field2;
    private ClassD objD;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public List<String> getField2() {
        return field2;
    }

    public void setField2(List<String> field2) {
        this.field2 = field2;
    }

    public ClassD getObjD() {
        return objD;
    }

    public void setObjD(ClassD objD) {
        this.objD = objD;
    }
}
