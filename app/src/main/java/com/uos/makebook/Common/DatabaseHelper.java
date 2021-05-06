package com.uos.makebook.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public static DatabaseHelper dbhelper = null;

    //Singleton
    public static DatabaseHelper getInstance(Context context){
        if(dbhelper == null){
            dbhelper = new DatabaseHelper(context);
        }
        return dbhelper;
    }

    private DatabaseHelper(Context context){
        super(context,Constant.DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //샘플용 db
        sqLiteDatabase.execSQL(Constant.CREATE_TABLE_BOOKLIST);
        println("onCreate 호출됨");
    }

    public void onOpen(SQLiteDatabase db){
        println("onOpen 호출됨");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        println("onUpgrade 호출됨 : " +i+" -> "+ i1);
        if(i1 > 1){
            for(int a = 0; a< Constant.TABLE_NAME.length; a++){
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+Constant.TABLE_NAME[a]);
            }
        }
        onCreate(sqLiteDatabase);
    }


    public static void println(String data){
        Log.d("DB" , data);
    }
}
