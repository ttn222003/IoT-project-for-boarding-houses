package com.example.cesplasmaservice.SmartMotelService;

public class InformationManageClass {
    private String imageURL;
    private String username;
    private String address;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public InformationManageClass(String imageURL, String username, String address) {
        this.imageURL = imageURL;
        this.username = username;
        this.address = address;
    }

    public InformationManageClass() {

    }
}
