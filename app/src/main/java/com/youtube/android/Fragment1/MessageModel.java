package com.youtube.android.Fragment1;

/**
 * Created by Win8.1 on 10/9/2017.
 */

public class MessageModel  {

    String message,gravity;

    public MessageModel() {
    }

    public MessageModel(String message, String gravity) {

        this.message = message;
        this.gravity = gravity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }
}
