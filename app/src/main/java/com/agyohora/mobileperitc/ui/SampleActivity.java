package com.agyohora.mobileperitc.ui;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_profile_menu, menu);*/
        return super.onCreateOptionsMenu(menu);
    }
}