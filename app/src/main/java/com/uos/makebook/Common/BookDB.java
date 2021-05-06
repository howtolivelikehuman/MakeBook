package com.uos.makebook.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.uos.makebook.MainList.Book;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class BookDB implements DB{

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    public BookDB(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insert(DTO o){
        Book book = (Book)o;
        DatabaseHelper.println("삽입");
        ContentValues val = new ContentValues();

        //ID는 AUTO INCREMENT
        val.put(Constant.COLUMN_BOOKLIST[1], book.getName());
        val.put(Constant.COLUMN_BOOKLIST[2], getByteArrayFromDrawable(book.getCover()));

        //insert시 PK return
        return database.insert(Constant.TABLE_NAME[0], null, val);
    }

    public int delete(int pk){
        DatabaseHelper.println("삭제");
        String selection = Constant.COLUMN_BOOKLIST[0] + " LIKE ?";
        String[] selectionArgs = {Integer.toString(pk)};
        //delete 된 수 return
        return database.delete(Constant.TABLE_NAME[0], selection, selectionArgs);
    }

    public int update(DTO o){
        DatabaseHelper.println("업데이트");

        Book book = (Book)o;
        ContentValues val = new ContentValues();
        val.put(Constant.COLUMN_BOOKLIST[1], book.getName());
        val.put(Constant.COLUMN_BOOKLIST[2], getByteArrayFromDrawable(book.getCover()));

        String whereClause = Constant.COLUMN_BOOKLIST[0] + " =";
        String[] whereArgs = {String.valueOf(book.getId())};

        //update 된 수 return
        return database.update(Constant.TABLE_NAME[0], val, whereClause, whereArgs);
    }

    public ArrayList<Book> selectAll(){
        DatabaseHelper.println("전체 조회");
        ArrayList<Book> books = new ArrayList<Book>();
        Cursor cursor = database.query(Constant.TABLE_NAME[0], null, null, null, null, null, null);

        //1개 이상이면
        if(cursor.getCount() > 0){
            Book b;

            while(cursor.moveToNext()){
                b = new Book();
                //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
                //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
                //그냥 하드코딩해도됨
                b.setId(cursor.getInt(0));
                b.setName(cursor.getString(1));
                b.setCover(getBitmapFromByteArray(cursor.getBlob(2)));
                books.add(b);
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return books;
    }

    //column에 해당하는 data로 찾기
    public ArrayList<Book> select(String column, String data){
        DatabaseHelper.println("조회");

        ArrayList<Book> books = new ArrayList<Book>();
        String selection  = column + " LIKE ?";
        String[] selectionArgs = {"%" + data + "%"};

        Cursor cursor = database.query(Constant.TABLE_NAME[0], null, selection, selectionArgs, null, null, null);

        //1개 이상이면
        if(cursor.getCount() > 0){
            Book b;

            while(cursor.moveToNext()){
                b = new Book();
                //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
                //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
                //그냥 하드코딩해도됨
                b.setId(cursor.getInt(0));
                b.setName(cursor.getString(1));
                b.setCover(getBitmapFromByteArray(cursor.getBlob(2)));
                books.add(b);
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return books;
    }

    //Bitmap -> Byte
    public byte[] getByteArrayFromDrawable(Bitmap d){
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
    public Bitmap getBitmapFromByteArray(byte[] bytes){
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
