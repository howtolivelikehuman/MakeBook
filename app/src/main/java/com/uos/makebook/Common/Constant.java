package com.uos.makebook.Common;

import java.util.ArrayList;

public class Constant {
    private Constant(){}

    public static final String DATABASE_NAME = "MAKEBOOK";
    public static final String[] TABLE_NAME = {"BOOKLIST", "PAGE"};
    public static final String[] COLUMN_BOOKLIST = {"ID", "NAME", "IMAGE"};
    public static final String[] COLUMN_PAGE = {"ID", "BOOKID", "TEXT", "IMAGE", "NEXTPAGE"};

    //책 테이블 생성
    public static final String CREATE_TABLE_BOOKLIST = "CREATE TABLE " + TABLE_NAME[0] + "(" +
            COLUMN_BOOKLIST[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BOOKLIST[1] + " TEXT, " +
            COLUMN_BOOKLIST[2] + " BLOB)";
    //페이지 테이블 생성
    public static final String CREATE_TABLE_PAGE = "CREATE TABLE " + TABLE_NAME[1] + "(" +
            COLUMN_PAGE[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PAGE[1] + " INTEGER, " +
            COLUMN_PAGE[2] + " TEXT, " +
            COLUMN_PAGE[3] + " BLOB, " +
            COLUMN_PAGE[4] + " INTEGER, " +
            "FOREIGN KEY " + "(" + COLUMN_PAGE[1] + ") " +
            "REFERENCES " + TABLE_NAME[0] + "(" + COLUMN_BOOKLIST[0] + ")" +
            "FOREIGN KEY " + "(" + COLUMN_PAGE[4] + ") " +
            "REFERENCES " + TABLE_NAME[1] + "(" + COLUMN_BOOKLIST[0] + "))";
}
