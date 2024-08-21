package com.example.myapplication;

public class SingerItem2 {
    private String name;
    private String mobile;
    private int resId;

    // 기본 생성자 (Firebase에서 객체를 역직렬화할 때 필요)
    public SingerItem2() {
        // 빈 생성자
    }

    // 매개변수를 받는 생성자
    public SingerItem2(String name, String mobile, int resId) {
        this.name = name;
        this.mobile = mobile;
        this.resId = resId;
    }

    // Getter와 Setter 메서드
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

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "SingerItem2{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", resId=" + resId +
                '}';
    }
}
