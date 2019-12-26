package com.example.gaosach.Model;

public class Favourites {
    private String RiceId,RiceName,RicePrice,RiceMenuId,RiceImage,RiceDiscount,RiceDescription,UserPhone;

    public Favourites() {
    }

    public Favourites(String riceId, String riceName, String ricePrice, String riceMenuId, String riceImage, String riceDiscount, String riceDescription, String userPhone) {
        RiceId = riceId;
        RiceName = riceName;
        RicePrice = ricePrice;
        RiceMenuId = riceMenuId;
        RiceImage = riceImage;
        RiceDiscount = riceDiscount;
        RiceDescription = riceDescription;
        UserPhone = userPhone;
    }

    public String getRiceId() {
        return RiceId;
    }

    public void setRiceId(String riceId) {
        RiceId = riceId;
    }

    public String getRiceName() {
        return RiceName;
    }

    public void setRiceName(String riceName) {
        RiceName = riceName;
    }

    public String getRicePrice() {
        return RicePrice;
    }

    public void setRicePrice(String ricePrice) {
        RicePrice = ricePrice;
    }

    public String getRiceMenuId() {
        return RiceMenuId;
    }

    public void setRiceMenuId(String riceMenuId) {
        RiceMenuId = riceMenuId;
    }

    public String getRiceImage() {
        return RiceImage;
    }

    public void setRiceImage(String riceImage) {
        RiceImage = riceImage;
    }

    public String getRiceDiscount() {
        return RiceDiscount;
    }

    public void setRiceDiscount(String riceDiscount) {
        RiceDiscount = riceDiscount;
    }

    public String getRiceDescription() {
        return RiceDescription;
    }

    public void setRiceDescription(String riceDescription) {
        RiceDescription = riceDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
