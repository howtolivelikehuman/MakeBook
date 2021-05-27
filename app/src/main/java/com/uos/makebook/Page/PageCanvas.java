package com.uos.makebook.Page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PageCanvas extends View {
    private Page page;
    private Paint paint;

    public PageCanvas(Context context, Page page) {
        super(context);
        this.page = page;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("Not Implemented", 0, 0, paint);
    }
}
