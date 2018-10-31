package com.example.user.productlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.user.helper.PostTask;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserActivity extends AppCompatActivity {
    private static final int RC_NOTIFICATION_CHECK = 3333;

    Boolean notificationTypeAdd;
    Boolean notificationTypeSoldOut;
    Boolean notificationTypeDelete;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_NOTIFICATION_CHECK) {
            switch(resultCode)
            {
                case 1:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Delete Notification!", "/topics/checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 2:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Sold Out Notification!", "/topics/checkSoldOut");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 3:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Sold Out & Delete Notification!", "/topics/checkSoldOut");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 4:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add Notification!", "/topics/checkAdd");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    break;
                case 5:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Delete Notification!", "/topics/checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    break;
                case 6:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Sold Out Notification!", "/topics/checkAdd");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    break;
                case 7:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Sold Out & Delete Notification!", "/topics/checkDelete");
                    break;
                default:
                    break;
            }

            int rc = resultCode;
            if (rc >= 4) {
                this.notificationTypeAdd = true;
                rc -= 4;
            } else { this.notificationTypeAdd = false; }
            if (rc >= 2) {
                this.notificationTypeSoldOut = true;
                rc -= 2;
            } else { this.notificationTypeSoldOut = false; }
            if (rc >= 1) {
                this.notificationTypeDelete = true;
                rc -= 1;
            } else { this.notificationTypeDelete = false; }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        this.notificationTypeAdd = getIntent().getBooleanExtra("notificationTypeAdd", false);
        this.notificationTypeSoldOut = getIntent().getBooleanExtra("notificationTypeSoldOut", false);
        this.notificationTypeDelete = getIntent().getBooleanExtra("notificationTypeDelete", false);
    }

    public void onClickMyList(View v)
    {
        Intent intent = new Intent(this, UserProductListActivity.class);
        intent.putExtra("notificationTypeAdd", notificationTypeAdd);
        intent.putExtra("notificationTypeSoldOut", notificationTypeSoldOut);
        intent.putExtra("notificationTypeDelete", notificationTypeDelete);
        intent.putExtra("UID", getIntent().getStringExtra("UID"));
        startActivity(intent);
    }

    public void changeOption(View v)
    {
        startActivityForResult(new Intent(UserActivity.this, NotificationAgreeActivity.class), RC_NOTIFICATION_CHECK);
    }
}
