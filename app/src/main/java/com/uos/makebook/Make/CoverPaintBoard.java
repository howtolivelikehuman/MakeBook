package com.uos.makebook.Make;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CoverPaintBoard extends View {
    public boolean changed = false;
    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;

    float lastX;
    float lastY;
    float mCurveEndX;
    float mCurveEndY;
    int mInvalidateExtraBorder = 10;

    Path mpath = new Path();
    Paint.Cap strokeCap = Paint.Cap.ROUND;

    //지우개
    int[] eraseSize = {10,40,70};
    boolean eraseMode = false;

    static final float TOUCH_TOLERANCE = 8;

    public CoverPaintBoard(Context context) {
        super(context);
        init(context);
    }

    public CoverPaintBoard(Context context, Bitmap bitmap) {
        super(context);
        mBitmap = bitmap;
        init(context);
    }

    public CoverPaintBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void clearBitmap(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    //색 설정
    public void setColor(int color){
        eraseMode = false;
        mPaint.setColor(color);
    }
    //두께 설정
    public void setBorder(int i){
        mPaint.setStrokeWidth(i);
    }

    //지우개
    public void setEraser(int checkedId){
        eraseMode = true;
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(eraseSize[checkedId]);
    }

    public void init(Context context){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15.0F);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(strokeCap);

        this.lastX = -1;
        this.lastY= -1;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        Canvas canvas = new Canvas();
        canvas.drawColor(Color.WHITE);

        if(mBitmap == null){
            Bitmap img = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
            canvas.setBitmap((img));
            mBitmap = img;
        }
        else{
            canvas.setBitmap(mBitmap);
        }
        mCanvas = canvas;
    }

    protected void onDraw(Canvas canvas){
        if(mBitmap != null){
            canvas.drawBitmap(mBitmap,0,0,null);
        }
    }


    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        Rect rect;
        switch (action){
            case MotionEvent.ACTION_UP : {
                changed = true;
                rect = touchUp(event,false);
                if(rect!= null){
                    invalidate(rect);
                }
                mpath.rewind();
                return true;
            }

            case MotionEvent.ACTION_DOWN :{
                rect = touchDown(event);
                if(rect!= null){
                    invalidate(rect);
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE : {
                rect = touchMove(event);
                if(rect!= null){
                    invalidate(rect);
                }
                return true;
            }
        }
        return false;
    }

    private Rect touchDown(MotionEvent event){
        float x = event.getX();
        float y =  event.getY();

        lastY = y;
        lastX = x;

        Rect mInvalidaRect = new Rect();
        mpath.moveTo(x,y);

        final int border = mInvalidateExtraBorder;
        mInvalidaRect.set((int)x-border, (int)y-border, (int) x+border,(int) y+border);
        mCurveEndX = x;
        mCurveEndY = y;
        mCanvas.drawPath(mpath,mPaint);
        return mInvalidaRect;
    }

    private Rect touchMove(MotionEvent event){
        Rect rect = processMove(event);
        return rect;
    }
    private Rect touchUp(MotionEvent event, boolean cancel){
        Rect rect = processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event){
        final float x = event.getX();
        final float y = event.getY();
        final float dx = Math.abs(x-lastX);
        final float dy = Math.abs(y-lastY);

        Rect mInvalidRect = new Rect();
        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            final int border = mInvalidateExtraBorder;
            mInvalidRect.set((int)mCurveEndX-border, (int)mCurveEndY - border, (int)mCurveEndX+border, (int)mCurveEndY+border);

            float cx = mCurveEndX = (x+lastX)/2;
            float cy = mCurveEndY = (y+lastY)/2;

            mpath.quadTo(lastX,lastY,cx,cy);
            mInvalidRect.union((int)lastX - border, (int)lastY - border, (int)lastX+border, (int)lastY+border);
            mInvalidRect.union((int)cx - border, (int)cy- border, (int)cx+border, (int)cy+border);
            lastY = y;
            lastX = x;
            mCanvas.drawPath(mpath,mPaint);
        }
        return mInvalidRect;

    }

}