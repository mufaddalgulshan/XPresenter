package com.saiflimited.xpresenter.Models.Content;

import android.net.Uri;

/**
 * Created by mufaddalgulshan on 22/11/14.
 */
public class ContentItem {

    String id;
    String name;
    String icon;
    Uri iconUri;
    String htmlBase64;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getHtmlBase64() {
        return htmlBase64;
    }

    public void setHtmlBase64(String htmlBase64) {
        this.htmlBase64 = htmlBase64;
    }

    public void setIconUri(Uri iconUri) {
        this.iconUri = iconUri;
    }
}
