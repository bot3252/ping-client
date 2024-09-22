package com.bot.ping.model;

import android.text.TextUtils;
import android.util.Patterns;

public class MailChecker {
    public static boolean checkEmail(String target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
