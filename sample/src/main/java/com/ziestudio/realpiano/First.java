package com.ziestudio.realpiano;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class First extends AppCompatActivity {

    ImageView imgPlay,imgShare,imgRateUs,imgToolTip, imgColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        declaration();
        action();
        startTootipAnimation();
    }

    private void declaration() {
        imgToolTip = findViewById(R.id.imgTooltipPlay);
        imgShare = findViewById(R.id.imgShare);
        imgPlay = findViewById(R.id.imgPlay);
        imgRateUs = findViewById(R.id.imgRateUs);
        imgColor = findViewById(R.id.imgWarna);
    }

    private void action() {
        imgPlay.setOnClickListener(view -> {
            new Handler().postDelayed(() -> startActivity(new Intent(First.this,MainActivity.class)),100);
        });

        imgShare.setOnClickListener(view -> {
            Intent sendIntent;
            sendIntent = new Intent(android.content.Intent.ACTION_SEND);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Play this Piano for fun\n\nhttps://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share with"));
        });

        imgRateUs.setOnClickListener(view -> {
            String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        imgColor.setOnClickListener(view -> {
            startActivity(new Intent(First.this,PilihPiano.class));
        });

    }

    private void startTootipAnimation() {
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgToolTip, "scaleY", 0.8f);
        scaleY.setDuration(200);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(imgToolTip, "scaleY", 1.4f);
        scaleYBack.setDuration(500);
        scaleYBack.setInterpolator(new BounceInterpolator());
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(1000);
        animatorSet.playSequentially(scaleY, scaleYBack);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.setStartDelay(2000);
                animatorSet.start();
            }
        });
        imgToolTip.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        animatorSet.start();
    }
}
