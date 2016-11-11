package com.gavincode.playbuttonapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gavincode.playbutton.PlayButton;

public class MainActivity extends AppCompatActivity {

    private PlayButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (PlayButton) findViewById(R.id.play_button);
    }
}
