package com.uos.makebook.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.uos.makebook.Edit.Page;

import java.util.ArrayList;

public class PageDB implements DB{

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    public PageDB(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insert(DTO o){
        Page page = (Page)o;
        DatabaseHelper.println("page 삽입");
        ContentValues val = new ContentValues();

        //ID는 AUTO INCREMENT
        val.put(Constant.COLUMN_PAGE[1], page.getBookId());
        val.put(Constant.COLUMN_PAGE[2], page.getIdx());
        val.put(Constant.COLUMN_PAGE[3], page.getText());
        val.put(Constant.COLUMN_PAGE[4], Function.getByteArrayFromDrawable(page.getImg()));

        //insert시 PK return
        return database.insert(Constant.TABLE_NAME[1], null, val);
    }

    public int delete(int pk){
        DatabaseHelper.println("page 삭제");
        String selection = Constant.COLUMN_PAGE[0] + " LIKE ?";
        String[] selectionArgs = {Integer.toString(pk)};
        //delete 된 수 return
        return database.delete(Constant.TABLE_NAME[1], selection, selectionArgs);
    }

    public int update(DTO o) {
        DatabaseHelper.println("page 업데이트");

        Page page = (Page) o;
        ContentValues val = new ContentValues();
        val.put(Constant.COLUMN_PAGE[1], page.getIdx());
        val.put(Constant.COLUMN_PAGE[2], page.getText());
        val.put(Constant.COLUMN_PAGE[3], Function.getByteArrayFromDrawable(page.getImg()));

        String whereClause = Constant.COLUMN_PAGE[0] + " =";
        String[] whereArgs = {String.valueOf(page.getId())};

        //update 된 수 return
        return database.update(Constant.TABLE_NAME[1], val, whereClause, whereArgs);
    }

    public ArrayList<Page> selectAll(){
        DatabaseHelper.println("page 전체 조회");
        ArrayList<Page> pages = new ArrayList<Page>();
        Cursor cursor = database.query(Constant.TABLE_NAME[1], null, null, null, null, null, null);

        //1개 이상이면
        if(cursor.getCount() > 0){
            Page p;

            while(cursor.moveToNext()){
                p = new Page();
                //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
                //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
                //그냥 하드코딩해도됨
                p.setId(cursor.getInt(0));
                p.setBookId(cursor.getInt(1));
                p.setIdx(cursor.getInt(2));
                p.setText(cursor.getString(3));
                p.setImg(Function.getBitmapFromByteArray(cursor.getBlob(4)));
                pages.add(p);
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
            return null;
        }
        cursor.close();
        return pages;
    }

    //column에 해당하는 data로 찾기
    public ArrayList<Page> select(String column, String data){
        DatabaseHelper.println("page 조회");

        ArrayList<Page> pages = new ArrayList<Page>();
        String selection  = column + " LIKE ?";
        String[] selectionArgs = {"%" + data + "%"};

        Cursor cursor = database.query(Constant.TABLE_NAME[1], null, selection, selectionArgs, null, null, null);

        //1개 이상이면
        if(cursor.getCount() > 0){
            Page p;

            while(cursor.moveToNext()){
                p = new Page();
                //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
                //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
                //그냥 하드코딩해도됨
                p.setId(cursor.getInt(0));
                p.setBookId(cursor.getInt(1));
                p.setIdx(cursor.getInt(2));
                p.setText(cursor.getString(3));
                p.setImg(Function.getBitmapFromByteArray(cursor.getBlob(4)));
                pages.add(p);
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return pages;
    }

}
