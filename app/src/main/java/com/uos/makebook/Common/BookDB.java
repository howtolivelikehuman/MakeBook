package com.uos.makebook.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uos.makebook.MainList.Book;

import java.util.ArrayList;

public class BookDB implements DB {

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    public BookDB(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insert(DTO o) {
        Book book = (Book) o;
        DatabaseHelper.println("Book 삽입");
        ContentValues val = new ContentValues();

        //ID는 AUTO INCREMENT
        val.put(Constant.COLUMN_BOOKLIST[1], book.getTitle());
        val.put(Constant.COLUMN_BOOKLIST[2], book.getWriter());
        val.put(Constant.COLUMN_BOOKLIST[3], Function.getByteArrayFromDrawable(book.getCover()));

        //insert시 PK return
        return database.insert(Constant.TABLE_NAME[0], null, val);
    }

    public int delete(long pk) {
        DatabaseHelper.println("Book 삭제");
        String selection = Constant.COLUMN_BOOKLIST[0] + " LIKE ?";
        String[] selectionArgs = {Long.toString(pk)};
        //delete 된 수 return
        return database.delete(Constant.TABLE_NAME[0], selection, selectionArgs);
    }

    public int update(DTO o) {
        DatabaseHelper.println("Book 업데이트");

        Book book = (Book) o;
        ContentValues val = new ContentValues();
        val.put(Constant.COLUMN_BOOKLIST[1], book.getTitle());
        val.put(Constant.COLUMN_BOOKLIST[2], book.getWriter());
        val.put(Constant.COLUMN_BOOKLIST[3], Function.getByteArrayFromDrawable(book.getCover()));

        String whereClause = Constant.COLUMN_BOOKLIST[0] + " = " + book.getId();

        //update 된 수 return
        return database.update(Constant.TABLE_NAME[0], val, whereClause, null);
    }

    public ArrayList<Book> selectAll() {
        DatabaseHelper.println("Book 전체 조회");
        ArrayList<Book> books = new ArrayList<Book>();
        Cursor cursor = database.query(Constant.TABLE_NAME[0], null, null, null, null, null, null);

        //1개 이상이면
        if (cursor.getCount() > 0) {
            Book b;

            while (cursor.moveToNext()) {
                b = new Book();
                //getColumnIndex(컬럼명)으로 몇번 컬럼인지 알아와서 넣어도 되고
                //b.setId(cursor.getInt(cursor.getColumnIndex(Constant.COLUMN_BOOKLIST[0])));
                //그냥 하드코딩해도됨
                b.setId(cursor.getLong(0));
                b.setTitle(cursor.getString(1));
                b.setWriter(cursor.getString(2));
                b.setCover(Function.getBitmapFromByteArray(cursor.getBlob(3)));
                b.setCreatedate(cursor.getString(4));
                books.add(b);
            }
        }
        else {
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return books;
    }

    public Book selectById(Long id){
        Book book = null;
        String query = "SELECT * FROM  " + Constant.TABLE_NAME[0] + " WHERE ID = " + id;
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.getCount() > 0){
            cursor.moveToNext();
            book = new Book();
            book.setId(cursor.getLong(0));
            book.setTitle(cursor.getString(1));
            book.setWriter(cursor.getString(2));
            book.setCover(Function.getBitmapFromByteArray(cursor.getBlob(3)));
            book.setCreatedate(cursor.getString(4));
        }
        cursor.close();
        return book;
    }

    public ArrayList<Book> select(String[] column, String[] data){ // 여러 조건을 사용할 수 있는 select문
        DatabaseHelper.println("Book 조회");
        ArrayList<Book> books = new ArrayList<Book>();

        if(column.length != data.length){
            System.out.println("넘어온 인자의 개수가 다릅니다.");
            return books;
        }

        String query = "SELECT * FROM " + Constant.TABLE_NAME[0] + " WHERE ";
        for(int i=0; i<column.length; i++){
            query += (column[i]+"=?");
            if(i!=column.length-1){
                query += " AND ";
            }
        }
        System.out.println(query);

        Cursor cursor = database.rawQuery(query, data);

        //1개 이상이면
        if(cursor.getCount() > 0){
            Book b;
            while (cursor.moveToNext()) {
                b = new Book();
                b.setId(cursor.getLong(0));
                b.setTitle(cursor.getString(1));
                b.setWriter(cursor.getString(2));
                b.setCover(Function.getBitmapFromByteArray(cursor.getBlob(3)));
                b.setCreatedate(cursor.getString(4));
                books.add(b);
            }
        }
        else{
            DatabaseHelper.println("조회 결과가 없습니다.");
        }
        cursor.close();
        return books;
    }
}
