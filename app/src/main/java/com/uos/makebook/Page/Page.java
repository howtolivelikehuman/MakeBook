package com.uos.makebook.Page;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.uos.makebook.Common.DTO;
import com.uos.makebook.Page.Element.ElementData;
import com.uos.makebook.Page.Element.ImageData;
import com.uos.makebook.Page.Element.TextData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Page implements DTO, Parcelable {
    long id = 0; // 어차피 insert 시에는 auto_increment
    long book_id;
    String contents; // 책의 내용을 JSON 형식으로 표현.
    long nextPage; // 연결리스트
    int isHead; // head node 인지 판별
    ArrayList<ElementData> elements; // 페이지에 있는 객체들의 리스트

    public long getPageId() {
        return id;
    }

    public void setPageId(long id) {
        this.id = id;
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

    public String getContents() {
        updateContents();
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
        parseContents();
    }

    public long getBookId() {
        return book_id;
    }

    public void setBookId(long id) {
        this.book_id = id;
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

    public Page() {
        elements = new ArrayList<>();
    }

    public Page(long book_id, String contents, long nextPage, int isHead) {
        this.book_id = book_id;
        this.contents = contents;
        this.nextPage = nextPage;
        this.isHead = isHead;

        if (this.contents.isEmpty()) {
            this.contents = "[]";
        }

        parseContents();
    }

    private void parseContents() {
        /*
        Text 예시
        {
            "kind": "text",
            "x": 100,
            "y": 100,
            "width": 400,
            "height": 400,
            "value": "안녕하세요~",
            "fontSize": 32,
            "fontColor": -16777216
        },

        Image 예시
        {
            "kind": "image",
            "x": 100,
            "y": 100,
            "width": 400,
            "height": 400,
            "src": "/~/~/example.png"
        },
        */

        // JSON 데이터를 파싱하여 그림, 텍스트 데이터(ElementData)를 알아낸다.
        try {
            elements = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(contents);

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String kind = jsonObject.getString("kind");

                ElementData data = null;
                switch (kind) {
                    case "image":
                        data = new ImageData(jsonObject);
                        break;
                    case "text":
                        data = new TextData(jsonObject);
                        break;
                    default:
                        break;
                }
                elements.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateContents() {
        JSONArray jsonArray = new JSONArray();

        for (ElementData element : elements) {
            jsonArray.put(element.toJSON());
        }

        contents = jsonArray.toString();
    }

    public void drawElements(Canvas canvas) {
        for (ElementData element : elements) {
            element.drawOn(canvas);
        }
    }

    public ElementData findElement(float x, float y) {
        for (ElementData element : elements) {
            if (element.isContaining(x, y)) {
                return element;
            }
        }
        return null;
    }

    public void addText(String value, int fontSize, int fontColor) {
        elements.add(new TextData(value, fontSize, fontColor));
        updateContents();
    }
}

