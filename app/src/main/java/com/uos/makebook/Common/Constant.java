package com.uos.makebook.Common;

import java.util.ArrayList;

public class Constant {
    private Constant(){}

    //RESULT CODE
    public static final int MAKECOVER_REQUEST = 100;
    public static final int MAKECOVER_RESULT_SUCCESS = 101;
    public static final int MAKECOVER_RESULT_EXIT = 102;

    public static final int EDIT_REQUEST = 200;
    public static final int READ_REQUEST = 300;
    public static final int MAKE_REQUEST = 400;
    public static final int DELETE_REQUEST = 500;
    public static final int ADD_TEXT_REQUEST = 600;
    public static final int ADD_IMAGE_REQUEST = 700;
    public static final int EDIT_TEXT_REQUEST = 800;
    public static final int EDIT_IMAGE_REQUEST = 900;

    public static final String DATABASE_NAME = "MAKEBOOK";
    public static final String[] TABLE_NAME = {"BOOKLIST", "PAGE"};
    public static final String[] COLUMN_BOOKLIST = {"ID", "TITLE", "WRITER", "IMAGE", "CREATEDATE"};
    public static final String[] COLUMN_PAGE = {"ID", "BOOKID", "CONTENTS", "NEXTPAGE", "ISHEAD"};

    //책 테이블 생성
    public static final String CREATE_TABLE_BOOKLIST = "CREATE TABLE " + TABLE_NAME[0] + "(" +
            COLUMN_BOOKLIST[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_BOOKLIST[1] + " TEXT, " +
            COLUMN_BOOKLIST[2] + " TEXT, " +
            COLUMN_BOOKLIST[3] + " BLOB, " +
            COLUMN_BOOKLIST[4] + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    //페이지 테이블 생성
    public static final String CREATE_TABLE_PAGE = "CREATE TABLE " + TABLE_NAME[1] + "(" +
            COLUMN_PAGE[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PAGE[1] + " INTEGER, " +
            COLUMN_PAGE[2] + " TEXT, " +
            COLUMN_PAGE[3] + " INTEGER, " +
            COLUMN_PAGE[4] + " INTEGER, " +
            "FOREIGN KEY " + "(" + COLUMN_PAGE[1] + ") " +
            "REFERENCES " + TABLE_NAME[0] + "(" + COLUMN_BOOKLIST[0] + ")" +
            "FOREIGN KEY " + "(" + COLUMN_PAGE[3] + ") " +
            "REFERENCES " + TABLE_NAME[1] + "(" + COLUMN_BOOKLIST[0] + "))";
}
