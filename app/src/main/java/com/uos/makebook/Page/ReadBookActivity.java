package com.uos.makebook.Page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.uos.makebook.Common.PageDB;
import com.uos.makebook.R;

public class ReadBookActivity extends PageActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // edit menu 연결
        this.menu = menu;
        getMenuInflater().inflate(R.menu.readbook_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.record :
                // 페이지 추가
                System.out.println("녹음 시작/끝");
                Toast.makeText(getApplicationContext(),"녹음 시작", Toast.LENGTH_LONG).show();

                return true;
            case R.id.playThis :
                // 페이지 추가
                System.out.println("해당 페이지 녹음 재생");
                Toast.makeText(getApplicationContext(),"녹음 재생", Toast.LENGTH_LONG).show();
                return true;
            case R.id.playAll :
                // 페이지 삭제
                System.out.println("전체 페이지 재생");
                Toast.makeText(getApplicationContext(),"전체 재생", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}