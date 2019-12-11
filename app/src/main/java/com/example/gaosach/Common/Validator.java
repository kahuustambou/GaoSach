package com.example.gaosach.Common;

import android.util.Patterns;

public class Validator {
    public static boolean isEmpty(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        return true;
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 10) {
            return false;
        }

        return true;
    }

    public static boolean isPassword(String password) {
        if (password.length() < 6) {
            return false;
        }

        return true;
    }
}
