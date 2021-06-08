package com.uos.makebook.Page.Element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.uos.makebook.Common.Function;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageData extends ElementData {
    private String src;
    private Bitmap bitmap = null;

    public ImageData(JSONObject jsonObject) {
        super(jsonObject);
        try {
            src = jsonObject.getString("src");
            getBitmap(src);
        } catch (JSONException e) {
            System.err.println("ImageData: Invalid JSON object");
        }
    }

    public ImageData(String src, int canvasWidth, int canvasHeight) {
        super(0, 0, 0, 0);
        getBitmap(src);

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        if (canvasWidth < width + RESIZE_BORDER || canvasHeight < height + RESIZE_BORDER) {
            float bitmapRatio = width / height;
            float displayRatio = (float) canvasWidth / canvasHeight;

            if (displayRatio < bitmapRatio) {
                // 비트맵의 가로길이를 화면 가로 길이에 맞게 줄여야 하는 상황.
                width = canvasWidth - RESIZE_BORDER;
                height = width / bitmapRatio;
            } else {
                // 비트맵의 세로길이를 화면 세로 길이에 맞게 줄여야 하는 상황.
                height = canvasHeight - RESIZE_BORDER;
                width = height * bitmapRatio;
            }
        }
        setWidth(width);
        setHeight(height);
    }

    private void getBitmap(String src) {
        this.src = src;
        bitmap = BitmapFactory.decodeFile(src);
        if (bitmap == null) {
            System.err.println("ImageData: Image file is not found - " + src);
        }
    }

    private void removeImageFile() {
        File old = new File(this.src);
        if (old.exists()) {
            old.delete();
        }
    }

    public void setSource(String newSrc) {
        // 이전의 이미지 파일이 존재한다면 삭제.
        removeImageFile();
        getBitmap(newSrc);
    }

    @Override
    public void drawOn(Canvas canvas) {
        if (bitmap != null) {
            canvas.save();
            canvas.translate(getX(), getY());
            RectF dest = new RectF(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(bitmap, null, dest, null);
            canvas.restore();
        }

        super.drawOn(canvas);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();

        try {
            json.put("kind", "image");
            json.put("src", src);
            return json;
        } catch (JSONException e) {
            System.err.println("TextData: Failed to generate JSON");
            return null;
        }
    }

    @Override
    public void onRemove() {
        super.onRemove();
        removeImageFile();
    }
}
