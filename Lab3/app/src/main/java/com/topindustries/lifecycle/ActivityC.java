package com.topindustries.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.topindustries.helloandroid.R;

public class ActivityC extends AppCompatActivity {

    private static final String TAG = "Activity C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        setTitle("C");
        Log.d(ActivityC.TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(ActivityC.TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ActivityC.TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ActivityC.TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(ActivityC.TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(ActivityC.TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(ActivityC.TAG, "onDestroy");
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.buttonA3:
                startActivity(new Intent(this, ActivityA.class));
                break;
            case R.id.buttonB3:
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.buttonC3:
                startActivity(new Intent(this, ActivityC.class));
                break;
        }
    }
}