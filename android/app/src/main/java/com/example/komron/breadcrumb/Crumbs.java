package com.example.komron.breadcrumb;

public class Crumbs {
    private double latitude;
    private double longitude;
    private String userName = "";
    private String title = "";
    private String content = "";
    private String color = "";

    public Crumbs(double latitude, double longitude, String userName, String title, String content, String color) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.color = color;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getTitle(){
        return this.title;
    }

    public String getContent(){
        return this.content;
    }

    public String getColor() {return this.color;}

}
