package com.rainbowpunch.jtdg.core.falseDomain;

import lombok.Data;

import java.util.List;

@Data
public class ClassAchild extends ClassA {
    private String fieldAlpha;
    private int fieldBeta;
    private Boolean fieldGamma;
    private List<ClassEchild> echildList;
}
