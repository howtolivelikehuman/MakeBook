package com.uos.makebook.Page.Element;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import com.uos.makebook.Common.Function;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageData extends ElementData {
    private String src;
    private Bitmap bitmap = null;

    public ImageData(JSONObject jsonObject) {
        super(jsonObject);
        try {
            src = jsonObject.getString("src");
            initialize(src);
        } catch (JSONException e) {
            System.err.println("ImageData: Invalid JSON object");
        }
    }

    public ImageData(String src) {
        super(0, 0, 0, 0);
        initialize(src);
        setWidth(bitmap.getWidth());
        setHeight(bitmap.getHeight());
    }

    private void initialize(String src) {
        this.src = src;
        bitmap = BitmapFactory.decodeFile(src);
        if (bitmap == null) {
            System.err.println("ImageData: Image file is not found - " + src);
        }
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
}
