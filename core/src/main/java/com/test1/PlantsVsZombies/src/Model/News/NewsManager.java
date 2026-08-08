package com.test1.PlantsVsZombies.src.Model.News;

import java.util.ArrayList;

public class NewsManager {

    private ArrayList<News> news = new ArrayList<>();

    public NewsManager() {
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public ArrayList<String> extractUnreadNews() {
        ArrayList<String> newsMessages = new ArrayList<>();
        for (News news1 : news) {
            if (!news1.isRead()) {
                news1.setRead(true);
                newsMessages.add(news1.getMessage());
            }
        }
        return newsMessages;
    }


    public ArrayList<String> extractAllNews() {
        ArrayList<String> newsMessages = new ArrayList<>();
        for (News news1 : news) {
            if (!news1.isRead())
                news1.setRead(true);
            newsMessages.add(news1.getMessage());
        }
        return newsMessages;
    }


    public void addNews(News news) {
        this.news.add(news);
    }

}
