package com.example.user.helper.listener;

import android.net.Uri;
import android.widget.Button;

import com.example.user.model.Product;

public interface OnDownloadImageListener {
    void onSucceed(Product p, Uri uri);

    void onFailed();
}
