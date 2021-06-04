package com.uos.makebook.Page;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.uos.makebook.R;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddTextActivity extends AppCompatActivity {
    private TextView doneText;
    private TextView colorText;
    private TextView sizeText;
    private EditText editText;
    private ColorPicker colorPicker;
    private int textColor = Color.BLACK;
    private int textSize = 64;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_bookpage_addtext);

        colorPicker = new ColorPicker(this);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                colorPicker.setColorButtonTickColor(position);
                textColor = color;
                editText.setTextColor(textColor);
            }

            @Override
            public void onCancel() { }
        });

        colorPicker.setColumns(5);
        colorPicker.setTitle("글자 색 고르기");
        colorPicker.setDefaultColorButton(Color.BLACK);
        colorPicker.setRoundColorButton(true);

        doneText = findViewById(R.id.add_text_done);
        colorText = findViewById(R.id.add_text_color);
        sizeText = findViewById(R.id.add_text_size);
        editText = findViewById(R.id.add_text_edit_text);

        doneText.setOnClickListener(v -> {
            Intent intent = new Intent();

            intent.putExtra("value", editText.getText().toString());
            intent.putExtra("fontColor", textColor);
            intent.putExtra("fontSize", textSize);

            setResult(RESULT_OK, intent);
            finish();
        });

        colorText.setOnClickListener(v -> colorPicker.show());

        sizeText.setOnClickListener(v -> {
            NumberPicker picker = new NumberPicker(this);
            picker.setMaxValue(300);
            picker.setMinValue(5);
            picker.setValue(textSize);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(picker);
            builder.setTitle("글자 크기 선택");
            builder.setPositiveButton("OK", (dialog, which) -> {
                textSize = picker.getValue();
                editText.setTextSize(textSize);
            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> { });
            builder.create().show();
        });
    }
}
