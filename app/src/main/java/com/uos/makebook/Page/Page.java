package com.uos.makebook.Page;

import com.uos.makebook.Common.DTO;
import com.uos.makebook.Page.Element.ElementData;
import com.uos.makebook.Page.Element.ImageData;
import com.uos.makebook.Page.Element.TextData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Page implements DTO {
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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
        System.out.println("[" + contents + "]");
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
        // JSON 데이터를 파싱하여 그림, 텍스트 데이터(ElementData)를 알아낸다.
        try {
            elements = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(contents);

            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String kind = jsonObject.getString("kind");

                // 공통 속성 파싱
                int x = jsonObject.getInt("x");
                int y = jsonObject.getInt("y");
                int width = jsonObject.getInt("width");
                int height = jsonObject.getInt("height");

                ElementData data = null;
                switch (kind) {
                    case "image":
                        String b64Data = jsonObject.getString("b64Data");
                        data = new ImageData(b64Data, x, y, width, height);
                        break;
                    case "text":
                        String value = jsonObject.getString("value");
                        data = new TextData(value, x, y, width, height);
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
}

