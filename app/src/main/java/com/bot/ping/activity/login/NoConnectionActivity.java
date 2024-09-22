package com.bot.ping.activity.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.activity.update.UpdateActivity;

public class NoConnectionActivity extends AppCompatActivity {
    Button buttonCheck;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_wifi);

        context=this;
        buttonCheck = findViewById(R.id.buttonUpdate);
        buttonCheck.setOnClickListener(check);
    }


    View.OnClickListener check = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
            if(connected) {
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
            }else {
                Toast.makeText(context, "У вас нет интернета", Toast.LENGTH_SHORT).show();
            }
        }
    };
}