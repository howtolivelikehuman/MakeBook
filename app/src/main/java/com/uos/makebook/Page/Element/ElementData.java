package com.uos.makebook.Page.Element;

import android.graphics.Canvas;

public interface ElementData<T> {
    // 좌측 상단의 X좌표.
    int getX();
    // 좌측 상단의 Y좌표.
    int getY();
    // 객제의 가로 길이.
    int getWidth();
    // 객체의 세로 길이.
    int getHeight();
    // Canvas 객체에 이 객체(Text, Image, etc)의 디자인을 그림.
    void DrawOn(Canvas canvas);
}
