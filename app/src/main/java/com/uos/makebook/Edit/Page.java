package com.uos.makebook.Edit;

import android.graphics.Bitmap;

import com.uos.makebook.Common.DTO;

public class Page implements DTO {
    long id = 0; // 어차피 insert 시에는 auto_increment
    long book_id;
    String text;
    Bitmap img;
    long nextPage; // 연결리스트
    int isHead; // head node 인지 판별

    public Page(){}

    public Page(long book_id, String text, Bitmap img, long nextPage, int isHead) {
        this.book_id = book_id;
        this.text = text;
        this.img = img;
        this.nextPage = nextPage;
        this.isHead = isHead;
    }

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() { return book_id; }

    public void setBookId(long id) {
        this.book_id = id;
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

    public long getNextPage() {
        return nextPage;
    }

    public void setNextPage(long nextPage) {
        this.nextPage = nextPage;
    }

    public int getIsHead() {
        return isHead;
    }

    public void setIsHead(int isHead) {
        this.isHead = isHead;
    }

}

