package com.cookie_clickers;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvPoints;
    private int points = 0;
    private int cps = 100;
    private CookieCounter cookieCounter = new CookieCounter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPoints = findViewById(R.id.tvpoints);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgCookie) {
            Animation a = AnimationUtils.loadAnimation(this, R.anim.cookie_animation);
            a.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    coockieClick();
                }
            });
            v.startAnimation(a);
        }
    }

    private void coockieClick() {
        points++;
        tvPoints.setText(Integer.toString(points));
        showToast(R.string.clicked);
        System.out.println(points);
    }

    private void showToast(int stringID) {
        Toast toast = new Toast(this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView textView = new TextView(this);
        textView.setText(stringID);
        textView.setTextSize(40f);
        textView.setTextColor(Color.BLACK);
        toast.setView(textView);
        toast.show();
    }


    private void update() {
        points += cps / 100;
        tvPoints.setText(Integer.toString(points));
        showToast(R.string.clicked);
    }


    public class CookieCounter {

        private Timer timer;

        public void CookieCounter() {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            }, 1000, 10);
        }
    }
}


