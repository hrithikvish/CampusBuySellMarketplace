package com.hrithikvish.cbsm.model;

import java.util.ArrayList;

public class ParentItemModelClassForRV {

    String collegeName;

    public ParentItemModelClassForRV() {}

    public ParentItemModelClassForRV(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    @Override
    public String toString() {
        return "ParentItemModelClassForRV{" +
                "collegeName='" + collegeName + '}';
    }
}
