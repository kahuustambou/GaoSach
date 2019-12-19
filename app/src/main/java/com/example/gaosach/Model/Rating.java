package com.example.gaosach.Model;

public class Rating {
    private String userPhone;
    private String riceId;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String riceId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.riceId = riceId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRiceId() {
        return riceId;
    }

    public void setRiceId(String riceId) {
        this.riceId = riceId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
