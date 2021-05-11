package com.uos.makebook.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Function {
    //Bitmap -> Byte
    public static byte[] getByteArrayFromDrawable(Bitmap d){
        if(d != null){
            //if drawable
            //Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            Bitmap bitmap = d;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] data = stream.toByteArray();
            return data;
        }
        else{
            DatabaseHelper.println("사진파일이 없음");
            return null;
        }
    }
    //Byte -> Bitmap
    public static Bitmap getBitmapFromByteArray(byte[] bytes){
        Bitmap bit;

        if(bytes != null){
            bit = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bit;
        }
        else{
            DatabaseHelper.println("사진파일 X");
            return null;
        }
    }
}
