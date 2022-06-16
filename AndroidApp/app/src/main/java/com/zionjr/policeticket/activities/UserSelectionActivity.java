package com.zionjr.policeticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zionjr.policeticket.R;

public class UserSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
    }

    public void onDriverSelected(View view) {

        startActivity(new Intent(UserSelectionActivity.this,
                com.zionjr.policeticket.section_driver.activities.LoginActivity.class));
    }

    public void onPolicemanSelected(View view) {

        startActivity(new Intent(UserSelectionActivity.this,
                com.zionjr.policeticket.section_policeman.activities.LoginActivity.class));
    }
}