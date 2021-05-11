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

import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.MainList.Book;
import com.uos.makebook.R;

import java.util.ArrayList;

import static com.uos.makebook.Common.Constant.COLUMN_PAGE;

public class EditBookActivity  extends AppCompatActivity {

    int page_idx = 0; // 현재 보고있는 페이지
    int page_number; // 페이지 개수

    //book
    int book_id;
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

        //값 없으면 dummy 넣기
        if(pageDB.selectAll() == null){
            for(int i=0; i<10; i++){
                pageDB.insert(new Page(i, 0,Integer.toString(i),null));
            }
        }

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
        getBookListFromDB();
        prev_button = findViewById(R.id.btn_previous);
        next_button = findViewById(R.id.btn_next);
        page_number = flipper.getChildCount();
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
            case R.id.action_create :
                // 페이지 추가
                System.out.println("페이지 추가");
                View PageView = View.inflate(this, R.layout.edit_editbook_page, null);
                page_number++;
                addPageFromDB(new Page(book_id, page_idx,"생성2", null));
                System.out.println(page_idx);
                setButtonEnable();
                Toast.makeText(getApplicationContext(),"페이지 생성", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_delete :
                // 페이지 삭제
                System.out.println("페이지 삭제");
                if(page_number == 1){
                    // 한 페이지만 남은 경우에는 삭제 안 됨
                    Toast.makeText(getApplicationContext(),"적어도 한 페이지는 있어야 합니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
                removePageFromDB(pageList.get(page_idx).id);
                page_idx--;
                page_number--;
                setButtonEnable();
                Toast.makeText(getApplicationContext(),"페이지 삭제", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getBookListFromDB(){
        flipper.removeAllViews();
        pageList = pageDB.select(COLUMN_PAGE[1], Integer.toString(book_id));
        for(int i=0; i<pageList.size(); i++){
            Page pageTemp = pageList.get(i);
            View PageView = View.inflate(this, R.layout.edit_editbook_page, null);
            // todo : 이미지를 설정해야함
            TextView text = PageView.findViewById(R.id.page_text);
            text.setText(pageTemp.text);
            flipper.addView(PageView);
        };
        for(int i=0; i<page_idx; i++) {
            flipper.showNext();
        }
    }

    public void addPageFromDB(Page page){
        pageDB.insert(page);
        getBookListFromDB();
    }
    public void removePageFromDB(int pk){
        pageDB.delete(pk);
        getBookListFromDB();
    }

    public void setButtonEnable(){
        if(page_number == 0 || page_number == 1){
            next_button.setEnabled(true);
            next_button.setEnabled(false);
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
