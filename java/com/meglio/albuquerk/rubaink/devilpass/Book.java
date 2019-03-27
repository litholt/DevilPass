package com.meglio.albuquerk.rubaink.devilpass;

public class Book {

    private String Title;
    private int Category ;

    public Book() {
    }

    public Book(String title, int category) {
        Title = title;
        Category = category;
    }

    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }

    public int getCategory() {
        return Category;
    }
    public void setCategory(int category) {
        Category = category;
    }
}