package com.example.user.helper.listener;

import com.example.user.model.Product;

import java.util.ArrayList;

public interface OnGetProductsListener {
        void onSucceed(ArrayList<Product> products);
        void onFailed();
}
