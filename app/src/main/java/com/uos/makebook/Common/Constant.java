package com.uos.makebook.Common;

import java.util.ArrayList;

public class Constant {
    private Constant(){}

    public static final String DATABASE_NAME = "MAKEBOOK";
    public static final String[] TABLE_NAME = {"BOOKLIST"};
    public static final String[] COLUMN_BOOKLIST = {"ID", "NAME", "IMAGE"};

    //테이블 생성
    public static final String CREATE_TABLE_BOOKLIST = "CREATE TABLE " + TABLE_NAME[0] + "(" +
            COLUMN_BOOKLIST[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BOOKLIST[1] + " TEXT, " +
            COLUMN_BOOKLIST[2] + " BLOB)";
}
