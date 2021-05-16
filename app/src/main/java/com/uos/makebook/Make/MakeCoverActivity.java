package com.uos.makebook.Make;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.uos.makebook.Common.Constant;
import com.uos.makebook.R;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MakeCoverActivity extends AppCompatActivity {

    //액티비티
    Activity activity;

    //그림판 레이아웃
    CoverPaintBoard paintBoard;
    LinearLayout layout;

    //GUI
    Button color, clear, save, exit;
    SeekBar border;
    RadioGroup eraser;
    RadioButton erS, erM, erB;

    //ColorPicker
    ColorPicker colorPicker;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_makecover);


        layout = findViewById(R.id.LayoutCanvas);
        paintBoard = new CoverPaintBoard(this);
        layout.addView(paintBoard);
        paintBoard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        color = findViewById(R.id.cover_btn_color);
        clear = findViewById(R.id.cover_btn_clear);
        save = findViewById(R.id.cover_btn_save);
        exit = findViewById(R.id.cover_btn_exit);
        border = findViewById(R.id.cover_border);

        eraser = (RadioGroup)findViewById(R.id.cover_eraser);
        erS = findViewById(R.id.cover_erase_small);
        erB = findViewById(R.id.cover_erase_big);
        erM = findViewById(R.id.cover_erase_middle);

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

        //비우기
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintBoard.clearBitmap();
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

        //취소
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Constant.MAKECOVER_RESULT_EXIT);
                finish();
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
