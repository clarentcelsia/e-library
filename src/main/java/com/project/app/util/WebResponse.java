package com.project.app.util;

public class WebResponse <T>{
    private String message;
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public WebResponse() {
    }

    public WebResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
