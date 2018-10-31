package com.example.user.helper.listener;

import android.widget.Button;

import com.example.user.model.Favorate;

public interface OnCheckFavorateListener {
    void exist(Button button, Favorate favorate);
    void notExist(Button button);
}
