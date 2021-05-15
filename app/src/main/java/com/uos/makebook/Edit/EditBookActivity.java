package com.uos.makebook.Edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.uos.makebook.Common.Constant;
import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.MainList.Book;
import com.uos.makebook.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.uos.makebook.Common.Constant.COLUMN_PAGE;

public class EditBookActivity  extends AppCompatActivity {

    int page_idx = 0; // 현재 보고있는 페이지

    //book
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_editbook);

        //DB setting
        pageDB = new PageDB(getApplicationContext());

        //인텐트로 값 받아오기
        Intent editIntent = getIntent();
        book_id = editIntent.getIntExtra("Id", -1);
        book_name = editIntent.getStringExtra("Name");

        //툴바 설정
        toolbar=findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(book_name);

        //flipper 설정
        flipper = findViewById(R.id.flipper);
        prev_button = findViewById(R.id.btn_previous);
        next_button = findViewById(R.id.btn_next);

        // DB로부터 값 받아오기
        getPageListFromDB();
        setButtonEnable();

        // 이전 버튼
        prev_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showPrevious();
                page_idx--;
                setButtonEnable();
            }
        });

        // 다음 버튼
        next_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showNext();
                page_idx++;
                setButtonEnable();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // edit menu 연결
        this.menu = menu;
        getMenuInflater().inflate(R.menu.editbook_menu, menu);
        return true;
    }

    // todo : DB와 연동해서 page 추가 삭제 하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.action_create_prev :
                // 페이지 추가
                System.out.println("페이지 추가");
                addPageBeforeIdx(page_idx);
                setButtonEnable();
                Toast.makeText(getApplicationContext(),"페이지 생성", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_create_next :
                // 페이지 추가
                System.out.println("페이지 추가");
                addPageAfterIdx(page_idx);
                setButtonEnable();
                Toast.makeText(getApplicationContext(),"페이지 생성", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_delete :
                // 페이지 삭제
                System.out.println("페이지 삭제");
                if(pageList.size() == 1){
                    // 한 페이지만 남은 경우에는 삭제 안 됨
                    Toast.makeText(getApplicationContext(),"적어도 한 페이지는 있어야 합니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
                removePageFromDB();
                setButtonEnable();
                Toast.makeText(getApplicationContext(),"페이지 삭제", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialize(){ // head와 첫 페이지 추가해주기
        System.out.println("initialize");
        Page head = new Page(book_id, "0",null,0, 1);
        Page firstPage = new Page(book_id, "1",null,0, 0);
        long head_pk = pageDB.insert(head); head.setId(head_pk);
        long first_pk = pageDB.insert(firstPage);
        head.setNextPage(first_pk);
        pageDB.update(head);
        pageList = pageDB.select(COLUMN_PAGE[1], Long.toString(book_id));
        pageList.remove(0); //head 제거
    }

    private void getPageListFromDB(){ // DB로부터 page list 가져와서 저장하기
        System.out.println("getPageListFromDB");
        pageList = pageDB.select(COLUMN_PAGE[1], Long.toString(book_id));
        if(pageList.size()==0){
            initialize();
            pageList = pageDB.select(COLUMN_PAGE[1], Long.toString(book_id));
        }
        pageList.remove(0); //head 제거

        sortPageList();

        flipper.removeAllViews();
        makeFlipperByPageList();
    }

    private void makeFlipperByPageList(){
        System.out.println("makeFlipperByPageList");
        for(int i=0; i<pageList.size(); i++){
            Page pageTemp = pageList.get(i);
            View PageView = View.inflate(this, R.layout.edit_editbook_page, null);
            // todo : 이미지를 설정해야함
            TextView text = PageView.findViewById(R.id.page_text);
            text.setText(pageTemp.text);
            flipper.addView(PageView);
        };

        for(int i=0; i<page_idx; i++) { // 원래 보던 위치로 되돌려놓기
            flipper.showNext();
        }
    }

    private void sortPageList(){
        System.out.println("Sort Page");
        ArrayList<Page> sortedPageList = new ArrayList<Page>();

        Page head = getHead();
        if(head == null)
            return;
        long nextPage = head.nextPage;
        System.out.println(nextPage);
        while(nextPage != 0 && !pageList.isEmpty()){ // nextPage 따라가면서 pageList 정렬
            for(int i=0; i<pageList.size(); i++){
                if(pageList.get(i).id != nextPage)
                    continue;
                sortedPageList.add(pageList.get(i));
                nextPage = pageList.get(i).nextPage;
                System.out.println(nextPage);
                pageList.remove(i);
                break;
            }
        }
        pageList = sortedPageList;
    }

    private Page getHead(){
        String[] selection = {COLUMN_PAGE[1], COLUMN_PAGE[5]};
        String[] selectionArgs = {Long.toString(book_id), Integer.toString(1)};
        System.out.println(book_id);
        ArrayList<Page> headList = pageDB.select(selection, selectionArgs); // head 데려오기
        if(headList.size() == 0) {
            System.out.println("Head가 없습니다.");
            return null;
        }
        return headList.get(0);
    }

    private void addPageBeforeIdx(int idx){ // 현재 idx의 page를 한 칸 뒤로 밀기
        Page current_page, new_page;
        if(idx == 0){ // 첫 장 추가 시
            current_page = getHead();
        }else{
            current_page = pageList.get(idx-1);
        }
        if(current_page == null)
            return;
        new_page = new Page(book_id, "생성", null, 0, 0);
        new_page.setNextPage(current_page.nextPage); // 새 페이지 삽입
        long pk = pageDB.insert(new_page);

        current_page.setNextPage(pk); // 기존 페이지 업데이트
        pageDB.update(current_page);

        getPageListFromDB();
    }

    private void addPageAfterIdx(int idx){
        Page current_page, new_page;
        new_page = new Page(book_id, "생성", null, 0, 0);
        current_page = pageList.get(idx);
        new_page.setNextPage(current_page.nextPage); // 새 페이지 삽입
        long pk = pageDB.insert(new_page);

        current_page.setNextPage(pk); // 기존 페이지 업데이트
        pageDB.update(current_page);

        getPageListFromDB();
    }

    private void removePageFromDB(){
        Page prevPage;
        if(page_idx==0){
            prevPage=getHead();
        }else {
            prevPage=pageList.get(page_idx-1);
        }
        Page deletePage = pageList.get(page_idx);
        prevPage.nextPage = deletePage.nextPage;
        pageDB.update(prevPage);
        int num = pageDB.delete((int)deletePage.getId()); // pk long으로 통일하면 안될까욤?-? (희은)

        getPageListFromDB();
    }

    private void setButtonEnable(){
        System.out.println("setButtonEnable");
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
