package com.uos.makebook.Make;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.uos.makebook.Common.BookDB;
import com.uos.makebook.Common.Constant;
import com.uos.makebook.Common.DB;
import com.uos.makebook.MainList.Book;
import com.uos.makebook.Page.EditBookActivity;
import com.uos.makebook.R;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MakeCoverActivity extends AppCompatActivity {

    //액티비티
    Activity activity;

    //그림판 레이아웃
    CoverPaintBoard paintBoard;
    LinearLayout layout;

    //GUI
    Button color, clear, save, exit, delete;
    SeekBar border;
    RadioGroup eraser;
    RadioButton erS, erM, erB;
    EditText edit_title, edit_writer;

    //ColorPicker
    ColorPicker colorPicker;

    //database
    DB bookDB;
    Book book;

    //Mode -> 생성에서 온건지, 수정에서 온건지
    static int CREAT_MODE = 1;
    static int EDIT_MODE = 2;
    int mode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_makecover);
        Intent intent = getIntent();

        book = intent.getParcelableExtra("book");
        bookDB = new BookDB(getApplicationContext());

        color = findViewById(R.id.cover_btn_color);
        border = findViewById(R.id.cover_border);

        clear = findViewById(R.id.cover_btn_clear);
        save = findViewById(R.id.cover_btn_save);
        exit = findViewById(R.id.cover_btn_exit);
        delete = findViewById(R.id.cover_btn_delete);

        eraser = (RadioGroup)findViewById(R.id.cover_eraser);
        erS = findViewById(R.id.cover_erase_small);
        erB = findViewById(R.id.cover_erase_big);
        erM = findViewById(R.id.cover_erase_middle);
        edit_title = findViewById(R.id.editText_Title);
        edit_writer = findViewById(R.id.editText_Writer);

        Bitmap curbitmap = null;
        //Id가 넘어온거면, 생성이 아님
        if(book != null){
            mode = EDIT_MODE;
            edit_title.setText(book.getTitle());
            edit_writer.setText(book.getWriter());
            curbitmap = ((Book)bookDB.selectById(book.getId())).getCover();
        }else {
            book = new Book();
        }

        if(curbitmap == null){
            paintBoard = new CoverPaintBoard(this);
        }else{
            Bitmap newBitmap = curbitmap.copy(Bitmap.Config.ARGB_8888,true);
            paintBoard = new CoverPaintBoard(this, newBitmap);
        }

        layout = findViewById(R.id.LayoutCanvas);
        layout.addView(paintBoard);
        paintBoard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        //지우개
        eraser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.cover_erase_small:
                        paintBoard.setEraser(0);
                        break;
                    case R.id.cover_erase_middle:
                        paintBoard.setEraser(1);
                        break;
                    case R.id.cover_erase_big:
                        paintBoard.setEraser(2);
                        break;
                }
            }
        });

        //색상 설정
        initColorPicker();
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorPicker.getDialogViewLayout().getParent() != null){
                    ((ViewGroup)colorPicker.getDialogViewLayout().getParent()).removeView(colorPicker.getDialogViewLayout());
                }
                eraser.clearCheck();
                colorPicker.show();
            }
        });

        //굵기
        border.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintBoard.setBorder(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paintBoard.setBorder(seekBar.getProgress()*2);
            }
        });

        //비우기
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintBoard.clearBitmap();
            }
        });

        //취소
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constant.MAKECOVER_RESULT_EXIT);
                finish();
            }
        });

        //저장
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_title.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(),"책 제목을 입력해주세요",Toast.LENGTH_LONG).show();
                    return;
                }
                if(edit_writer.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(),"작가를 입력해주세요",Toast.LENGTH_LONG).show();
                    return;
                }
                book.setCover(paintBoard.mBitmap);
                book.setTitle(edit_title.getText().toString());
                book.setWriter(edit_writer.getText().toString());

                //edit -> 종료하고 돌아가기
                if(mode == EDIT_MODE){
                    bookDB.update(book);
                    setResult(Constant.MAKECOVER_RESULT_SUCCESS);
                    finish();
                }

                //insert -> 페이지 만들기
                else{
                    book.setId(bookDB.insert(book));
                    Intent nextIntent = new Intent(getApplicationContext(), EditBookActivity.class);
                    nextIntent.putExtra("book",book);
                    book.setCover(null);
                    startActivity(nextIntent);
                    finish();
                }

            }
        });

        //표지 삭제 (제목도 수정)
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_title.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(),"책 제목을 입력해주세요",Toast.LENGTH_LONG).show();
                    return;
                }
                if(edit_writer.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(),"작가를 입력해주세요",Toast.LENGTH_LONG).show();
                    return;
                }
                book.setTitle(edit_title.getText().toString());
                book.setWriter(edit_writer.getText().toString());

                //만들던 중이면
                if(mode == CREAT_MODE){
                    book.setCover(null);

                    //삽입
                    book.setId(bookDB.insert(book));
                    Intent nextIntent = new Intent(getApplicationContext(), EditBookActivity.class);
                    nextIntent.putExtra("book", book);
                    book.setCover(null);
                    startActivity(nextIntent);
                    finish();
                }

                //수정에서 들어온거면
                else if(mode == EDIT_MODE){
                    book.setCover(null);
                    bookDB.update(book);
                    setResult(Constant.MAKECOVER_RESULT_SUCCESS);
                    finish();
                }
                return;
            }
        });

    }

    public void initColorPicker(){
        colorPicker= new ColorPicker(this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                colorPicker.setColorButtonTickColor(position);
                paintBoard.setColor(color);
                paintBoard.setBorder(border.getProgress());
            }

            @Override
            public void onCancel() {
            }
        })
        .addListenerButton("newButton", new ColorPicker.OnButtonListener() {
                    @Override
                    public void onClick(View v, int position, int color) {
                        Toast.makeText(getApplicationContext(),"아",Toast.LENGTH_LONG).show();
                    }
                })
        .setColumns(5)
        .setTitle("색 고르기")
        .setDefaultColorButton(Color.BLACK)
        .setRoundColorButton(true);
    }
}
