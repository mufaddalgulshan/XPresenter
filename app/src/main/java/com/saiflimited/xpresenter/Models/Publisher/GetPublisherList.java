package com.saiflimited.xpresenter.Models.Publisher;

import java.util.ArrayList;

/**
 * Created by mufaddalgulshan on 20/11/14.
 */
public class GetPublisherList {

    int returnCode;
    String message;
    ArrayList<Publisher> publisherList;

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPublisherList(ArrayList<Publisher> publisherList) {
        this.publisherList = publisherList;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Publisher> getPublisherList() {
        return publisherList;
    }
}
