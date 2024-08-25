package com.example.myapplication;

/**
 * * 사용자 계정 정보 모델 클래스
 */
public class UserAccount
{
    private String idToken; // firbass Uid(고유 토큰정보)
    private String emailId; // 이메일 아이디
    private String password;  // 비밀번호
    private String name;
    private String Birth;
    private String Birth2;
    private String Birth3;
    private String mail;
    private String profileImageUrl; // 프로필 이미지 URL을 저장할 필드 추가

    public UserAccount() { }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return Birth;
    }

    public void setBirth(String birth) {
        Birth = birth;
    }

    public String getBirth2() {
        return Birth2;
    }

    public void setBirth2(String birth2) {
        Birth2 = birth2;
    }

    public String getBirth3() {
        return Birth3;
    }

    public void setBirth3(String birth3) {
        Birth3 = birth3;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
