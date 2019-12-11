package com.example.gaosach.Common;

import com.example.gaosach.Model.User;

public class Common {
    public static User currentUser;

//<<<<<<< HEAD

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Đặt hàng";
        else if(status.equals("1"))
            return "Đang trên đường giao";
        else
            return "Giao hàng";

    }
//=======
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
//>>>>>>> 3d41be5112c10c44bff1f14cccd5de5f16b16629
}
