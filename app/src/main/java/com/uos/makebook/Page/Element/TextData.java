package com.uos.makebook.Page.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONException;
import org.json.JSONObject;

public class TextData implements ElementData {
    private String text = "";
    private float x = 0, y = 0;
    private float width = 0, height = 0;
    private int fontSize, fontColor;
    private boolean isSelected = false;

    private Paint paint;

    public TextData(JSONObject jsonObject) {
        try {
            // 공통 속성 파싱
            x = (float) jsonObject.getDouble("x");
            y = (float) jsonObject.getDouble("y");
            width = (float) jsonObject.getDouble("width");
            height = (float) jsonObject.getDouble("height");

            text = jsonObject.getString("value");
            fontSize = jsonObject.getInt("fontSize");
            fontColor = jsonObject.getInt("fontColor");

            paint = new Paint();
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
        paint.setTextSize(fontSize);
        paint.setColor(fontColor);
        canvas.drawText(text, x, y, paint);

//        if (isSelected) {
//            paint.setColor(Color.BLUE);
//            canvas.drawRect(x, y, x+width, y+height, paint);
//        }
    }

    @Override
    public void setSelected(boolean sel) {
        isSelected = sel;
    }
}
