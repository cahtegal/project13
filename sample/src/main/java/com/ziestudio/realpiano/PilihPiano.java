package com.ziestudio.realpiano;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PilihPiano extends AppCompatActivity {

    ImageView theme1,theme2,theme3,theme4,theme5,theme6,theme7,theme8;
    public static int tema = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pilih_piano);
        declaration();
        action();
    }

    private void declaration() {
        theme1 = findViewById(R.id.theme1);
        theme2 = findViewById(R.id.theme2);
        theme3 = findViewById(R.id.theme3);
        theme4 = findViewById(R.id.theme4);
        theme5 = findViewById(R.id.theme5);
        theme6 = findViewById(R.id.theme6);
        theme7 = findViewById(R.id.theme7);
        theme8 = findViewById(R.id.theme8);
    }

    private void action() {
        theme1.setOnClickListener(view -> {
            tema = 1;
            finish();
        });
        theme2.setOnClickListener(view -> {
            tema = 2;
            finish();
        });
        theme3.setOnClickListener(view -> {
            tema = 3;
            finish();
        });
        theme4.setOnClickListener(view -> {
            tema = 4;
            finish();
        });
        theme5.setOnClickListener(view -> {
            tema = 5;
            finish();
        });
        theme6.setOnClickListener(view -> {
            tema = 6;
            finish();
        });
        theme7.setOnClickListener(view -> {
            tema = 7;
            finish();
        });
        theme8.setOnClickListener(view -> {
            tema = 8;
            finish();
        });
    }
}
