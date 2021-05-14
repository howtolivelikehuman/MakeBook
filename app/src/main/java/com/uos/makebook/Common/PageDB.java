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
        val.put(Constant.COLUMN_PAGE[2], page.getText());
        val.put(Constant.COLUMN_PAGE[3], Function.getByteArrayFromDrawable(page.getImg()));
        val.put(Constant.COLUMN_PAGE[4], page.getNextPage());

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
        Page page = (Page) o;
        DatabaseHelper.println("page 업데이트");
        ContentValues val = new ContentValues();
        System.out.println(page.getBookId());
        System.out.println(page.getText());
        System.out.println(page.getImg());
        System.out.println(page.getNextPage());
        System.out.println(page.getId());

        val.put(Constant.COLUMN_PAGE[2], page.getText());
        val.put(Constant.COLUMN_PAGE[3], Function.getByteArrayFromDrawable(page.getImg()));
        val.put(Constant.COLUMN_PAGE[4], page.getNextPage());

        String whereClause = Constant.COLUMN_PAGE[0] + "=?";
        String[] whereArgs = {Long.toString(page.getId())};

        //update 된 수 return
        return database.update(Constant.TABLE_NAME[1], val, whereClause, whereArgs);
    }

    private Page makePage(Cursor cursor){
        Page p;
        p = new Page();
        //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
        //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
        //그냥 하드코딩해도됨
        p.setId(cursor.getLong(0));
        p.setBookId(cursor.getLong(1));
        p.setText(cursor.getString(2));
        p.setImg(Function.getBitmapFromByteArray(cursor.getBlob(3)));
        p.setNextPage(cursor.getLong(4));
        return p;
    }

    public ArrayList<Page> selectAll(){
        DatabaseHelper.println("page 전체 조회");
        ArrayList<Page> pages = new ArrayList<Page>();

        Cursor cursor = database.query(Constant.TABLE_NAME[1], null, null, null, null, null, null);
        try {
            //1개 이상이면
            if (cursor.getCount() > 1) { // 1번 페이지는 안 씀! (head로 사용)
                cursor.moveToNext();
                long next_page = cursor.getLong(4);
                while (next_page != 0) {
                    if(cursor.moveToNext())
                        break;
                    String selection = Constant.COLUMN_PAGE[0] + " LIKE ?";
                    String[] selectionArgs = {"%" + next_page + "%"};
                    cursor = database.query(Constant.TABLE_NAME[1], null, selection, selectionArgs, null, null, null);

                    pages.add(makePage(cursor));
                    next_page = cursor.getLong(4);
                }
            } else {
                DatabaseHelper.println("조회 결과가 없습니다.");
                return null;
            }
        }finally {
            cursor.close();
        }
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
            cursor.moveToNext(); // 첫 페이지는 head
            while(cursor.moveToNext()){
                pages.add(makePage(cursor));
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return pages;
    }

}
