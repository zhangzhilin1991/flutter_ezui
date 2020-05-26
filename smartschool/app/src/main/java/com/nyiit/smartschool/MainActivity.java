package com.nyiit.smartschool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        jumpToFlutter();
        this.finish();
    }

    public void onClick(View v) {
        Toast.makeText(MainActivity.this, v.getId()+"", Toast.LENGTH_LONG).show();

        jumpToFlutter();
    }

    private void jumpToFlutter() {
        startActivity(
                FlutterActivity
                        .withCachedEngine("my_engine_id").build(this));
        //startActivity(
        //        FlutterActivity.createDefaultIntent(this));
    }
}
