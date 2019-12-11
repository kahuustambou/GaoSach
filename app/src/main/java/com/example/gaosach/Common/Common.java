package com.example.gaosach.Common;

import com.example.gaosach.Model.User;

public class Common {
    public static User currentUser;


    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Đặt hàng";
        else if(status.equals("1"))
            return "Đang trên đường giao";
        else
            return "Giao hàng";

    }
}
