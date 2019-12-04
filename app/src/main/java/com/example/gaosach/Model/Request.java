package com.example.gaosach.Model;

import java.util.List;

public class Request {
    private String email;
    private String name;
    private String address;
    private String total;
    private String status;
    private List<Order> rices;

    public Request() {
    }

    public Request(String email, String name, String address, String total, String status, List<Order> rices) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.rices = rices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Order> getRices() {
        return rices;
    }

    public void setRices(List<Order> rices) {
        this.rices = rices;
    }
}
