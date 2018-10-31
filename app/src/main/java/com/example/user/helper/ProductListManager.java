package com.example.user.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Button;

import com.example.user.helper.listener.OnAddTokenListener;
import com.example.user.helper.listener.OnCheckFavorateListener;
import com.example.user.helper.listener.OnDownloadImageListener;
import com.example.user.helper.listener.OnGetProductsListener;
import com.example.user.helper.listener.OnTokenCheckListener;
import com.example.user.model.Favorate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.user.helper.listener.OnAddProductListener;
import com.example.user.helper.listener.OnGetAdminInfoListener;
import com.example.user.model.Product;
import com.example.user.model.User;

import java.io.File;
import java.util.ArrayList;


public class ProductListManager {
    // objects
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    // constants
    private final String USER_COLLECTION_NAME = "User";
    private final String PRODUCT_COLLECTION_NAME = "Product";
    private final String TOKEN_COLLECTION_NAME = "Token";

    public ProductListManager() {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }



    public void addProduct(final String name, final String explanation, final Uri image, final FirebaseUser user, final OnAddProductListener onAddProductListener) {
        DatabaseReference ref = database.getReference()
                .child(PRODUCT_COLLECTION_NAME)
                .child(name);

        // Check to exist a product.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onAddProductListener.onExist();
                } else {
                    StorageReference storageRef = storage.getReference();

                    // Set path. it doesn't process that about file extension.
                    final String path = String.format("images/%s", name);

                    // Upload a image file.
                    storageRef.child(path).putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When finish to upload, insert a product.
                            DatabaseReference ref = database.getReference()
                                    .child(PRODUCT_COLLECTION_NAME)
                                    .child(name);

                            ref.child("explanation").setValue(explanation);
                            ref.child("image").setValue(path);
                            ref.child("exist").setValue(Product.EXIST_DEFAULT_VALUE);

                            onAddProductListener.onUpload();
                        }
                    });

                    // Doesn't process for fail.
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkToken(final String name, final String token, final OnTokenCheckListener onTokenCheckListener)
    {
        DatabaseReference ref = database.getReference()
                .child(PRODUCT_COLLECTION_NAME)
                .child(name);

        // Check to exist a product.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    onTokenCheckListener.exist();
                } else {
                    onTokenCheckListener.notExist();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addToken(final String name, final String token) {
        DatabaseReference ref = database.getReference()
                .child(TOKEN_COLLECTION_NAME)
                .child(name)
                .child("Token");

        ref.setValue(token);
    }

    public void getAdminInfo(final OnGetAdminInfoListener onGetAdminInfoListener) {
        DatabaseReference ref = database.getReference()
                .child(USER_COLLECTION_NAME);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    onGetAdminInfoListener.onSucceed(user);
                } else {
                    onGetAdminInfoListener.onFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onGetAdminInfoListener.onFailed();
            }
        });
    }

    public void manageToken()
    {

    }

    public void getProducts(final OnGetProductsListener onGetProductsListener) {
        DatabaseReference ref = database.getReference();
        ref = ref.child(PRODUCT_COLLECTION_NAME);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Product> products = new ArrayList<Product>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    product.setName(ds.getKey());
                    products.add(product);
                }

                onGetProductsListener.onSucceed(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onGetProductsListener.onFailed();
            }
        };

        ref.addListenerForSingleValueEvent(eventListener);
    }

    public void downloadImage(final Product p, final String path, final OnDownloadImageListener onDownloadImageListener) {
        StorageReference ref = storage.getReference().child(path);

        final File tmpFile;

        try {
            tmpFile = File.createTempFile("images", ".png");

            ref.getFile(tmpFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Uri uri = Uri.fromFile(tmpFile);

                    onDownloadImageListener.onSucceed(p, uri);
                }
            });
        } catch (Exception e) {
            onDownloadImageListener.onFailed();
        }
    }

    public void checkFavorate(final String UID, final Button button, final OnCheckFavorateListener onCheckFavorateListener) {
        DatabaseReference ref = database.getReference();
        ref = ref.child("UID")
        .child(UID)
        .child(button.getContentDescription().toString());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Favorate favorate = dataSnapshot.getValue(Favorate.class);

                if (dataSnapshot.exists()) {
                    onCheckFavorateListener.exist(button, favorate);
                } else {
                    onCheckFavorateListener.notExist(button);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addFavorate(String UID, String productName, Boolean notificationTypeAdd, Boolean notificationTypeSoldOut, Boolean notificationTypeDelete)
    {
        DatabaseReference ref = database.getReference()
                .child("UID")
                .child(UID)
                .child(productName);

        if(notificationTypeAdd) {
            ref.child("Add").setValue("true");
        }
        else {
            ref.child("Add").setValue("false");
        }
        if(notificationTypeSoldOut) {
            ref.child("SoldOut").setValue("true");
        }
        else {
            ref.child("SoldOut").setValue("false");
        }
        if(notificationTypeDelete) {
            ref.child("Delete").setValue("true");
        }
        else {
            ref.child("Delete").setValue("false");
        }
    }

    public void removeFavorate(String UID, String productName)
    {
        DatabaseReference ref = database.getReference()
                .child("UID")
                .child(UID)
                .child(productName);

        ref.removeValue();
    }

    public void AddProduct(String productName)
    {
        DatabaseReference ref = database.getReference()
                .child("Product")
                .child(productName)
                .child("exist");

        ref.setValue("yes");
    }

    public void SoldOutProduct(String productName)
    {
        DatabaseReference ref = database.getReference()
                .child("Product")
                .child(productName)
                .child("exist");

        ref.setValue("no");
    }

    public void DeleteProduct(final String productName)// , final Button b, final OnDownloadImageListener onDownloadImageListener)
    {
        //image remove
        StorageReference ref1 = storage.getReference().child("images/" + productName);
        try {
            ref1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference ref = database.getReference()
                            .child("Product")
                            .child(productName);

                    ref.removeValue();

                    //onDownloadImageListener.onRemoveSucceed(b);
                }
            });

        } catch (Exception e) {
            //onDownloadImageListener.onFailed();
        }
    }
}
