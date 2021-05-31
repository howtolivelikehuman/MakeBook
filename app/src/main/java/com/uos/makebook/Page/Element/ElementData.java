package com.uos.makebook.Page.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ElementData {
    public static final float RESIZE_BORDER = 14;

    private float x = 0, y = 0;
    private float width = 0, height = 0;
    private boolean isSelected = false;
    private Paint borderPaint;

    public ElementData(JSONObject jsonObject) {
        try {
            // 공통 속성 파싱
            x = (float) jsonObject.getDouble("x");
            y = (float) jsonObject.getDouble("y");
            width = (float) jsonObject.getDouble("width");
            height = (float) jsonObject.getDouble("height");

            borderPaint = new Paint();
            borderPaint.setAntiAlias(true);
            borderPaint.setStrokeCap(Paint.Cap.ROUND);
            borderPaint.setColor(Color.parseColor("#33DBFF"));
            borderPaint.setStrokeWidth(3);
        } catch (JSONException e) {
            System.err.println("ElementData: Invalid JSON object");
        }
    }

    // 좌측 상단의 X좌표.
    public float getX() {
        return x;
    }

    // 좌측 상단의 Y좌표.
    public float getY() {
        return y;
    }

    // 객제의 가로 길이.
    public float getWidth() {
        return width;
    }

    // 객체의 세로 길이.
    public float getHeight() {
        return height;
    }

    // 좌측 상단의 X좌표를 설정.
    public void setX(float x) {
        this.x = x;
    }

    // 좌측 상단의 Y좌표를 설정.
    public void setY(float y) {
        this.y = y;
    }

    // 객제의 가로 길이를 설정.
    public void setWidth(float width) {
        this.width = width;
    }

    // 객체의 세로 길이를 설정.
    public void setHeight(float height) {
        this.height = height;
    }

    // Canvas 객체에 이 객체(Text, Image, etc)의 디자인을 그림.
    public void drawOn(Canvas canvas) {
        if (isSelected) {
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(x, y, x+width, y+height, borderPaint);
            borderPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, 7, borderPaint);
            canvas.drawCircle(x+width/2, y, 7, borderPaint);
            canvas.drawCircle(x+width, y, 7, borderPaint);
            canvas.drawCircle(x, y+height/2, 7, borderPaint);
            canvas.drawCircle(x+width, y+height/2, 7, borderPaint);
            canvas.drawCircle(x, y+height, 7, borderPaint);
            canvas.drawCircle(x+width/2, y+height, 7, borderPaint);
            canvas.drawCircle(x+width, y+height, 7, borderPaint);
        }
    }

    // 인터페이스에서 해당 객체가 선택된 상태인지를 설정.
    public void setSelected(boolean sel) {
        isSelected = sel;
    }

    // 해당 좌표가 이 객체 위에 있는지 판별.
    public boolean isContaining(float x, float y) {
        return (getX() - RESIZE_BORDER/2 <= x && x <= getX() + getWidth() + RESIZE_BORDER/2 &&
                getY() - RESIZE_BORDER/2 <= y && y <= getY() + getHeight() + RESIZE_BORDER/2);
    }

    // 해당 좌표가 이 객체의 테두리(크기 조절용, 폭이 RESIZE_BORDER와 같음) 위에 있는지 판별.
    public boolean isOnBorder(float x, float y) {
        return (isContaining(x, y) && !isInBorder(x, y));
    }

    // 해당 좌표가 이 객체의 테두리 안에 있는지 판별.
    public boolean isInBorder(float x, float y) {
        return (getX() + RESIZE_BORDER/2 <= x && x <= getX() + getWidth() - RESIZE_BORDER/2 &&
                getY() + RESIZE_BORDER/2 <= y && y <= getY() + getHeight() - RESIZE_BORDER/2);
    }

    // 해당 좌표가 테두리에 해당하는 지점이라면 그 데두리가 어느 방향에 속하는지를 반환.
    public BorderKind recognizeBorderOn(float x, float y) {
        BorderKind kind = new BorderKind();
        if (isInBorder(x, y)) {
            return kind;
        }

        float innerLeft = this.x + RESIZE_BORDER/2;
        float innerRight = this.x + width - RESIZE_BORDER/2;
        float innerUp = this.y + RESIZE_BORDER/2;
        float innerDown = this.y + height - RESIZE_BORDER/2;

        boolean xLeft = innerLeft - RESIZE_BORDER <= x && x <= innerLeft;
        boolean xCenter = innerLeft <= x && x <= innerRight;
        boolean xRight = innerRight <= x && x <= innerRight + RESIZE_BORDER;

        boolean yUp = innerUp - RESIZE_BORDER <= y && y <= innerUp;
        boolean yDown = innerDown <= y && y <= innerDown + RESIZE_BORDER;

        if (xLeft) {
            kind.setLeft(true); // 좌측 공통
            if (yUp) { // 좌측 상단
                kind.setUp(true);
            } else if (yDown) { // 좌측 하단
                kind.setDown(true);
            }
        } else if (xCenter) {
            if (yUp) { // 상단
                kind.setUp(true);
            } else if (yDown) { // 하단
                kind.setDown(true);
            }
        } else if (xRight) {
            kind.setRight(true); // 우측 공통
            if (yUp) { // 우측 상단
                kind.setUp(true);
            } else if (yDown) { // 우측 하단
                kind.setDown(true);
            }
        }

        return kind;
    }
}
