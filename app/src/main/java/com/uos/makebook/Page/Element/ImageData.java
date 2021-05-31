package com.uos.makebook.Page.Element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import com.uos.makebook.Common.Function;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageData implements ElementData {
    private Bitmap bitmap = null;
    private float x = 0, y = 0;
    private float width = 0, height = 0;
    private Paint paint;
    private boolean isSelected = false;

    public ImageData(JSONObject jsonObject) {
        try {
            // 공통 속성 파싱
            x = (float) jsonObject.getDouble("x");
            y = (float) jsonObject.getDouble("y");
            width = (float) jsonObject.getDouble("width");
            height = (float) jsonObject.getDouble("height");

            String src = jsonObject.getString("src");
            bitmap = BitmapFactory.decodeFile(src);
            if (bitmap == null) {
                System.err.println("TextData: Image file is not found");
            }

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
        } catch (JSONException e) {
            System.err.println("TextData: Invalid JSON object");
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void drawOn(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, x, y, paint);
        }
    }

    @Override
    public void setSelected(boolean sel) {
        isSelected = sel;
    }
}
