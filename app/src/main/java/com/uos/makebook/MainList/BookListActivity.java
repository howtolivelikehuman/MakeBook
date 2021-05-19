package com.uos.makebook.MainList;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uos.makebook.Common.BookDB;
import com.uos.makebook.Common.Constant;
import com.uos.makebook.Common.DB;
import com.uos.makebook.Page.EditBookActivity;
import com.uos.makebook.Page.ReadBookActivity;
import com.uos.makebook.R;
import com.uos.makebook.Make.MakeCoverActivity;


public class BookListActivity extends AppCompatActivity{
    //읽기, 수정
    //static final int READ_MODE = 0;
    //static final int EDIT_MODE = 1;
    //int mode = READ_MODE;

    //DB
    DB bookDB;

    //GUI
    RecyclerView recyclerView;
    BookAdapter bookAdapter = new BookAdapter();
    Toolbar toolbar;
    Menu menu;

    //팝업
    Dialog popUp;
    AlertDialog finalAsk;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainlist_booklist);

        //DB setting
        bookDB = new BookDB(getApplicationContext());

        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        //팝업 설정
        popUp = new Dialog(BookListActivity.this);
        popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUp.setContentView(R.layout.mainlist_popup);

        AlertDialog.Builder builder = new AlertDialog.Builder(BookListActivity.this);

        recyclerView = findViewById(R.id.recyBooklist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Book book = bookAdapter.getItem(pos);

                //만들기
                if(pos == 0){
                    Toast.makeText(getApplicationContext(), "책 만들기를 시작합니다", Toast.LENGTH_LONG).show();
                    Intent nextIntent = new Intent(getApplicationContext(), MakeCoverActivity.class);
                    startActivityForResult(nextIntent, Constant.MAKECOVER_REQUEST);
                }

                //그외 클릭
                else{
                   showPopUp(book, builder);
                }
                return;
            }
        });

        //DB에서 데이터 받아오기
        bookAdapter.setItems(bookDB.selectAll());
        recyclerView.setAdapter(bookAdapter);
    }

    public void refresh(){
        bookAdapter.setItems(bookDB.selectAll());
        bookAdapter.notifyDataSetChanged();
    }

    public void showPopUp(Book book, AlertDialog.Builder builder){
        //GUI
        TextView cover, edit, read, delete;

        popUp.show();
        cover =  popUp.findViewById(R.id.popuptv_cover);
        edit =  popUp.findViewById(R.id.popuptv_edit);
        delete =  popUp.findViewById(R.id.popuptv_delete);
        read =  popUp.findViewById(R.id.popuptv_read);

        //표지만들기
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeCoverActivity.class);
                book.setCover(null);
                intent.putExtra("book",book);
                startActivityForResult(intent, Constant.MAKECOVER_REQUEST);
                popUp.dismiss();
            }
        });

        //수정하기
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditBookActivity.class);
                book.setCover(null);
                intent.putExtra("book",book);
                startActivityForResult(intent,Constant.EDIT_REQUEST);
                popUp.dismiss();
            }
        });

        //읽기
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadBookActivity.class);
                book.setCover(null);
                intent.putExtra("book",book);
                startActivityForResult(intent, Constant.READ_REQUEST);
                popUp.dismiss();
            }
        });

        //삭제하기
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteFinalAsk(book.getId(),builder);
                popUp.dismiss();
            }
        });
    }

    public void onDeleteFinalAsk(long bookId, AlertDialog.Builder builder){
        builder.setMessage("책을 삭제한 뒤엔 복구할 수 없습니다. \n삭제하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                bookDB.delete(bookId);
                Toast.makeText(getApplicationContext(), "책 삭제를 완료하였습니다.", Toast.LENGTH_LONG).show();
                refresh();
                finalAsk.dismiss();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                finalAsk.dismiss();
            }
        });
        finalAsk = builder.create();
        finalAsk.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            //표지 수정
            case Constant.MAKECOVER_REQUEST :
                //성공
                if (resultCode == Constant.MAKECOVER_RESULT_SUCCESS) {
                    Toast.makeText(getApplicationContext(), "표지 설정에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    refresh();
                    return;
                }
                break;

                //읽기
            case Constant.READ_REQUEST :
                break;
            //수정
            case Constant.EDIT_REQUEST:
                break;
        }

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
                menu.findItem(R.id.action_edit).setVisible(true);
                item.setVisible(false);
                Toast.makeText(getApplicationContext(),"편집 모드", Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_edit:
                menu.findItem(R.id.action_read).setVisible(true);
                item.setVisible(false);
                Toast.makeText(getApplicationContext(),"감상 모드", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
