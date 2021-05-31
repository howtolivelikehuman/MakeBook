package com.uos.makebook.Page.Element;

import android.graphics.Canvas;

public interface ElementData {
    // 좌측 상단의 X좌표.
    float getX();
    // 좌측 상단의 Y좌표.
    float getY();
    // 객제의 가로 길이.
    float getWidth();
    // 객체의 세로 길이.
    float getHeight();

    // 좌측 상단의 X좌표를 설정.
    void setX(float x);
    // 좌측 상단의 Y좌표를 설정.
    void setY(float y);
    // 객제의 가로 길이를 설정.
    void setWidth(float width);
    // 객체의 세로 길이를 설정.
    void setHeight(float height);

    // Canvas 객체에 이 객체(Text, Image, etc)의 디자인을 그림.
    void drawOn(Canvas canvas);
    // 인터페이스에서 해당 객체가 선택된 상태인지를 설정.
    void setSelected(boolean sel);
}
