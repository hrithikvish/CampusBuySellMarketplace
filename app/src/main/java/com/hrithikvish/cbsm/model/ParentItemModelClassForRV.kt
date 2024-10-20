package com.hrithikvish.cbsm.model;

import java.util.ArrayList;

public class ParentItemModelClassForRV {

    String collegeName;
    ArrayList<PostModelClassForRV> postList;

    public ParentItemModelClassForRV() {}

    public ParentItemModelClassForRV(String collegeName, ArrayList<PostModelClassForRV> postList) {
        this.collegeName = collegeName;
        this.postList = postList;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public ArrayList<PostModelClassForRV> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<PostModelClassForRV> postList) {
        this.postList = postList;
    }

    @Override
    public String toString() {
        return "ParentItemModelClassForRV{" +
                "collegeName='" + collegeName + '}';
    }
}
