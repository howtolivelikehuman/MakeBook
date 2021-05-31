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

public class ImageData extends ElementData {
    private Bitmap bitmap = null;
    private Paint paint;

    public ImageData(JSONObject jsonObject) {
        super(jsonObject);
        try {
            String src = jsonObject.getString("src");
            bitmap = BitmapFactory.decodeFile(src);
            if (bitmap == null) {
                System.err.println("ImageData: Image file is not found");
            }
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
        } catch (JSONException e) {
            System.err.println("ImageData: Invalid JSON object");
        }
    }

    @Override
    public void drawOn(Canvas canvas) {
        super.drawOn(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, getX(), getY(), paint);
        }
    }
}
