package com.uos.makebook.Page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.uos.makebook.Page.Element.ElementData;

import java.time.Duration;

public class PageCanvas extends View {
    private Page page;
    private Paint paint;
    private ElementData currentSelected = null;

    public PageCanvas(Context context, Page page) {
        super(context);
        this.page = page;

        setBackgroundColor(Color.TRANSPARENT);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextSize(72);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (page != null) {
            page.drawElements(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean used = true;
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (currentSelected != null) {
                    currentSelected.setSelected(false);
                }
                ElementData element = page.findElement(x, y);
                if (element != null) {
                    element.setSelected(true);
                    currentSelected = element;
                }
                this.invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentSelected != null) {
                    currentSelected.setX(x);
                    currentSelected.setY(y);
                }
                this.invalidate();
                break;

            default:
                used = false;
        }

        return used;
    }
}
