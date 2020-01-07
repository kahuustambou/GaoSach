package com.example.gaosach.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String phone;
    private String password;
    private String code;
    private String address;
    private boolean isStaff = false;

    public User() {
    }

    public User(String name, String phone, String password, boolean isStaff, String code, String address) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.isStaff = isStaff;
        this.code = code;
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("phone", phone);
        result.put("password", password);
        result.put("code", code);
        result.put("isStaff", isStaff);
        result.put("address", address);
        return result;
    }
}
