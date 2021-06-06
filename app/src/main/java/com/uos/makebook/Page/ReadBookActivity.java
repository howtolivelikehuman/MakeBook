package com.uos.makebook.Page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private String makeFolder(){ // 폴더 없으면 생성하고 path return
        String strSDpath = Environment.getExternalStorageDirectory().getAbsolutePath();
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
            Toast.makeText(this, "파일 이미 있음", Toast.LENGTH_SHORT).show();
        }
        return recordFile.getAbsolutePath();
    }

    private void startRecord(){
        // 녹음 시작
        record.setIcon(ContextCompat.getDrawable(this, R.drawable.stop));

        String filepath = makeRecordFile();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord(){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayRecord(){
        if (player != null && isPlaying) {
            player.stop();

            Toast.makeText(this, "재생 중지", Toast.LENGTH_SHORT).show();
            playThis.setIcon(ContextCompat.getDrawable(this, R.drawable.play));
            isPlaying = false;
        }
    }

    private void playAll() {
        Handler mHandler = new Handler();
        Toast.makeText(getApplicationContext(),"전체 재생", Toast.LENGTH_LONG).show();
        for (int i = 0; i < page_idx; i++) {
            flipper.showPrevious();
        }
        page_idx = 0;
        updateButtonState();
        isPlaying = true;
        playRecord(0);
        for (int i = 0; i < pageList.size()-1; i++) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flipper.showNext();
                    page_idx++;
                    updateButtonState();
                    playRecord(page_idx);
                }
            }, 3000*(i+1)); // 3초 간격으로 페이지 실행
        }
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
            case R.id.action_read_done :
                finish();
                return true;

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
}