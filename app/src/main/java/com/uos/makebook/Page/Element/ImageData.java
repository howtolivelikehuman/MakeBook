package com.uos.makebook.Page.Element;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.util.Base64;
import com.uos.makebook.Common.Function;

public class ImageData implements ElementData {
    private Bitmap bitmap;
    private int x, y;
    private int width, height;

    public ImageData(String b64Data, int x, int y, int width, int height) {
        byte[] data = Base64.decode(b64Data, Base64.DEFAULT);
        bitmap = Function.getBitmapFromByteArray(data);
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
