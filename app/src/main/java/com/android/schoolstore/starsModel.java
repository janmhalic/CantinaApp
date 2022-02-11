package com.android.schoolstore;

public class starsModel {
    private String name;
    private String context;
    private String date;
    private float stars;

    public starsModel(String name, String context, String date, float stars) {
        this.name = name;
        this.context = context;
        this.date = date;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }
}
