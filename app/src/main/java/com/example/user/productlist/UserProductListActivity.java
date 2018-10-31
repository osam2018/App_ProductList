package com.example.user.productlist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.helper.PostTask;
import com.example.user.helper.ProductListManager;
import com.example.user.helper.listener.OnDownloadImageListener;
import com.example.user.helper.listener.OnGetProductsListener;
import com.example.user.model.Product;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.util.ArrayList;

public class UserProductListActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    private String UID;

    Boolean notificationTypeAdd;
    Boolean notificationTypeSoldOut;
    Boolean notificationTypeDelete;

    private ArrayList<ProductItem> dataItems;
    private ListView lvProduct = null;
    private ProductListAdapter productListAdapter = null;
    private ProductListManager productListManager = null;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //FirebaseMessaging.getInstance().subscribeToTopic("all");
                            //new PostTask().execute("Hi", mAuth.getCurrentUser().getEmail() + " SignIn!", "/topics/all");
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(UserProductListActivity.this, "Login Data Incorrect!", Toast.LENGTH_LONG).show();
                            try {
                                signOut();
                            }catch(Exception e) {}
                        }
                    }
                });
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_list);

        notificationTypeAdd = getIntent().getBooleanExtra("notificationTypeAdd", false);
        notificationTypeSoldOut = getIntent().getBooleanExtra("notificationTypeSoldOut", false);
        notificationTypeDelete = getIntent().getBooleanExtra("notificationTypeDelete", false);
        UID = getIntent().getStringExtra("UID");

        productListManager = new ProductListManager();

        lvProduct = findViewById(R.id.lvProduct);
        dataItems = new ArrayList<>();


        productListAdapter = new ProductListAdapter(dataItems, getApplicationContext(), UID, notificationTypeAdd, notificationTypeSoldOut, notificationTypeDelete);
        lvProduct.setAdapter(productListAdapter);

        /*
        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserProductListActivity.this, "Item Clicked!", Toast.LENGTH_LONG).show();
                ((Button)view.findViewById(R.id.btnWatch)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(UserProductListActivity.this, "ItemButtonClicked!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });*/

        productListManager.getProducts(new OnGetProductsListener() {
            @Override
            public void onSucceed(final ArrayList<Product> products) {


                for (Product p : products
                     ) {
                    ProductItem productItem = new ProductItem();
                    productItem.setName(p.getName());
                    productItem.setExist(p.getExist());
                    productItem.setExplanation(p.getExplanation());
                    productItem.setImage(null);
                    productItem.setFavorate(false);

                    dataItems.add(productItem);

                    productListManager.downloadImage(p, p.getImage(), new OnDownloadImageListener() {
                        @Override
                        public void onSucceed(Product p, Uri uri) {
                            for (ProductItem pi : dataItems
                                 ) {
                                if(pi.getName().equals(p.getName())) {
                                    pi.setImage(uri);
                                    productListAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                }
                for (int i = 0; i < products.size(); i++) {






                    //////////////////////////////////////////////////////////////////////////////
                }
            }

            @Override
            public void onFailed() {

            }
        });


    }


}