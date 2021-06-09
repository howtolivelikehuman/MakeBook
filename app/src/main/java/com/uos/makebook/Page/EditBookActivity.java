package com.uos.makebook.Page;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.Common.Constant;
import com.uos.makebook.Page.Element.ImageData;
import com.uos.makebook.Page.Element.TextData;
import com.uos.makebook.R;
import com.uos.makebook.MainList.BookListActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.uos.makebook.Common.Constant.EDIT_REQUEST;

public class EditBookActivity  extends PageActivity {
    private TextData editingText = null;
    private ImageData editingImage = null;
    private int isCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreated = getIntent().getIntExtra("code",-1);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
        startActivity(intent);
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
    public void complete(){
        super.complete();
        startActivity(new Intent(getApplicationContext(), BookListActivity.class));
        finish();
        //종료하지 말고 그냥 디비에 수정된 페이지 반영하고 flipper는 계속 떠있도록 하고, 뒤로가기 하면 페이지모아보기 뜨도록 하면 좋을 것 같은데 어떤가요?(다현)
    }
  
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.action_insert_text:
                System.out.println("글 추가");
                intent = new Intent(getApplicationContext(), AddTextActivity.class);
                startActivityForResult(intent, Constant.ADD_TEXT_REQUEST);
                return true;
            case R.id.action_insert_image:
                System.out.println("이미지 추가");
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "이미지 선택"), Constant.ADD_IMAGE_REQUEST);
                return true;
            case R.id.action_view_pagelist :
                // 페이지 모아보기
                Intent tmpIntent = new Intent(getApplicationContext(), ViewPageListActivity.class);
                book.setCover(null);
                tmpIntent.putExtra("book",book);
                tmpIntent.putExtra("mode", "EDIT_MODE");
                startActivity(tmpIntent);
                System.out.println("페이지 모아보기");
                finish();
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

    // 이미 있는 텍스트 객체의 내용을 수정.
    // PageCanvas에서 호출하며, 내부적으로 AddTextActivity를 사용한다는 점에서
    // 텍스트를 새로 추가하는 과정과 동일.
    public void startTextEditing(TextData textData) {
        Intent intent = new Intent(getApplicationContext(), AddTextActivity.class);
        intent.putExtra("value", textData.getText());
        intent.putExtra("fontSize", textData.getFontSize());
        intent.putExtra("fontColor", textData.getFontColor());

        editingText = textData;
        startActivityForResult(intent, Constant.EDIT_TEXT_REQUEST);
    }

    // 이미 있는 이미지 객체의 내용을 수정.
    public void startImageEditing(ImageData imageData) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        editingImage = imageData;
        startActivityForResult(Intent.createChooser(intent, "이미지 선택"), Constant.EDIT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        switch (requestCode) {
            case Constant.ADD_TEXT_REQUEST:
            case Constant.EDIT_TEXT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Page current = pageList.get(page_idx);
                    String value = data.getStringExtra("value");
                    int fontSize = data.getIntExtra("fontSize", 32);
                    int fontColor = data.getIntExtra("fontColor", Color.BLACK);

                    if (value.equals("")) {
                        Toast.makeText(getApplicationContext(), "내용이 없어 글이 추가되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (requestCode == Constant.ADD_TEXT_REQUEST) {
                            current.addText(value, fontSize, fontColor, flipper.getCurrentView().getWidth());
                        } else {
                            editingText.setText(value);
                            editingText.setFontSize(fontSize);
                            editingText.setFontColor(fontColor);
                            editingText = null;
                        }
                        flipper.getCurrentView().invalidate();
                    }
                } else {
                    System.err.println("EditBookActivity: Failed to add text.");
                }
                break;

            case Constant.ADD_IMAGE_REQUEST:
            case Constant.EDIT_IMAGE_REQUEST:
                Uri uri = data.getData();
                if (uri != null) {
                    String name = getFileNameFromUri(uri);
                    File dest = generateInternalFile(name + getRandomString(10));
                    try (FileOutputStream out = new FileOutputStream(dest)) {
                        Bitmap bitmap = getBitmapFromUri(uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                        if (requestCode == Constant.ADD_IMAGE_REQUEST) {
                            Page current = pageList.get(page_idx);
                            current.addImage(dest.getAbsolutePath(), flipper.getCurrentView().getWidth(), flipper.getCurrentView().getHeight());
                        } else {
                            editingImage.setSource(dest.getAbsolutePath());
                            editingImage = null;
                        }

                        flipper.getCurrentView().invalidate();
                    } catch (IOException e) {
                        System.err.println("EditBookActivity: Failed to add image.");
                    }
                } else {
                    System.err.println("EditBookActivity: Failed to add image.");
                }
                break;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
        cursor.close();
        return name;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            buffer.append('A' + ((int)(Math.random()*2600) % 26));
        }
        return buffer.toString();
    }

    private File generateInternalFile(String fileName) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);
        return new File(directory, fileName);
    }
}
