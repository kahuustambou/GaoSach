package com.example.gaosach.Model;

import java.util.HashMap;
import java.util.Map;

import static com.example.gaosach.Common.Validator.isEmpty;

public class User {
    private String name;
    private String phone;
    private String password;
    private boolean isStaff = false;

    public User() {
    }

    public User(String name, String phoneNumber, String password, boolean isStaff) {
        this.name = name;
        this.phone = phoneNumber;
        this.password = password;
        this.isStaff = isStaff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(boolean isStaff) {
        this.isStaff = isStaff;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("phone", phone);
//        result.put("password", password);
        result.put("isStaff", isStaff);
        return result;
    }
}
