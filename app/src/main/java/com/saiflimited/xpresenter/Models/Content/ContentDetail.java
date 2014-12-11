package com.saiflimited.xpresenter.Models.Content;

import java.util.ArrayList;

/**
 * Created by mufaddalgulshan on 22/11/14.
 */
public class ContentDetail {
    String seq;
    String name;
    ArrayList<ContentItemList> contentItemList;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ContentItemList> getContentItemList() {
        return contentItemList;
    }

    public void setContentItemList(ArrayList<ContentItemList> contentItemList) {
        this.contentItemList = contentItemList;
    }
}
