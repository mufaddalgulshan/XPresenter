package com.saiflimited.xpresenter.Models.ContentData;

import java.util.ArrayList;

/**
 * Created by mufaddalgulshan on 22/11/14.
 */
public class Content {
    String brand;
    String activity;
    String goal;
    String dateStart;
    String dateEnd;
    String reachDescription;
    String rule;
    String implementedBy;

    ArrayList<ContentDetail> contentDetailList;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getReachDescription() {
        return reachDescription;
    }

    public void setReachDescription(String reachDescription) {
        this.reachDescription = reachDescription;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getImplementedBy() {
        return implementedBy;
    }

    public void setImplementedBy(String implementedBy) {
        this.implementedBy = implementedBy;
    }

    public ArrayList<ContentDetail> getContentDetailList() {
        return contentDetailList;
    }

    public void setContentDetailList(ArrayList<ContentDetail> contentDetailList) {
        this.contentDetailList = contentDetailList;
    }
}
