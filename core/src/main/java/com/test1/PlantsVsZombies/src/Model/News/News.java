package com.test1.PlantsVsZombies.src.Model.News;

public class News {
    private boolean isRead;
    private String message;

    public News(String message) {
        this.message = message;
        isRead = false;
    }

    public News() {
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
