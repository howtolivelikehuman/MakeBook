package com.uos.makebook.Page;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.Common.Constant;
import com.uos.makebook.R;


public class EditBookActivity  extends PageActivity {
    public static final int REQUEST_TEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), ViewPageListActivity.class);
        book.setCover(null);
        intent.putExtra("book",book);
        intent.putExtra("mode", "EDIT_MODE");
        startActivityForResult(intent, Constant.EDIT_REQUEST);
        super.onBackPressed();
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
            case R.id.action_insert_text:
                System.out.println("글 추가");
                Intent intent = new Intent(this, AddTextActivity.class);
                startActivityForResult(intent, REQUEST_TEXT);
                return true;
            case R.id.action_insert_image:
                return true;
            case R.id.action_edit_done :
                // 페이지 수정
                System.out.println("책 수정 완료");
                //finish();
                //종료하지 말고 그냥 디비에 수정된 페이지 반영하고 flipper는 계속 떠있도록 하고, 뒤로가기 하면 페이지모아보기 뜨도록 하면 좋을 것 같은데 어떤가요?(다현)
                Toast.makeText(getApplicationContext(),"페이지 수정 완료", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_create_prev :
                // 페이지 추가
                System.out.println("이전 페이지 추가");
                addPageBeforeIdx();
                updateButtonState();
                Toast.makeText(getApplicationContext(),"페이지 생성", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_create_next :
                // 페이지 추가
                System.out.println("이후 페이지 추가");
                addPageAfterIdx();
                updateButtonState();
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
                updateButtonState();
                Toast.makeText(getApplicationContext(),"페이지 삭제", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEXT) {
            if (resultCode == RESULT_OK) {
                Page current = pageList.get(page_idx);
                String value = data.getStringExtra("value");
                int fontSize = data.getIntExtra("fontSize", 32);
                int fontColor = data.getIntExtra("fontColor", Color.BLACK);
                current.addText(value, fontSize, fontColor);
                flipper.getCurrentView().invalidate();
            } else {
                System.err.println("EditBookActivity: Failed to add text.");
            }
        }
    }
}
