package com.uos.makebook.Edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.uos.makebook.R;

public class EditBookActivity  extends AppCompatActivity {

    int id;
    String name;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_editbook);

        button = findViewById(R.id.buttonEditBook);

        //인텐트로 값 받아오기
        Intent editIntent = getIntent();
        id = editIntent.getIntExtra("Id", -1);
        name = editIntent.getStringExtra("Name");

        button.setText(id+"번 - "+ name + " 책 편집하기");
    }
}
