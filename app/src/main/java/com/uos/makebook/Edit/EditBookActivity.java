package com.uos.makebook.Edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.uos.makebook.R;

public class EditBookActivity  extends AppCompatActivity {

    int id;
    int page_number; // 페이지 개수
    int page_idx; // 현재 보고있는 페이지
    String book_name;
    Button prev_button, next_button;
    Toolbar toolbar;
    ViewFlipper flipper;
    TextView page;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_editbook);

        //인텐트로 값 받아오기
        Intent editIntent = getIntent();
        id = editIntent.getIntExtra("Id", -1);
        book_name = editIntent.getStringExtra("Name");

        //툴바 설정
        toolbar=findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(book_name);

        //flipper 설정
        flipper = findViewById(R.id.flipper);
        prev_button = findViewById(R.id.btn_previous);
        next_button = findViewById(R.id.btn_next);
        page_number = flipper.getChildCount();

        prev_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showPrevious();
                page_idx--;
                next_button.setEnabled(true);
                if(page_idx == 0){
                    // 첫 페이지이면 이전 버튼 비활성화
                    prev_button.setEnabled(false);
                }
            }
        });
        next_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                flipper.showNext();
                page_idx++;
                prev_button.setEnabled(true);
                if(page_idx == page_number-1){
                    // 마지막 페이지이면 다음 버튼 비활성화
                    next_button.setEnabled(false);
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.action_create :
                // 페이지 추가
                // todo : page에 id 넘겨서 할당 하기 (DB랑 연결 공부)
                View PageView = View.inflate(this, R.layout.edit_editbook_page, null);
                System.out.println(page_idx);
                flipper.addView(PageView, page_idx); // 현재 보고 있는 다음 페이지에 넣기 => 아직 부정확 (맨 뒤에 추가됨)
                page_number++;
                next_button.setEnabled(true);
                Toast.makeText(getApplicationContext(),"페이지 생성", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
