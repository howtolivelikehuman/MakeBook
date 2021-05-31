package com.uos.makebook.Page.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import org.json.JSONException;
import org.json.JSONObject;

public class TextData extends ElementData {
    private String text = "";
    private int fontSize, fontColor;
    private StaticLayout textLayout;
    private TextPaint textPaint;

    public TextData(JSONObject jsonObject) {
        super(jsonObject);
        try {
            text = jsonObject.getString("value");
            fontSize = jsonObject.getInt("fontSize");
            fontColor = jsonObject.getInt("fontColor");
            generateTextPaint();
            generateTextLayout();
        } catch (JSONException e) {
            System.err.println("TextData: Invalid JSON object");
        }
    }

    private void generateTextLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textLayout = new StaticLayout(text, textPaint, (int)getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        } else {
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, (int)getWidth());
            textLayout = builder.build();
        }
    }

    private void generateTextPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextSize(fontSize);
        textPaint.setColor(fontColor);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        generateTextLayout();
    }

    @Override
    public void drawOn(Canvas canvas) {
        super.drawOn(canvas);
        canvas.save();
        canvas.translate(getX(), getY());
        textLayout.draw(canvas);
        canvas.restore();
    }
}
