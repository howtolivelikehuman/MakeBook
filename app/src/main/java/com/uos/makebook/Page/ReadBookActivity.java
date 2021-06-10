package com.uos.makebook.Page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uos.makebook.Common.Constant;
import com.uos.makebook.MainList.BookListActivity;
import com.uos.makebook.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReadBookActivity extends PageActivity {
    String folderPath;

    boolean isRecording = false;
    boolean isPlaying = false;

    MenuItem record, playThis;
    MediaRecorder recorder;
    MediaPlayer player;

    String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File myDir = new File(strSDpath+"/Book");
        if(!myDir.exists())
            myDir.mkdir();
        folderPath = makeFolder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // read menu 연결
        this.menu = menu;
        getMenuInflater().inflate(R.menu.readbook_menu, menu);
        record = menu.findItem(R.id.record);
        playThis = menu.findItem(R.id.playThis);
        record.setEnabled(true);
        playThis.setEnabled(true);
        return true;
    }

    @Override
    public void gotoNext(){
        if(isRecording){ // 페이지가 넘어가면 자동 녹음 중지
            stopRecord();
        }
        if(isPlaying){ // 페이지가 넘어가면 자동 재생 중지
            stopPlayRecord();
        }
        super.gotoNext();
    }

    @Override
    public void gotoPrev(){
        if(isRecording){ // 페이지가 넘어가면 자동 녹음 중지
            stopRecord();
        }
        if(isPlaying){ // 페이지가 넘어가면 자동 재생 중지
            stopPlayRecord();
        }
        super.gotoPrev();
    }

    @Override
    public void complete(){
        super.complete();
        finish();
    }

    private String makeFolder(){ // 폴더 없으면 생성하고 path return
        File myDir = new File(strSDpath+"/Book/book"+Long.toString(book_id));
        myDir.mkdir(); // todo : sdk 버전 30에서 권한 오류남
        return myDir.getAbsolutePath();
    }

    private String getPagePath(){ // 해당 page 녹음 파일의 path return
        String filename = "page" + Long.toString(pageList.get(page_idx).id) + ".mp4";
        String filePath = folderPath + "/" + filename;
        return filePath;
    }

    private String getPagePath(int idx){ // 특정 idx page 녹음 파일의 path return
        String filename = "page" + Long.toString(pageList.get(idx).id) + ".mp4";
        String filePath = folderPath + "/" + filename;
        return filePath;
    }

    private String makeRecordFile(){ // 파일 없으면 생성하고 path return
        String filePath = getPagePath();
        File recordFile = new File(filePath);
        try {
            recordFile.createNewFile(); // todo : sdk 버전 30에서 권한 오류남
            Toast.makeText(this, "파일 생성 성공", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recordFile.getAbsolutePath();
    }

    private void startRecord(){
        if(isPlaying){
            // 재생 중이면 녹음 x
            return;
        }
        // 녹음 시작
        String filepath = makeRecordFile();
        System.out.println(filepath);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 어디에서 음성 데이터를 받을 것인지
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 압축 형식 설정
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        recorder.setOutputFile(filepath);

        try {
            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "녹음 시작", Toast.LENGTH_SHORT).show();
            isRecording = true;
            record.setIcon(ContextCompat.getDrawable(this, R.drawable.stop));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord(){
        if(isPlaying){
            // 재생 중이면 녹음 중지 x
            return;
        }
        // 녹음 끝
        record.setIcon(ContextCompat.getDrawable(this, R.drawable.mic));
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Toast.makeText(this, "녹음 중지", Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }

    private void playRecord(int idx){
        if(isRecording){
            // 녹음 중이면 재생 x
            return;
        }
        // 녹음 재생
        if(idx == -1){
            idx = page_idx;
        }
        String filepath = getPagePath(idx);
        File file = new File(filepath);
        System.out.println(filepath);
        if(!file.exists()){
            Toast.makeText(this, "녹음 파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (player != null) {
                player.release();
                player = null;
            }

            player = new MediaPlayer();
            player.setDataSource(filepath);
            player.prepare();
            player.start();

            Toast.makeText(this, "재생 시작", Toast.LENGTH_SHORT).show();
            playThis.setIcon(ContextCompat.getDrawable(this, R.drawable.stop));
            isPlaying = true;

            /* 별도 thread로 play 중인지 확인 */
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    while(player.isPlaying()){}
                    Message msg = handler.obtainMessage(); // main thread의 gui를 이용하기 위해 필요
                    handler.sendMessage(msg);
                }
            };
            Thread thread = new Thread(myRunnable);
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            stopPlayRecord();
        }
    };

    private void stopPlayRecord(){
        if(isRecording){
            // 녹음 중이면 재생 중지 x
            return;
        }
        if (isPlaying) {
            player.stop();

            Toast.makeText(this, "재생 중지", Toast.LENGTH_SHORT).show();
            playThis.setIcon(ContextCompat.getDrawable(this, R.drawable.play));
            isPlaying = false;
        }
    }

    private void playAllModule(){
        if(page_idx == pageList.size()-1)
            return;
        if(!isPlaying)
            playRecord(page_idx); // 음성 파일 실행

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isPlaying) { // 4초 후 음성 파일 계속 실행 중이면 재귀 실행
                    playAllModule();
                    return;
                }
                flipper.showNext();
                page_idx++;
                updateButtonState();
                playAllModule();
            }
        }, 4000); // 4초 간격으로 페이지 실행
    }

    private void playAll() {
        Toast.makeText(getApplicationContext(),"전체 재생", Toast.LENGTH_LONG).show();
        for (int i = 0; i < page_idx; i++) {
            flipper.showPrevious();
        }
        page_idx = 0;
        playAllModule();
        isPlaying = false;
        playThis.setIcon(ContextCompat.getDrawable(this, R.drawable.play));
    }

    private void deleteThis(){ // 파일 존재하는 지 판단 => 삭제
        String filepath = getPagePath();
        File file = new File(filepath);
        try{
            if(file.exists()){
                file.delete();
                Toast.makeText(this, "녹음 삭제", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "녹음 삭제 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAll(){
        // 모든 페이지 녹음 파일 삭제
        for(int i=0; i<pageList.size(); i++){
            String filepath = getPagePath(i);
            File file = new File(filepath);
            try{
                if(file.exists()){
                    file.delete();
                    Toast.makeText(this, "녹음 삭제", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this, "녹음 삭제 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.record :
                // 녹음 시작 / 정지
                if(isRecording) {
                    stopRecord();
                }else {
                    startRecord();
                }
                return true;
            case R.id.playThis :
                // 해당 페이지 재생 / 정지
                if(isPlaying) {
                    stopPlayRecord();
                }else{
                    playRecord(-1);
                }
                return true;

            case R.id.action_view_pagelist :
                // 페이지 모아보기
                Intent tmpIntent = new Intent(getApplicationContext(), ViewPageListActivity.class);
                book.setCover(null);
                tmpIntent.putExtra("book",book);
                tmpIntent.putExtra("mode", "READ_MODE");
                startActivity(tmpIntent);
                finish();
                return true;
            case R.id.playAll :
                // 전체 페이지 재생
                playAll();
                return true;
            case R.id.deleteThis :
                // 해당 페이지 녹음 삭제
                deleteThis();
                return true;
            case R.id.deleteAll :
                // 전체 페이지 녹음 삭제
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected PageCanvas generatePageCanvas(Page page) {
        return new PageCanvas(this, page, false);
    }
}