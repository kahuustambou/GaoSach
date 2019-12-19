package com.example.gaosach.Model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String phone;
    private String password;
    private String sourceCode;
    private boolean isStaff = false;

    public User() {
    }

    public User(String name, String phone, String password, boolean isStaff, String sourceCode) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.isStaff = isStaff;
        this.sourceCode= sourceCode;

    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
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
        result.put("password", password);
        result.put("isStaff", isStaff);
        return result;
    }
}
