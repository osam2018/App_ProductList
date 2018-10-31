package com.example.user.productlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.user.helper.ProductListManager;
import com.example.user.productlist.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AdminActivity extends AppCompatActivity {
    private ProductListAdapter productListAdapter = null;
    private ProductListManager productListManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void showAddProduct(View v) {
        Intent newActivity = new Intent(this, AddProductActivity.class);
        newActivity.putExtra("user", getIntent().getSerializableExtra("user"));
        startActivity(newActivity);
    }

    public void showProductList(View v) {
        Intent intent = new Intent(this, AdminProductListActivity.class);
        startActivity(intent);
    }
}
