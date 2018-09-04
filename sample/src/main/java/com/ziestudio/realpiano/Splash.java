package com.ziestudio.realpiano;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Splash extends AppCompatActivity {
    RelativeLayout rlCahTegal;
    ImageView imgLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        imgLogo = findViewById(R.id.logo_piano_pro);
        rlCahTegal = findViewById(R.id.rlCahtegal);
        imgLogo.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.blink);
        rlCahTegal.setAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlCahTegal.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLogo();
                    }
                },200);
            }

        }, 3000);
    }

    private void showLogo() {
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.blink);
        imgLogo.setAnimation(animation);
        imgLogo.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(Splash.this,First.class));
            }
        },3000);
    }
}
