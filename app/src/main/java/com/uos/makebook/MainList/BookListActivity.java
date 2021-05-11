package com.uos.makebook.MainList;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uos.makebook.Common.BookDB;
import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.DatabaseHelper;
import com.uos.makebook.Edit.EditBookActivity;
import com.uos.makebook.R;
import com.uos.makebook.Read.ReadBookActivity;


public class BookListActivity extends AppCompatActivity{
    //읽기, 수정
    static final int READ_MODE = 0;
    static final int EDIT_MODE = 1;
    int mode = READ_MODE;

    //DB
    DB bookDB;

    //GUI
    RecyclerView recyclerView;
    BookAdapter bookAdapter = new BookAdapter();
    Toolbar toolbar;
    Menu menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainlist_booklist);

        //DB setting
        bookDB = new BookDB(getApplicationContext());

        //값 없으면 dummy 넣기
        if(bookDB.selectAll() == null){
            for(int i=0; i<10; i++){
                bookDB.insert(new Book(0,"나의책 " + i,null));
            }
        }


        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        recyclerView = findViewById(R.id.recyBooklist);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);

        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Book book = bookAdapter.getItem(pos);
                Intent nextIntent;

                switch (mode){
                    case EDIT_MODE :
                        nextIntent = new Intent(getApplicationContext(), EditBookActivity.class);
                        break;

                    case READ_MODE :
                        nextIntent = new Intent(getApplicationContext(), ReadBookActivity.class);
                        break;

                    default :
                        Toast.makeText(getApplicationContext(), "선택에 오류가 발생했습니다", Toast.LENGTH_LONG).show();
                        return;
                }
                nextIntent.putExtra("Id", book.id);
                nextIntent.putExtra("Name", book.name);
                startActivity(nextIntent);
                return;
            }
        });
        //DB에서 데이터 받아오기
        bookAdapter.setItems(bookDB.selectAll());
        recyclerView.setAdapter(bookAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.menu = menu;
        getMenuInflater().inflate(R.menu.booklist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.action_read :
                mode = EDIT_MODE;
                menu.findItem(R.id.action_edit).setVisible(true);
                item.setVisible(false);
                Toast.makeText(getApplicationContext(),"편집 모드", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_edit:
                mode = READ_MODE;
                menu.findItem(R.id.action_read).setVisible(true);
                item.setVisible(false);
                Toast.makeText(getApplicationContext(),"감상 모드", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
