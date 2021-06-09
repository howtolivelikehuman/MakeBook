package com.uos.makebook.MainList;

import android.Manifest;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pedro.library.AutoPermissions;
import com.uos.makebook.Common.BookDB;
import com.uos.makebook.Common.Constant;
import com.uos.makebook.Common.DB;
import com.uos.makebook.Common.PageDB;
import com.uos.makebook.Page.EditBookActivity;
import com.uos.makebook.Page.Page;
import com.uos.makebook.Page.PageMethod;
import com.uos.makebook.Page.ReadBookActivity;
import com.uos.makebook.R;
import com.uos.makebook.Make.MakeCoverActivity;
import com.uos.makebook.Page.ViewPageListActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity{

    //DB
    DB bookDB;

    //GUI
    RecyclerView recyclerView;
    BookAdapter bookAdapter = new BookAdapter();
    Toolbar toolbar;
    Menu menu;

    //팝업
    PopupMenu popUp;
    AlertDialog finalAsk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainlist_booklist);

        //안드로이드 권한 질의
        AutoPermissions.Companion.loadAllPermissions(this,101);
        permissionCheck();

        //DB setting
        bookDB = new BookDB(getApplicationContext());

        //툴바 설정
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        recyclerView = findViewById(R.id.recyBooklist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        /*.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    View reV = rv.findChildViewUnder(e.getX(), e.getY());
                    int position = rv.getChildAdapterPosition(reV);


                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });*/

        bookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Book book = bookAdapter.getItem(pos);
                showPopUp(book, v);
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

    public void showPopUp(Book book, View view){

        //팝업메뉴
        if(Build.VERSION.SDK_INT >= 22){
            popUp = new PopupMenu(getApplicationContext(), view,Gravity.END,0,R.style.MyPopupMenu);
        }
        else{
            popUp = new PopupMenu(getApplicationContext(), view);
        }

        popUp.inflate(R.menu.mainlist_popup);

        popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    //표지만들기
                    case R.id.popuptv_cover : {
                        Intent intent = new Intent(getApplicationContext(), MakeCoverActivity.class);
                        book.setCover(null);
                        intent.putExtra("book",book);
                        startActivityForResult(intent, Constant.MAKECOVER_REQUEST);
                        popUp.dismiss();
                        break;
                    }
                    //읽기
                    case R.id.popuptv_read : {
                        Intent intent = new Intent(getApplicationContext(), ViewPageListActivity.class);
                        book.setCover(null);
                        intent.putExtra("book",book);
                        intent.putExtra("mode", "READ_MODE");
                        startActivity(intent);
                        popUp.dismiss();
                        //finish(); //추가
                        break;
                    }
                    //수정하기
                    case R.id.popuptv_edit : {
                        Intent intent = new Intent(getApplicationContext(), ViewPageListActivity.class);
                        book.setCover(null);
                        intent.putExtra("book",book);
                        intent.putExtra("mode", "EDIT_MODE");
                        startActivity(intent);
                        popUp.dismiss();
                        finish(); //추가
                        break;
                    }
                    //삭제하기
                    case R.id.popuptv_delete : {
                        onDeleteFinalAsk(book.getId());
                        popUp.dismiss();
                        break;
                    }
                    //내보내기
                    case R.id.popuptv_share:{
                        ArrayList<Page> pages = PageMethod.setPageList(new PageDB(getApplicationContext()), book.getId());
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;

                        drawPDF(pages, book, width, height);
                        break;
                    }
                }
                return false;
            }
        });
        popUp.show();

    }


    //PDF 만들기
    public void drawPDF(ArrayList<Page> pages, Book book, int width, int heigth){

        int pageNum = 0;
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo;
        PdfDocument.Page page;
        Canvas canvas;


        if(book.cover != null){
            pageInfo =  new PdfDocument.PageInfo.Builder(width,heigth,pageNum++).create();
            page = doc.startPage(pageInfo);
            canvas = page.getCanvas();
            canvas.drawBitmap(book.cover,0,0,new Paint());
            doc.finishPage(page);
        }

        for(int i = 0; i < pages.size(); i++){
            pageInfo = new PdfDocument.PageInfo.Builder(width,heigth,pageNum++).create();
            page = doc.startPage(pageInfo);
            canvas = page.getCanvas();
            pages.get(i).drawElements(canvas);
            doc.finishPage(page);
        }

        //폴더 생성
        String folderPath = makeFolder(book.getId());
        //파일 생성
        String filePath = folderPath + "/" + book.getId()+".pdf";
        File file = new File(filePath);

        try{
            //있으면 삭제하고 다시만들기
            if(file.exists()){
                file.delete();
            }
            doc.writeTo(new FileOutputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_LONG).show();
        doc.close();
    }


    private String makeFolder(long book_id){
        String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(strSDpath+"/Book");
        myDir.mkdir();
        myDir = new File(strSDpath+"/Book/book"+Long.toString(book_id));
        myDir.mkdir();
        return myDir.getAbsolutePath();
    }

    public void onDeleteFinalAsk(long bookId){
        AlertDialog.Builder builder = new AlertDialog.Builder(BookListActivity.this);
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
                }
                break;

                //읽기
            case Constant.READ_REQUEST :
                break;
            //수정
            case Constant.EDIT_REQUEST:
                break;
        }
        refresh();
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
            case R.id.action_create2:
                Toast.makeText(getApplicationContext(), "책 만들기를 시작합니다", Toast.LENGTH_LONG).show();
                Intent nextIntent = new Intent(getApplicationContext(), MakeCoverActivity.class);
                nextIntent.putExtra("create",1);
                //startActivityForResult(nextIntent, Constant.MAKECOVER_REQUEST);
                startActivity(nextIntent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
}
