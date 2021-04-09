package com.milk4u.doorstep.delivery.response;

import java.util.List;

public class DroplistResponse {
    private int cstId;
    private String email;
    private String fName;
    private String lName;
    private String postcode;
    private List<CustomerResponse> orders;

    public int getCstId() {
        return cstId;
    }

    public void setCstId(int cstId) {
        this.cstId = cstId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public List<CustomerResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<CustomerResponse> orders) {
        this.orders = orders;
    }
}
