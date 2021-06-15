package com.uos.makebook.Page;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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

    //book
    Book book;
    long book_id;
    String book_name;

    //DB
    DB pageDB;
    // 페이지가 업데이트 되었을 때 실행될 이벤트 리스너 (주로 DB 업데이트를 함.)
    PageUpdateEventListener pageUpdateEventListener;

    // layout
    Button prev_button, next_button, complete_button;
    Toolbar toolbar;
    ViewFlipper flipper;
    TextView page;
    Menu menu;

    ArrayList<Page> pageList;
    int page_idx; // 현재 보고있는 페이지



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_bookpage);

        //DB setting
        pageDB = new PageDB(getApplicationContext());
        pageUpdateEventListener = sender -> pageDB.update(sender);

        //인텐트로 값 받아오기
        Intent intent = getIntent();
        book = intent.getParcelableExtra("book");
        book_id = book.getId();
        book_name = book.getTitle();
        page_idx = intent.getIntExtra("list_idx", -1);


        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(book_name);

        //flipper 설정
        flipper = findViewById(R.id.flipper);
        prev_button = findViewById(R.id.btn_previous);
        next_button = findViewById(R.id.btn_next);
        complete_button = findViewById(R.id.btn_complete);

        // DB로부터 값 받아오기
        setPageList();
        makeFlipperByPageList(); // pagelist의 page를 flipper에 적용
        updateButtonState();

        // 이전 버튼
        prev_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                gotoPrev();
            }
        });

        // 다음 버튼
        next_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                gotoNext();
            }
        });

        complete_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                complete();
            }
        });
    }

    public void gotoPrev(){ // 이전 페이지로 이동
        flipper.showPrevious();
        page_idx--;
        updateButtonState();
    }

    public void gotoNext(){ // 이후 페이지로 이동
        flipper.showNext();
        page_idx++;
        updateButtonState();
    }

    public void complete(){} // 완료

    public void setPageList(){ // pageList 만들기
        System.out.println("setPageList");

        getPageListFromDB(); // DB로부터 page 받아오기 (정렬 안 된 상태)

        if(pageList.size()==0){ // page 없으면 추가 후 다시 받아오기
            initialize();
            getPageListFromDB();
        }
        pageList.remove(0); // head 제거

        // Update시 DB에 반영하기 위해 listener 적용
        for (Page p : pageList) {
            p.setPageUpdateEventListener(pageUpdateEventListener);
        }

        sortPageList(); // page 순서 정하기
        makeFlipperByPageList(); // pagelist의 page를 flipper에 적용
    }

    public void initialize(){ // head와 첫 페이지 추가
        System.out.println("initialize");

        Page head = new Page(book_id, "[]", 0, 1);
        Page firstPage = new Page(book_id, "[]", 0, 0);
        long head_pk = pageDB.insert(head); head.setPageId(head_pk);
        long first_pk = pageDB.insert(firstPage);
        head.setNextPage(first_pk);
        pageDB.update(head);

        head.setPageUpdateEventListener(pageUpdateEventListener);
        firstPage.setPageUpdateEventListener(pageUpdateEventListener);
    }

    public void getPageListFromDB(){ // book_id에 해당하는 모든 page 가져오기
        String[] selection = {COLUMN_PAGE[1]};
        String[] selectionArgs = {Long.toString(book_id)};
        pageList = pageDB.select(selection, selectionArgs);
    }

    public void sortPageList(){ // pageList nextPage에 따라 정렬하기
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

    public Page getHead(){ // 해당 책의 head 페이지 찾기
        String[] selection = {COLUMN_PAGE[1], COLUMN_PAGE[4]};
        String[] selectionArgs = {Long.toString(book_id), Integer.toString(1)};

        ArrayList<Page> headList = pageDB.select(selection, selectionArgs); // head 데려오기
        if(headList.size() == 0) {
            System.out.println("Head가 없습니다.");
            return null;
        }
        return headList.get(0);
    }

    public void makeFlipperByPageList(){ // pagelist를 flipper에 등록하기
        System.out.println("makeFlipperByPageList");

        flipper.removeAllViews();
        for(int i=0; i<pageList.size(); i++){
            Page currentPage = pageList.get(i);
            flipper.addView(generatePageCanvas(currentPage));
        };

        for(int i=0; i<page_idx; i++) { // 원래 보던 위치로 되돌려놓기
            flipper.showNext();
        }
    }

    // makeFlipperByPageList 함수에서 필요한 PageCanvas를 생성해서 반환.
    // 파생 클래스인 EditBookActivity와 ReadBookActivity는 이 함수를
    // override하여 isEditable 인자를 적절히 설정해야 함.
    protected PageCanvas generatePageCanvas(Page page) {
        return new PageCanvas(this, page, false);
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
        new_page = new Page(book_id, "[]", 0, 0);
        new_page.setNextPage(current_page.nextPage);
        long pk = pageDB.insert(new_page); // 새 페이지 삽입
        new_page.setPageUpdateEventListener(pageUpdateEventListener);

        current_page.setNextPage(pk);
        pageDB.update(current_page); // 기존 페이지 업데이트
        setPageList();
        System.out.println(page_idx);
    }


    public void addPageAfterIdx(){ // 다음 페이지 추가
        System.out.println("EditBookActivity.addPageAfterIdx");
        Page current_page, new_page;
        new_page = new Page(book_id, "[]", 0, 0);
        current_page = pageList.get(page_idx);
        new_page.setNextPage(current_page.nextPage);
        long pk = pageDB.insert(new_page); // 새 페이지 삽입

        current_page.setNextPage(pk);
        pageDB.update(current_page); // 기존 페이지 업데이트
        new_page.setPageUpdateEventListener(pageUpdateEventListener);
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
        // 이전, 다음 버튼 상태 update
        int page_number = pageList.size();
        System.out.println(page_number);
        if(page_number == 0 || page_number == 1) {
            next_button.setEnabled(false);
            next_button.setBackgroundResource(R.drawable.diable_btn);
            prev_button.setEnabled(false);
            prev_button.setBackgroundResource(R.drawable.diable_btn);
            return;
        }

        if(page_idx == page_number-1){
            // 마지막 페이지이면 다음 버튼 비활성화
            next_button.setEnabled(false);
            next_button.setBackgroundResource(R.drawable.diable_btn);
        }else{
            next_button.setEnabled(true);
            next_button.setBackgroundResource(R.drawable.yellow_btn);
        }

        if(page_idx == 0){
            prev_button.setEnabled(false);
            prev_button.setBackgroundResource(R.drawable.diable_btn);
        }else{
            prev_button.setEnabled(true);
            prev_button.setBackgroundResource(R.drawable.yellow_btn);
        }
    }
}
