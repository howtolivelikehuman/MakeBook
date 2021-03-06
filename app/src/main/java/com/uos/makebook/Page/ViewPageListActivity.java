package com.uos.makebook.Page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;

import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.MainList.Book;
import com.uos.makebook.MainList.BookListActivity;
import com.uos.makebook.R;


public class ViewPageListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PageListAdapter pagelistAdapter = new PageListAdapter();
    Toolbar toolbar;

    //book
    Book book;
    long book_id;
    String book_name;

    String mode;
    ArrayList<Page> pageList;

    //DB
    DB pageDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_pagelist);

        //DB setting
        pageDB = new PageDB(getApplicationContext());

        //인텐트로 값 받아오기
        Intent editIntent = getIntent();

        book = editIntent.getParcelableExtra("book");
        mode = editIntent.getStringExtra("mode");
        book_id = book.getId();
        book_name = book.getTitle();

        // DB로부터 값 받아오기 -> pageList에 넣기
        pageList = PageMethod.setPageList(pageDB, book_id);

        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(book_name);

        recyclerView = findViewById(R.id.recyPagelist);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);

        // pageListAdapter에서 bitmap의 크기를 결정하는데 사용됨.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        pagelistAdapter.setScreenSize(metrics);

        for(int i=0; i<pageList.size(); i++) {
            Page pageTemp = pageList.get(i);
            pagelistAdapter.addItem(pageTemp);
        }

        //각 페이지 아이템에 클릭리스너 붙이기
        pagelistAdapter.setOnItemClickListener(new PageListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) { //pos : 클릭한 아이템의 인덱스

                if(mode.equals("READ_MODE")){
                    Intent intent = new Intent(getApplicationContext(), ReadBookActivity.class);
                    book.setListIdx(pos);

                    intent.putExtra("list_idx", book.getListIdx());
                    intent.putExtra("book", book);

                    startActivity(intent);
                }
                else if(mode.equals("EDIT_MODE")){
                    Intent intent = new Intent(getApplicationContext(), EditBookActivity.class);
                    book.setListIdx(pos);

                    intent.putExtra("list_idx", book.getListIdx());
                    intent.putExtra("book", book);
                    startActivity(intent);
                }
                finish();
            }
        });

        recyclerView.setAdapter(pagelistAdapter);

    }

    @Override
    public void onBackPressed(){
        if(mode.equals("EDIT_MODE")){
            Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }


   /* public void setPageList(){
        System.out.println("setPageList");
        getPageListFromDB();
        if(pageList.size()==0){ // page 없으면 추가 후 다시 받아오기
            initialize();
            getPageListFromDB();
        }
        pageList.remove(0); // head 제거
        sortPageList(); // page 순서 정하기
    }


    public void initialize(){ // head와 첫 페이지 추가
        System.out.println("initialize");

        Page head = new Page(book_id, "[]", 0, 1);
        Page firstPage = new Page(book_id, "[]",0, 0);
        long head_pk = pageDB.insert(head); head.setPageId(head_pk);
        long first_pk = pageDB.insert(firstPage);
        head.setNextPage(first_pk);
        pageDB.update(head);
    }

    public void getPageListFromDB(){ // book_id에 해당하는 모든 page 가져오기
        String[] selection = {COLUMN_PAGE[1]};
        String[] selectionArgs = {Long.toString(book_id)};
        pageList = pageDB.select(selection, selectionArgs);
    }

    public Page getHead(){
        String[] selection = {COLUMN_PAGE[1], COLUMN_PAGE[4]};
        String[] selectionArgs = {Long.toString(book_id), Integer.toString(1)};

        ArrayList<Page> headList = pageDB.select(selection, selectionArgs); // head 데려오기
        if(headList.size() == 0) {
            System.out.println("Head가 없습니다.");
            return null;
        }
        return headList.get(0);
    }

    public void sortPageList(){
        System.out.println("sortPageList");
        ArrayList<Page> sortedPageList = new ArrayList<Page>();

        Page head = getHead();
        if(head == null)
            return;
        long nextPage = head.getNextPage();

        while(nextPage != 0 && !pageList.isEmpty()){ // nextPage 따라가면서 pageList 정렬
            for(int i=0; i<pageList.size(); i++){
                if(pageList.get(i).getPageId() != nextPage)
                    continue;
                sortedPageList.add(pageList.get(i));
                nextPage = pageList.get(i).getNextPage();
                pageList.remove(i);
                break;
            }
        }

        pageList = sortedPageList;
    }*/
}
