package com.uos.makebook.Edit;

import android.graphics.Bitmap;

import com.uos.makebook.Common.DTO;

public class Page implements DTO {
    int id = 0; // 어차피 insert 시에는 auto_increment
    int book_id;
    int idx;
    String text;
    Bitmap img;

    public Page(){}

    public Page(int book_id, int idx, String text, Bitmap img) {
        this.book_id = book_id;
        this.idx = idx;
        this.text = text;
        this.img = img;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() { return book_id; }

    public void setBookId(int id) {
        this.book_id = id;
    }

    public int getIdx() { return idx; }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getText() { return text; }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap cover) {
        this.img = img;
    }
}

