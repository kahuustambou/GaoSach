package com.example.gaosach.Common;

import android.util.Patterns;

import java.util.regex.Pattern;

public class Validator {
    public static String addressPattern = "\\d+\\s+([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]";
    public static String phoneNumberPattern = "^[+]{0,1}[0-9]{5,13}$";

    public static boolean isMatchPattern(String target, String pattern) {
//        return target.matches(pattern);
        return Pattern.matches(target, pattern);
    }

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
//        return isMatchPattern(phoneNumber, phoneNumberPattern);

        if (phoneNumber.length() != 10) {
            return false;
        }

        return true;
    }

    public static boolean isAddress(String address) {
        return isMatchPattern(address, addressPattern);
    }

    public static boolean isPassword(String password) {
        if (password.length() < 6) {
            return false;
        }

        return true;
    }
}
