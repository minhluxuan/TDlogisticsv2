package com.example.demo.models;

public class ReponseObject<T> {
    private boolean error;
    private String message;
    private T data;

    public ReponseObject() {
    }

    public ReponseObject(boolean error, String message, T data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public boolean isError() {
        return error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setError(boolean error) {
        this.error = error;
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
}
