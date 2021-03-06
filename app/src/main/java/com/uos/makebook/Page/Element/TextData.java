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

    private boolean textDirty = false;
    private boolean fontSizeDirty = false;
    private boolean fontColorDirty = false;

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

    public TextData(String value, int fontSize, int fontColor, int canvasWidth) {
        super(0, 0, 0, 0);
        this.text = value;
        this.fontSize = fontSize;
        this.fontColor = fontColor;

        generateTextPaint();
        generateTextLayout();

        Rect bound = new Rect();
        textPaint.getTextBounds(value, 0, value.length(), bound);
        if (canvasWidth < bound.width() + RESIZE_BORDER * 2) {
            setWidth(canvasWidth - RESIZE_BORDER * 2);
            setHeight(bound.height() * (float)Math.ceil((float)bound.width() / canvasWidth));
        } else {
            setWidth(bound.width());
            setHeight(bound.height());
        }
    }

    private void generateTextLayout() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textLayout = new StaticLayout(text, textPaint, (int)getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        } else {
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, (int)getWidth());
            textLayout = builder.build();
        }
        textDirty = false;
    }

    private void generateTextPaint() {
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextSize(fontSize);
        textPaint.setColor(fontColor);

        fontSizeDirty = false;
        fontColorDirty = false;
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        generateTextLayout();
    }

    public String getText() {
        return text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setText(String value) {
        text = value;
        textDirty = true;
    }

    public void setFontSize(int size) {
        fontSize = size;
        fontSizeDirty = true;
    }

    public void setFontColor(int color) {
        fontColor = color;
        fontColorDirty = true;
    }

    @Override
    public void drawOn(Canvas canvas) {
        if (fontColorDirty || fontSizeDirty || textDirty) {
            generateTextPaint();
            generateTextLayout();
        }
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
