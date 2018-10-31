package com.example.user.helper.listener;

import com.example.user.model.User;

public interface OnGetAdminInfoListener {
    void onSucceed(User user);
    void onFailed();
}
