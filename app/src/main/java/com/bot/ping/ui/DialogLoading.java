package com.bot.ping.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.bot.ping.R;

public class DialogLoading {
    Context context;
    Dialog dialog;

    public DialogLoading(Context context){
        this.context = context;
    }

    public void showDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();
    }

    public void closeDialog(){
        dialog.dismiss();
    }
}