package com.uos.makebook.Page;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.uos.makebook.Common.DTO;

public class Page implements DTO, Parcelable {
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

    public Page(Parcel in){
        readFromParcel(in);
    }

    //Parcel -> Page
    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>(){
        @Override
        public Page createFromParcel(Parcel in){
            return new Page(in);
        }

        @Override
        public Page[] newArray(int i) {
            return new Page[i];
        }
    };

    public void readFromParcel(Parcel in){
        book_id = in.readLong();
        text = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
        nextPage = in.readLong();
        isHead = in.readInt();
        //page_img = in.readParcelable(Bitmap.class.getClassLoader());
    }

    //Page -> Parcel
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(book_id);
        parcel.writeString(text);
        parcel.writeParcelable(img,i);
        parcel.writeLong(nextPage);
        parcel.writeInt(isHead);
        //parcel.writeParcelable(page_img,i);
    }

    @Override
    public int describeContents() {
        return 0;
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

