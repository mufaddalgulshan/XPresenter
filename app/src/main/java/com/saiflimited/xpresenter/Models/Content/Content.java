package com.saiflimited.xpresenter.Models.Content;

import java.util.ArrayList;

/**
 * Created by mufaddalgulshan on 22/11/14.
 */
public class Content {
    int id;
    String name;
    String format;
    String type;
    String fromDate;
    String toDate;
    String lastUpdated;
    String contentDocument;
    String from;
    String returnCode;
    String message;
    String brand;
    String activity;
    String goal;
    String dateStart;
    String dateEnd;
    String reachDescription;
    String rule;
    String implementedBy;
    String icon;
    String image;
    ArrayList<ContentDetail> contentDetailList;

    public String getBrand() {
        if (brand == null) {
            return "";
        } else {
            return brand;
        }
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getActivity() {
        if (activity == null) {
            return "";
        } else {
            return activity;
        }
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getGoal() {
        if (goal == null) {
            return "";
        } else {
            return goal;
        }
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDateStart() {
        if (dateStart == null) {
            return "";
        } else {
            return dateStart;
        }
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        if (dateEnd == null) {
            return "";
        } else {
            return dateEnd;
        }
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getReachDescription() {
        if (reachDescription == null) {
            return "";
        } else {
            return reachDescription;
        }
    }

    public void setReachDescription(String reachDescription) {
        this.reachDescription = reachDescription;
    }

    public String getRule() {
        if (rule == null) {
            return "";
        } else {
            return rule;
        }
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getImplementedBy() {
        if (implementedBy == null) {
            return "";
        } else {
            return implementedBy;
        }
    }

    public void setImplementedBy(String implementedBy) {
        this.implementedBy = implementedBy;
    }

    public String getMessage() {
        if (message == null) {
            return "";
        } else {
            return message;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReturnCode() {
        if (returnCode == null) {
            return "";
        } else {
            return returnCode;
        }
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        if (format == null) {
            return "";
        } else {
            return format;
        }
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        if (type == null) {
            return "";
        } else {
            return type;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromDate() {
        if (fromDate == null) {
            return "";
        } else {
            return fromDate;
        }
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        if (toDate == null) {
            return "";
        } else {
            return toDate;
        }
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getLastUpdated() {
        if (lastUpdated == null) {
            return "";
        } else {
            return lastUpdated;
        }
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getContentDocument() {
        if (contentDocument == null) {
            return "";
        } else {
            return contentDocument;
        }
    }

    public void setContentDocument(String contentDocument) {
        this.contentDocument = contentDocument;
    }

    public String getFrom() {
        if (from == null) {
            return "";
        } else {
            return from;
        }
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<ContentDetail> getContentDetailList() {
        return contentDetailList;
    }

    public void setContentDetailList(ArrayList<ContentDetail> contentDetailList) {
        this.contentDetailList = contentDetailList;
    }

    public String getIcon() {
        if (icon == null) {
            return "";
        } else {
            return icon;
        }
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        if (image == null) {
            return "";
        } else {
            return image;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }
}
