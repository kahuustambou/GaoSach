package com.example.gaosach.Common;

import android.content.Intent;

import com.example.gaosach.Home;
import com.example.gaosach.MainActivity;
import com.example.gaosach.Model.Request;
import com.example.gaosach.Model.User;
import com.example.gaosach.Remote.APIService;
import com.example.gaosach.Remote.FCMRetroClient;
import com.example.gaosach.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static Intent nextIntent;

    public static String topicName = "News";
    public static Request currentRequest;
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";
    public static final String RATING_TIME = "ratingTime";
    public static final String DELETE = "Delete";
    public static String PHONE_TEXT = "userPhone";
    public static final String INSERT_RICE_ID = "RiceId";
    public static final String fcmUrl = "https://fcm.googleapis.com/";
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static APIService getFCMClient() {
        return FCMRetroClient.getClient(fcmUrl).create(APIService.class);
    }

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Đặt hàng";
        else if (status.equals("1"))
            return "Đang trên đường giao";
        else
            return "Giao hàng";
    }

    public static String getDate(Long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString());
        return date.toString();
    }
}
