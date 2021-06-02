package com.uos.makebook.MainList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.uos.makebook.Common.DTO;

public class Book implements DTO, Parcelable {
    long id;
    String title;
    String writer;
    String createdate;
    Bitmap cover;
    int list_idx; //페이지 모아보기에서 클릭된 페이지의 pos


    public Book(){}

    public Book(long id, String name, String writer, String createdate, Bitmap cover) {
        this.id = id;
        this.title = name;
        this.writer = writer;
        this.createdate = createdate;
        this.cover = cover;
    }

    public Book(Parcel in) {
        readFromParcel(in);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate.split(" ")[0];
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public int getListIdx() {
        return list_idx;
    }

    public void setListIdx(int list_idx) {
        this.list_idx = list_idx;
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>(){
        public Book createFromParcel(Parcel in){
            return new Book(in);
        }

        @Override
        public Book[] newArray(int i) {
            return new Book[i];
        }
    };
    public void readFromParcel(Parcel in){
        id = in.readLong();
        title = in.readString();
        writer = in.readString();
        createdate = in.readString();
        cover = in.readParcelable(Bitmap.class.getClassLoader());
        list_idx = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(writer);
        parcel.writeString(createdate);
        parcel.writeParcelable(cover,i);
        parcel.writeInt(list_idx);
    }
}
