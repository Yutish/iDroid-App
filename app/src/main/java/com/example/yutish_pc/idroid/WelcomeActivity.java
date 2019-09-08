package com.example.yutish_pc.idroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread() {
            @Override
            public void run() {                                //used to pause
                try {
                    sleep(2500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);        // moved to main activity
                    startActivity(mainIntent);
                }
            }
        };
        thread.start();
    }

    protected void onPause() {
        super.onPause();
        finish();
    }
}
