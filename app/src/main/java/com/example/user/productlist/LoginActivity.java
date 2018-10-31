package com.example.user.productlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.helper.PostTask;
import com.example.user.helper.listener.OnGetAdminInfoListener;
import com.example.user.helper.listener.OnTokenCheckListener;
import com.example.user.model.User;
import com.google.firebase.FirebaseApp;
import com.example.user.helper.ProductListManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();

        signOut();
    }

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_NOTIFICATION_CHECK = 3333;
    private static final int RC_SIGN_OUT = 666;

    private ProductListManager productListManager = null;

    private Button btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set member variables about ui.
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        // Initialize member objects
        FirebaseApp.initializeApp(this);
        productListManager = new ProductListManager();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        try
        {
            signOut();
        } catch(Exception e) {}
        // [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
        else if (requestCode == RC_NOTIFICATION_CHECK) {
            //Toast.makeText(LoginActivity.this, Integer.toString(resultCode), Toast.LENGTH_LONG).show();


            switch(resultCode)
            {
                case 1:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Delete Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 2:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Sold Out Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 3:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Sold Out & Delete Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkAdd");
                    break;
                case 4:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    break;
                case 5:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Delete Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkSoldOut");
                    break;
                case 6:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Sold Out Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("checkDelete");
                    break;
                case 7:
                    FirebaseMessaging.getInstance().subscribeToTopic("checkSoldOut");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkAdd");
                    FirebaseMessaging.getInstance().subscribeToTopic("checkDelete");
                    new PostTask().execute("Notification Setup Is Complete!", "You Added Add & Sold Out & Delete Notification!", "/topics/" + mAuth.getCurrentUser().getUid());
                    break;
                default:
                    break;
            }

            productListManager.getAdminInfo(new OnGetAdminInfoListener() {
                @Override
                public void onSucceed(User user) {
                    // check the password
                    Intent intent = null;

                    if (user.getAdmin().equals(mAuth.getCurrentUser().getUid())) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, UserActivity.class);
                    }

                    if (intent != null) {
                        intent.putExtra("UID", mAuth.getCurrentUser().getUid());
                        intent.putExtra("notificationTypeAdd", resultCode >= 4 ? true : false);
                        intent.putExtra("notificationTypeSoldOut", resultCode == 2 || resultCode == 3 || resultCode == 6 || resultCode == 7 ? true : false);
                        intent.putExtra("notificationTypeDelete", resultCode == 1 || resultCode == 3 || resultCode == 5 || resultCode == 7 ? true : false);
                        startActivityForResult(intent, RC_SIGN_OUT);
                    }
                    btnGoogleLogin.setEnabled(true);
                    signOut();
                }

                @Override
                public void onFailed() {
                    btnGoogleLogin.setEnabled(true);
                }
            });
        }
        else if(requestCode == RC_SIGN_OUT)
        {
            signOut();
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

                            FirebaseMessaging.getInstance().subscribeToTopic("all");
                            FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());

                            //Toast.makeText(LoginActivity.this, "", Toast.LENGTH_LONG).show();

                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(LoginActivity.this, task.getResult().getToken(), Toast.LENGTH_LONG).show();
                                        final String idToken = task.getResult().getToken();
                                        // Send token to your backend via HTTPS

                                        productListManager.checkToken(mAuth.getCurrentUser().getUid(), idToken, new OnTokenCheckListener() {
                                            @Override
                                            public void exist() {
                                                productListManager.addToken(mAuth.getCurrentUser().getUid(), idToken);

                                                afterWork();
                                            }

                                            @Override
                                            public void notExist() {
                                                productListManager.getAdminInfo(new OnGetAdminInfoListener() {
                                                    @Override
                                                    public void onSucceed(User user) {
                                                        // check the password
                                                        Intent intent = null;

                                                        if (user.getAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                            if (intent != null) {
                                                                intent.putExtra("user",mAuth.getCurrentUser());
                                                                startActivity(intent);

                                                                btnGoogleLogin.setEnabled(true);
                                                                signOut();
                                                            }
                                                        } else {
                                                            intent = new Intent(LoginActivity.this, NotificationAgreeActivity.class);
                                                            startActivityForResult(intent, RC_NOTIFICATION_CHECK);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        btnGoogleLogin.setEnabled(true);
                                                    }
                                                });
                                                productListManager.addToken(mAuth.getCurrentUser().getUid(), idToken);
                                            }

                                            @Override
                                            public void afterWork() {
                                                productListManager.getAdminInfo(new OnGetAdminInfoListener() {
                                                    @Override
                                                    public void onSucceed(User user) {
                                                        // check the password
                                                        Intent intent = null;

                                                        if (user.getAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                                                        } else {
                                                            intent = new Intent(LoginActivity.this, UserActivity.class);
                                                        }

                                                        if (intent != null) {
                                                            intent.putExtra("user",mAuth.getCurrentUser());
                                                            startActivity(intent);
                                                        }
                                                        btnGoogleLogin.setEnabled(true);
                                                        signOut();
                                                    }

                                                    @Override
                                                    public void onFailed() {
                                                        btnGoogleLogin.setEnabled(true);
                                                    }
                                                });
                                            }
                                        });

                                        new PostTask().execute("Hi", mAuth.getCurrentUser().getEmail() + " SignIn!", "/topics/" + mAuth.getCurrentUser().getUid());
                                        //new PostTask().execute("Hi", mAuth.getCurrentUser().getEmail() + " SignIn!", "/topics/all");
                                    } else {
                                        // Handle error -> task.getException();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Data Incorrect!", Toast.LENGTH_LONG).show();
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    public void checkLogin(View v) {
        signIn();


    }

    public void testClicik(View v) {



    }

    public void onClickFW(View v)
    {
        Toast.makeText(LoginActivity.this, "Will Be Updated", Toast.LENGTH_LONG).show();
    }
}
