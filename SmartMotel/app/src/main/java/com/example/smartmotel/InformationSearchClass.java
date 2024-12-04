package com.example.smartmotel;

public class InformationSearchClass {
    private String imageURL;
    private String username;
    private String address;
    private String usernameLogin;

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

    public String getUsernameLogin() {
        return usernameLogin;
    }

    public void setUsernameLogin(String usernameLogin) {
        this.usernameLogin = usernameLogin;
    }

    public InformationSearchClass(String imageURL, String username, String address, String usernameLogin) {
        this.imageURL = imageURL;
        this.username = username;
        this.address = address;
        this.usernameLogin = usernameLogin;
    }

    public InformationSearchClass() {

    }
}
