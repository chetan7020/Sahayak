package com.example.saftytracking.notification;

import java.util.UUID;

public class FloorModel {
    private String floorId;
    private String floorName;
    private CornerModel cornerModel1;
    private CornerModel cornerModel2;
    private CornerModel cornerModel3;
    private CornerModel cornerModel4;
    private CornerModel cornerModel5;
    private CornerModel cornerModel6;
    private CornerModel cornerModel7;
    private CornerModel cornerModel8;

    public FloorModel() {
        this.floorId = UUID.randomUUID().toString();
        this.cornerModel1 = new CornerModel();
        this.cornerModel2 = new CornerModel();
        this.cornerModel3 = new CornerModel();
        this.cornerModel4 = new CornerModel();
        this.cornerModel5 = new CornerModel();
        this.cornerModel6 = new CornerModel();
        this.cornerModel7 = new CornerModel();
        this.cornerModel8 = new CornerModel();
    }

    public FloorModel(String floorId, String floorName, CornerModel cornerModel1, CornerModel cornerModel2, CornerModel cornerModel3, CornerModel cornerModel4, CornerModel cornerModel5, CornerModel cornerModel6, CornerModel cornerModel7, CornerModel cornerModel8) {
        this.floorId = floorId;
        this.floorName = floorName;
        this.cornerModel1 = cornerModel1;
        this.cornerModel2 = cornerModel2;
        this.cornerModel3 = cornerModel3;
        this.cornerModel4 = cornerModel4;
        this.cornerModel5 = cornerModel5;
        this.cornerModel6 = cornerModel6;
        this.cornerModel7 = cornerModel7;
        this.cornerModel8 = cornerModel8;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public CornerModel getCornerModel1() {
        return cornerModel1;
    }

    public void setCornerModel1(CornerModel cornerModel1) {
        this.cornerModel1 = cornerModel1;
    }

    public CornerModel getCornerModel2() {
        return cornerModel2;
    }

    public void setCornerModel2(CornerModel cornerModel2) {
        this.cornerModel2 = cornerModel2;
    }

    public CornerModel getCornerModel3() {
        return cornerModel3;
    }

    public void setCornerModel3(CornerModel cornerModel3) {
        this.cornerModel3 = cornerModel3;
    }

    public CornerModel getCornerModel4() {
        return cornerModel4;
    }

    public void setCornerModel4(CornerModel cornerModel4) {
        this.cornerModel4 = cornerModel4;
    }

    public CornerModel getCornerModel5() {
        return cornerModel5;
    }

    public void setCornerModel5(CornerModel cornerModel5) {
        this.cornerModel5 = cornerModel5;
    }

    public CornerModel getCornerModel6() {
        return cornerModel6;
    }

    public void setCornerModel6(CornerModel cornerModel6) {
        this.cornerModel6 = cornerModel6;
    }

    public CornerModel getCornerModel7() {
        return cornerModel7;
    }

    public void setCornerModel7(CornerModel cornerModel7) {
        this.cornerModel7 = cornerModel7;
    }

    public CornerModel getCornerModel8() {
        return cornerModel8;
    }

    public void setCornerModel8(CornerModel cornerModel8) {
        this.cornerModel8 = cornerModel8;
    }

    @Override
    public String toString() {
        return "FloorModel{" +
                "floorId='" + floorId + '\'' +
                ", floorName='" + floorName + '\'' +
                ", cornerModel1=" + cornerModel1 +
                ", cornerModel2=" + cornerModel2 +
                ", cornerModel3=" + cornerModel3 +
                ", cornerModel4=" + cornerModel4 +
                ", cornerModel5=" + cornerModel5 +
                ", cornerModel6=" + cornerModel6 +
                ", cornerModel7=" + cornerModel7 +
                ", cornerModel8=" + cornerModel8 +
                '}';
    }
}