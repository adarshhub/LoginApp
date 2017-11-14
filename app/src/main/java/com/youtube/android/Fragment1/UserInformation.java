package com.youtube.android.Fragment1;

class UserInformation {

    private String name,email,password,status,image,uid,thumbnail;

    UserInformation(){

    }



    UserInformation(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        status="Hey there! I am using this chat app";
        image="default";
        uid="default";
        thumbnail="default";
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
