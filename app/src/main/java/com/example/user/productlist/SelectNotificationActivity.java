package com.example.user.productlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;

public class SelectNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_notification);
    }

    public void onClickOK(View v) {
        int resultCode = 0;
        resultCode += ((CheckBox)findViewById(R.id.checkAdd)).isChecked() ? 4 : 0;
        resultCode += ((CheckBox)findViewById(R.id.checkSoldOut)).isChecked() ? 2 : 0;
        resultCode += ((CheckBox)findViewById(R.id.checkDelete)).isChecked() ? 1 : 0;

        Intent intent = new Intent();
        setResult(resultCode, intent);
        finish();
    }
}
