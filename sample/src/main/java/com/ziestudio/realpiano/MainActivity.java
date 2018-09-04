package com.ziestudio.realpiano;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chengtao.pianoview.entity.AutoPlayEntity;
import com.chengtao.pianoview.entity.Piano;
import com.chengtao.pianoview.listener.OnLoadAudioListener;
import com.chengtao.pianoview.listener.OnPianoAutoPlayListener;
import com.chengtao.pianoview.listener.OnPianoListener;
import com.chengtao.pianoview.utils.AutoPlayUtils;
import com.chengtao.pianoview.view.PianoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends Activity
        implements OnPianoListener, OnLoadAudioListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, OnPianoAutoPlayListener {
    private static final boolean USE_CONFIG_FILE = true;
    private PianoView pianoView;
    private SeekBar seekBar,seekBarLoading;
    private Button leftArrow;
    private Button rightArrow;
    private Button btnMusic;
    private int scrollProgress = 0;
    private final static float SEEKBAR_OFFSET_SIZE = -12;
    private boolean isPlay = false;
    private ArrayList<AutoPlayEntity> litterStarList = null;
    private static final long LITTER_STAR_BREAK_SHORT_TIME = 500;
    private static final long LITTER_STAR_BREAK_LONG_TIME = 1000;
    AdView mAdView, adViewBot;
    AdRequest adRequest = new AdRequest.Builder().build();
    private InterstitialAd mInterstitialAd;
    RelativeLayout layMain,layLoading;
    TextView teksWait,teksPersen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view
        mAdView = findViewById(R.id.adView);
        adViewBot = findViewById(R.id.adViewBottom);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        });

        adViewBot.loadAd(adRequest);
        adViewBot.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }
        });

        pianoView = findViewById(R.id.pv);
        seekBar = findViewById(R.id.sb);
        seekBar.setThumbOffset((int) convertDpToPixel(SEEKBAR_OFFSET_SIZE));
        leftArrow = findViewById(R.id.iv_left_arrow);
        rightArrow = findViewById(R.id.iv_right_arrow);
        if (Build.VERSION.SDK_INT >= 21) {
            leftArrow.setBackground(getDrawable(R.drawable.ic_left_arrow));
            rightArrow.setBackground(getDrawable(R.drawable.ic_right_arrow));
        }
        btnMusic = findViewById(R.id.iv_music);
        layMain = findViewById(R.id.activity_main);
        layLoading = findViewById(R.id.layLoading);

        teksWait = findViewById(R.id.teksPleaseWait);
        teksPersen = findViewById(R.id.teksPersen);
        seekBarLoading = findViewById(R.id.seekbarLoading);
        pianoView.setOnClickListener(view -> {
            if (isPlay) {
                pianoView.releaseAutoPlay();
                isPlay = false;
                finish();
            }
        });
        //listener
        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setLoadAudioListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        rightArrow.setOnClickListener(this);
        leftArrow.setOnClickListener(this);
        btnMusic.setOnClickListener(this);
        //init
        if (USE_CONFIG_FILE) {
            AssetManager assetManager = getAssets();
            try {
                litterStarList = AutoPlayUtils.getAutoPlayEntityListByCustomConfigInputStream(
                        assetManager.open("flight_of_the_bumble_bee"));
            } catch (IOException e) {
                Log.e("TAG", e.getMessage());
            }
        } else {
            initLitterStarList();
        }

        if (PilihPiano.tema == 1) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (PilihPiano.tema == 2) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        } else if (PilihPiano.tema == 3) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorOrange));
        } else if (PilihPiano.tema == 4) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        } else if (PilihPiano.tema == 5) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorCyan));
        } else if (PilihPiano.tema == 6) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorIndigo));
        } else if (PilihPiano.tema == 7) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorPink));
        } else if (PilihPiano.tema == 8) {
            layMain.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public void onPause() {
        // This method should be called in the parent Activity's onPause() method.
        if (mAdView != null) {
            mAdView.pause();
        }

        if (adViewBot != null) {
            adViewBot.pause();
        }
        super.onPause();
    }

    private void initLitterStarList() {
        litterStarList = new ArrayList<>();
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 5, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 5, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_LONG_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_LONG_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_LONG_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_LONG_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 5, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 5, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 4, LITTER_STAR_BREAK_LONG_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 3, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 2, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 1, LITTER_STAR_BREAK_SHORT_TIME));
        litterStarList.add(
                new AutoPlayEntity(Piano.PianoKeyType.WHITE, 4, 0, LITTER_STAR_BREAK_LONG_TIME));
    }

    @Override
    public void onPianoInitFinish() {

    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group,
                             int positionOfGroup) {
        Log.e("TAG", "Type:" + type + "---Voice:" + voice);
        Log.e("TAG", "Group:" + group + "---" + "Position:" + positionOfGroup);
    }

    @Override
    public void loadPianoAudioStart() {

    }

    @Override
    public void loadPianoAudioFinish() {
        layLoading.setVisibility(View.GONE);
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/7228451046");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void loadPianoAudioError(Exception e) {
        layLoading.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "loadPianoMusicError", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void loadPianoAudioProgress(int progress) {
        int prog = progress;
        String progres = String.valueOf(prog) + " %";
        layLoading.setVisibility(View.VISIBLE);
        seekBarLoading.setProgress(prog);
        teksPersen.setText(progres);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        pianoView.scroll(i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (adViewBot != null) {
            adViewBot.resume();
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onClick(View view) {
        if (scrollProgress == 0) {
            try {
                scrollProgress = (pianoView.getLayoutWidth() * 100) / pianoView.getPianoWidth();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int progress;
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                if (scrollProgress == 0) {
                    progress = 0;
                } else {
                    progress = seekBar.getProgress() - scrollProgress;
                    if (progress < 0) {
                        progress = 0;
                    }
                }
                seekBar.setProgress(progress);
                break;
            case R.id.iv_right_arrow:
                if (scrollProgress == 0) {
                    progress = 100;
                } else {
                    progress = seekBar.getProgress() + scrollProgress;
                    if (progress > 100) {
                        progress = 100;
                    }
                }
                seekBar.setProgress(progress);
                break;
            case R.id.iv_music:
                if (!isPlay) {
                    pianoView.autoPlay(litterStarList);
                    isPlay = true;
                } else {
                    pianoView.releaseAutoPlay();
                    isPlay = false;
                }
                break;
        }
    }

    /**
     * Dp to px
     *
     * @param dp dp值
     * @return px 值
     */
    private float convertDpToPixel(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5730449577374867/7228451046");
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        finish();
    }

    @Override
    public void onPianoAutoPlayStart() {
        Toast.makeText(this, "Start Auto Play Piano", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPianoAutoPlayEnd() {
        isPlay = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pianoView != null) {
            pianoView.releaseAutoPlay();
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adViewBot != null) {
            adViewBot.destroy();
        }
    }
}