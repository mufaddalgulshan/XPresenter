package com.saiflimited.xpresenter.Models.User;

public class User {
    String username;
    String name;
    String mobileNumber;
    String loginRestriction;
    int maxUserSyncDelta;
    String timezone;
    String lastUpdate;

    String decimalSep;
    String format;
    int id;
    String language;
    String lastLoginDate;
    String lastSyncDate;
    String pin;

    public String getUsername() {
        if (username == null) {
            return "";
        } else {
            return username;
        }
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getMobileNumber() {
        if (mobileNumber == null) {
            return "";
        } else {
            return mobileNumber;
        }
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLoginRestriction() {
        if (loginRestriction == null) {
            return "";
        } else {
            return loginRestriction;
        }
    }

    public void setLoginRestriction(String loginRestriction) {
        this.loginRestriction = loginRestriction;
    }

    public int getMaxUserSyncDelta() {
        return maxUserSyncDelta;
    }

    public void setMaxUserSyncDelta(int maxUserSyncDelta) {
        this.maxUserSyncDelta = maxUserSyncDelta;
    }

    public String getTimezone() {
        if (timezone == null) {
            return "";
        } else {
            return timezone;
        }
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLastUpdate() {
        if (lastUpdate == null) {
            return "";
        } else {
            return lastUpdate;
        }
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDecimalSep() {
        if (decimalSep == null) {
            return "";
        } else {
            return decimalSep;
        }
    }

    public void setDecimalSep(String decimalSep) {
        this.decimalSep = decimalSep;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        if (language == null) {
            return "";
        } else {
            return language;
        }
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastLoginDate() {
        if (lastLoginDate == null) {
            return "";
        } else {
            return lastLoginDate;
        }
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastSyncDate() {
        if (lastSyncDate == null) {
            return "";
        } else {
            return lastSyncDate;
        }
    }

    public void setLastSyncDate(String lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    public String getPin() {
        if (pin == null) {
            return "";
        } else {
            return pin;
        }
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}