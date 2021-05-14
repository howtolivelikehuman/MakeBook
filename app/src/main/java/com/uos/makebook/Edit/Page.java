package com.uos.makebook.Edit;

import android.graphics.Bitmap;

import com.uos.makebook.Common.DTO;

public class Page implements DTO {
    long id = 0; // 어차피 insert 시에는 auto_increment
    long book_id;
    String text;
    Bitmap img;
    long nextPage; // idx를 넣는 대신 자체 참조 속성을 넣어서 다음 페이지를 가리키게 함

    public Page(){}

    public Page(long book_id, String text, Bitmap img, long nextPage) {
        this.book_id = book_id;
        this.text = text;
        this.img = img;
        this.nextPage = nextPage;
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

}

