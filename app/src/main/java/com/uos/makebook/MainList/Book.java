package com.uos.makebook.MainList;

import android.graphics.Bitmap;

public class Book {
    int id;
    String name;
    Bitmap cover;

    public Book(){}

    public Book(int id, String name, Bitmap cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
