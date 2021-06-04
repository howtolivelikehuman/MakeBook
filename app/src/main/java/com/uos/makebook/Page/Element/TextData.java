package com.uos.makebook.Page.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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

    public TextData(String value, int fontSize, int fontColor) {
        super(0, 0, 0, 0);
        this.text = value;
        this.fontSize = fontSize;
        this.fontColor = fontColor;

        generateTextPaint();
        generateTextLayout();

        Rect bound = new Rect();
        TextPaint paint = new TextPaint();
        paint.setTextSize(fontSize);
        paint.getTextBounds(value, 0, value.length(), bound);

        setWidth(bound.width());
        setHeight(bound.height());
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

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();

        try {
            json.put("kind", "text");
            json.put("value", text);
            json.put("fontSize", fontSize);
            json.put("fontColor", fontColor);
            return json;
        } catch (JSONException e) {
            System.err.println("TextData: Failed to generate JSON");
            return null;
        }
    }
}
