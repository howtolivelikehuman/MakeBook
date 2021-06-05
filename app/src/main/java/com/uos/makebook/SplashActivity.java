package com.uos.makebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.pedro.library.AutoPermissions;
import com.uos.makebook.MainList.BookListActivity;

public class SplashActivity extends AppCompatActivity {
    ImageView logo, icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //안드로이드 권한 질의
        AutoPermissions.Companion.loadAllPermissions(this,101);
        permissionCheck();

        setContentView(R.layout.main_splash);
        logo = findViewById(R.id.imageLogo);
        icon = findViewById(R.id.imageIcon);

        StartAnimations();

    }

    public void permissionCheck(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void StartAnimations(){
        icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.image_bounce));
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_bounce);
        logo.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), BookListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.page_slideout, R.anim.page_slidein);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }
}