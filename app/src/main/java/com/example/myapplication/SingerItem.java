package com.example.myapplication;

public class SingerItem {
    private String id;
    private String name;
    private String mobile;
    private int recommendationCount;

    public SingerItem() {
    }

    public SingerItem(String id, String name, String mobile, int recommendationCount) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.recommendationCount = recommendationCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getRecommendationCount() {
        return recommendationCount;
    }

    public void setRecommendationCount(int recommendationCount) {
        this.recommendationCount = recommendationCount;
    }

    @Override
    public String toString() {
        return "SingerItem{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
