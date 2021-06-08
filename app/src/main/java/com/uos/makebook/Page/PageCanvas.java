package com.uos.makebook.Page;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.uos.makebook.Page.Element.BorderKind;
import com.uos.makebook.Page.Element.ElementData;
import com.uos.makebook.Page.Element.ImageData;
import com.uos.makebook.Page.Element.TextData;
import com.uos.makebook.R;

import org.w3c.dom.Text;

import java.time.Duration;

public class PageCanvas extends View {
    enum DragKind {
        MOVE, RESIZE
    }

    private Page page;
    private ElementData currentSelected = null;
    private float downX, downY;
    private float dx, dy;
    private DragKind dragKind;
    private BorderKind borderKind;

    // 아래 변수는 Long Click(0.5초 이상 꾹 누르고 있는 동작)을 감지하기 위해 사용됨.
    // down 이벤트가 발생하면 Handler에 0.5초 뒤 onLongClick을 실행하도록 예약하고,
    // downCount를 currCount로 하는데,
    // onLongClick에서는 downCount가 currCount와 같다면 Long Click으로 인식.
    // 0.5초가 지나기 전 이벤트가 해제된다면(사용자가 손을 떼거나, 움직이는 등...)
    // currCount++가 되므로 onLongClick은 실행되지 않음.
    private Handler longClickHandler;
    private Runnable onLongClick;
    private int currCount = 0;
    private int downCount = -1;

    public PageCanvas(Context context, Page page) {
        super(context);
        this.page = page;

        setBackgroundColor(Color.TRANSPARENT);

        longClickHandler = new Handler();
        onLongClick = () -> {
            if (currCount == downCount && currentSelected != null) {
                showLongTouchDialog(context, currentSelected);
            }
        };
    }

    private void showLongTouchDialog(Context context, ElementData element) {
        final String[] items = { "수정하기", "삭제하기" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("선택한 객체를 어떻게 할까요?");
        builder.setItems(items, ((dialog, which) -> {
            switch (which) {
                case 0:

                    break;
                case 1:
                    showDeletionDialog(context, element);
                    break;
            }
        }));
        builder.setCancelable(true);
        builder.show();
    }

    private void showDeletionDialog(Context context, ElementData element) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("정말 삭제할까요?");
        if (element instanceof TextData) {
            builder.setMessage("선택한 텍스트를 정말 삭제할까요?");
        } else if (element instanceof ImageData) {
            builder.setMessage("선택한 이미지를 정말 삭제할까요?");
        } else {
            builder.setMessage("선택한 객체를 정말 삭제할까요?");
        }

        builder.setPositiveButton("예", (dialog, which) -> {
            page.removeElement(element);
            this.invalidate();
        });
        builder.setNegativeButton("아니요", null);

        builder.setCancelable(true);
        builder.show();
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
                selectElement(x, y);
                if (currentSelected != null) {
                    downX = x;
                    downY = y;
                    downCount = currCount;
                    longClickHandler.postDelayed(onLongClick, 500); // Long Click 이벤트 예약
                }
                this.invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (currCount == downCount && 100 < Math.pow(downX - x, 2) + Math.pow(downY - y, 2)) {
                    currCount = (currCount + 1) % Integer.MAX_VALUE; // 이벤트 비활성화
                }
                handleDrag(x, y);
                this.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                currCount = (currCount + 1) % Integer.MAX_VALUE; // 이벤트 비활성화
                page.updateContents();

            default:
                used = false;
        }

        return used;
    }

    private void selectElement(float x, float y) {
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
    }

    private void handleDrag(float x, float y) {
        if (currentSelected == null) {
            return;
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
    }
}
