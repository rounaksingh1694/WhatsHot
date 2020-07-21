package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    public void goToTweetActivity(boolean isUserLoggedIn) {
        Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
        intent.putExtra("Is User Logged In", isUserLoggedIn);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (ParseUser.getCurrentUser() != null) {
                    Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

}
