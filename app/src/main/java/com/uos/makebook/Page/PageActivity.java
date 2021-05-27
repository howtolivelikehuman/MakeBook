package com.uos.makebook.Page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.MainList.Book;
import com.uos.makebook.R;

import java.util.ArrayList;

import static com.uos.makebook.Common.Constant.COLUMN_PAGE;

public class PageActivity extends AppCompatActivity {
    int page_idx = 0; // 현재 보고있는 페이지

    //book
    Book book;
    long book_id;
    String book_name;

    //DB
    DB pageDB;

    // layout
    Button prev_button, next_button;
    Toolbar toolbar;
    ViewFlipper flipper;
    TextView page;
    Menu menu;

    ArrayList<Page> pageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("EditBookActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_bookpage);

        //DB setting
        pageDB = new PageDB(getApplicationContext());

        //인텐트로 값 받아오기
        Intent editIntent = getIntent();

        /***List에서 Parcelable로 Book 객체 자체를 넘기기 때문에, 코드부분 수정합니다!***/
        //book_id = editIntent.getIntExtra("Id", -1);
        //book_name = editIntent.getStringExtra("Name");
        book = editIntent.getParcelableExtra("book");
        book_id = book.getId();
        book_name = book.getTitle();



        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(book_name);

        //flipper 설정
        flipper = findViewById(R.id.flipper);
        prev_button = findViewById(R.id.btn_previous);
        next_button = findViewById(R.id.btn_next);

        // DB로부터 값 받아오기
        setPageList();
        updateButtonState();

        // 이전 버튼
        prev_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showPrevious();
                page_idx--;
                updateButtonState();
            }
        });

        // 다음 버튼
        next_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showNext();
                page_idx++;
                updateButtonState();
            }
        });
    }

    public void setPageList(){
        System.out.println("setPageList");

        getPageListFromDB();

        if(pageList.size()==0){ // page 없으면 추가 후 다시 받아오기
            initialize();
            getPageListFromDB();
        }
        pageList.remove(0); // head 제거

        sortPageList(); // page 순서 정하기
        makeFlipperByPageList(); // pagelist의 page를 flipper에 적용
    }

    public void initialize(){ // head와 첫 페이지 추가
        System.out.println("initialize");

        Page head = new Page(book_id, "0", 0, 1);
        Page firstPage = new Page(book_id, "1", 0, 0);
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

    public void makeFlipperByPageList(){
        System.out.println("makeFlipperByPageList");

        flipper.removeAllViews();
        for(int i=0; i<pageList.size(); i++){
            Page currentPage = pageList.get(i);
            flipper.addView(new PageCanvas(this, currentPage));
        };

        for(int i=0; i<page_idx; i++) { // 원래 보던 위치로 되돌려놓기
            flipper.showNext();
        }
    }

    public void addPageBeforeIdx(){ // 이전 페이지 추가
        System.out.println("EditBookActivity.addPageBeforeIdx");
        Page current_page, new_page;
        if(page_idx == 0){ // 첫 장 추가 시
            current_page = getHead();
            if(current_page == null)
                return;
        }else{
            current_page = pageList.get(page_idx-1);
        }
        new_page = new Page(book_id, "생성", 0, 0);
        new_page.setNextPage(current_page.nextPage);
        long pk = pageDB.insert(new_page); // 새 페이지 삽입

        current_page.setNextPage(pk);
        pageDB.update(current_page); // 기존 페이지 업데이트

        setPageList();
        System.out.println(page_idx);
    }

    public void addPageAfterIdx(){ // 다음 페이지 추가
        System.out.println("EditBookActivity.addPageAfterIdx");
        Page current_page, new_page;
        new_page = new Page(book_id, "생성", 0, 0);
        current_page = pageList.get(page_idx);
        new_page.setNextPage(current_page.nextPage);
        long pk = pageDB.insert(new_page); // 새 페이지 삽입

        current_page.setNextPage(pk);
        pageDB.update(current_page); // 기존 페이지 업데이트

        setPageList();
        System.out.println(page_idx);
    }

    public void removePageFromDB(){
        System.out.println("EditBookActivity.removePageFromDB");
        Page prevPage;
        if(page_idx==0){
            prevPage=getHead();
            if(prevPage == null)
                return;
        }else {
            prevPage=pageList.get(page_idx-1);
        }
        Page deletePage = pageList.get(page_idx);
        prevPage.nextPage = deletePage.nextPage;
        pageDB.update(prevPage);
        int num = pageDB.delete((int)deletePage.getPageId()); // pk long으로 통일하면 안될까욤?-? (희은)

        setPageList();
        if(page_idx == pageList.size()){
            page_idx--;
        }
    }

    public void updateButtonState(){
        System.out.println("updateButtonState");
        // 이전, 다음 버튼 상태 update
        int page_number = pageList.size();
        if(page_number == 0 || page_number == 1) {
            next_button.setEnabled(false);
            prev_button.setEnabled(false);
            return;
        }

        if(page_idx == page_number-1){
            // 마지막 페이지이면 다음 버튼 비활성화
            next_button.setEnabled(false);
        }else{
            next_button.setEnabled(true);
        }

        if(page_idx == 0){
            prev_button.setEnabled(false);
        }else{
            prev_button.setEnabled(true);
        }
    }
}
