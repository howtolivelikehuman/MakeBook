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

import com.uos.makebook.Edit.EditBookActivity;
import com.uos.makebook.R;
import com.uos.makebook.Read.ReadBookActivity;


public class BookListActivity extends AppCompatActivity{
    //읽기, 수정
    static final int READ_MODE = 0;
    static final int EDIT_MODE = 1;

    int mode = READ_MODE;
    RecyclerView recyclerView;
    BookAdapter bookAdapter = new BookAdapter();
    Toolbar toolbar;
    Menu menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlist_booklist);

        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);



        recyclerView = findViewById(R.id.recyBooklist);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);

        //dummy data
        bookAdapter.addItem(new Book(1,"나의책1",null));
        bookAdapter.addItem(new Book(2,"나의책2",null));
        bookAdapter.addItem(new Book(3,"나의책3",null));
        bookAdapter.addItem(new Book(4,"나의책4",null));
        bookAdapter.addItem(new Book(5,"나의책5",null));
        bookAdapter.addItem(new Book(6,"나의책6",null));
        bookAdapter.addItem(new Book(7,"나의책7",null));
        bookAdapter.addItem(new Book(8,"나의책8",null));
        bookAdapter.addItem(new Book(1,"나의책1",null));
        bookAdapter.addItem(new Book(2,"나의책2",null));
        bookAdapter.addItem(new Book(3,"나의책3",null));
        bookAdapter.addItem(new Book(4,"나의책4",null));
        bookAdapter.addItem(new Book(5,"나의책5",null));
        bookAdapter.addItem(new Book(6,"나의책6",null));
        bookAdapter.addItem(new Book(7,"나의책7",null));
        bookAdapter.addItem(new Book(8,"나의책8",null));
        bookAdapter.addItem(new Book(1,"나의책1",null));
        bookAdapter.addItem(new Book(2,"나의책2",null));
        bookAdapter.addItem(new Book(3,"나의책3",null));
        bookAdapter.addItem(new Book(4,"나의책4",null));
        bookAdapter.addItem(new Book(5,"나의책5",null));
        bookAdapter.addItem(new Book(6,"나의책6",null));
        bookAdapter.addItem(new Book(7,"나의책7",null));
        bookAdapter.addItem(new Book(8,"나의책8",null));

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
