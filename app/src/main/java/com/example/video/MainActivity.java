package com.example.video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    AppCompatButton button;



    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
    int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
http://console.firebase.google.com/u/0/cc
        editText = findViewById(R.id.etUserName);
        button = findViewById(R.id.login);

        if (!permissionIsGranted()) {
            askPermission();
        }

        FirebaseApp.initializeApp(this);




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editText.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, callActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }

        });


    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, permission, requestCode);

    }

    private boolean permissionIsGranted() {
        for (String permission : permission) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }
}