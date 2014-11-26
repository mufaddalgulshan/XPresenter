package com.saiflimited.xpresenter.Models.Publisher;

import java.util.ArrayList;

public class Publisher {
    String code;
    String name;
    String logo;
    String background;
    String appname;
    String messages;
    ArrayList<ContentType> contentTypeList;
    int id;
    String lastUpdated;

    public String getCode() {
        if (code == null) {
            return "";
        } else {
            return code;
        }
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getLogo() {
        if (logo == null) {
            return "";
        } else {
            return logo;
        }
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBackground() {
        if (background == null) {
            return "";
        } else {
            return background;
        }
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAppname() {
        if (appname == null) {
            return "";
        } else {
            return appname;
        }
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getMessages() {
        if (messages == null) {
            return "";
        } else {
            return messages;
        }
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public ArrayList<ContentType> getContentTypeList() {
        return contentTypeList;
    }

    public void setContentTypeList(ArrayList<ContentType> contentTypeList) {
        this.contentTypeList = contentTypeList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}