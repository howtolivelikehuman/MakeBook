package com.uos.makebook.Common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public interface DB<T>{
    long insert(DTO o);
    int delete(int pk);
    int update(DTO o);
    ArrayList<T> selectAll();
    ArrayList<T> select(String column, String data);
}
