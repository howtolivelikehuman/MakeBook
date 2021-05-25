package com.uos.makebook.Page.Element;

import android.graphics.Canvas;

public class TextData implements ElementData {
    private String text;
    private int x, y;
    private int width, height;

    public TextData(String text, int x, int y, int width, int height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void DrawOn(Canvas canvas) {

    }
}
