package com.uos.makebook.Page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.uos.makebook.Page.Element.BorderKind;
import com.uos.makebook.Page.Element.ElementData;

import java.time.Duration;

enum DragKind {
    MOVE, RESIZE
}

public class PageCanvas extends View {
    private Page page;
    private ElementData currentSelected = null;
    private float dx, dy;
    private DragKind dragKind;
    private BorderKind borderKind;

    public PageCanvas(Context context, Page page) {
        super(context);
        this.page = page;

        setBackgroundColor(Color.TRANSPARENT);
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
                    currentSelected = null;
                }
                ElementData element = page.findElement(x, y);
                if (element != null) {
                    element.setSelected(true);
                    currentSelected = element;
                    dx = x - element.getX();
                    dy = y - element.getY();

                    borderKind = element.recognizeBorderOn(x, y);
                    dragKind = borderKind.isSet() ? DragKind.RESIZE : DragKind.MOVE;
                }
                this.invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (currentSelected == null) {
                    break;
                }

                switch (dragKind) {
                    case MOVE:
                        currentSelected.setX(x-dx);
                        currentSelected.setY(y-dy);
                        break;

                    case RESIZE:
                        float pX = currentSelected.getX();
                        float pY = currentSelected.getY();
                        float pWidth = currentSelected.getWidth();
                        float pHeight = currentSelected.getHeight();
                        float diffX = x - pX;
                        float diffY = y - pY;

                        float minLength = ElementData.RESIZE_BORDER * 2;

                        if (borderKind.isLeft()) {
                            currentSelected.setWidth(Math.max(pWidth - diffX, minLength));
                            float maxX = pX + pWidth - minLength;
                            currentSelected.setX(Math.min(pX + diffX, maxX));
                        }
                        if (borderKind.isRight()) {
                            currentSelected.setWidth(Math.max(diffX, minLength));
                        }
                        if (borderKind.isUp()) {
                            currentSelected.setHeight(Math.max(pHeight - diffY, minLength));
                            float maxY = pY + pHeight - minLength;
                            currentSelected.setY(Math.min(pY + diffY, maxY));
                        }
                        if (borderKind.isDown()) {
                            currentSelected.setHeight(Math.max(diffY, minLength));
                        }
                        break;
                }

                this.invalidate();
                break;

            default:
                used = false;
        }

        return used;
    }
}
