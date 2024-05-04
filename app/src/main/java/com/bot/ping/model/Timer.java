package com.bot.ping.model;

import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.bot.ping.R;

public class Timer {
    public static void start(TextView textView, int seconds){
        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long l) {
                int newSeconds = (int) ((l/1000)%60);
                int minutes = (int) ((l/1000)/60);
                textView.setText(minutes +":"+ newSeconds);
            }

            public void onFinish() {
                textView.setText(R.string.time_is_over);
            }
        }.start();
    }
}
