package com.bot.ping.activity.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bot.ping.R;
import com.bot.ping.activity.login.NoConnectionActivity;
import com.bot.ping.activity.main.ChatActivity;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.dataBase.Parser;
import com.bot.ping.manager.download.Adress;
import com.bot.ping.manager.download.UpdateManger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {
    Button button;
    String domain;
    OkHttpClient client;
    TextView textViewHeaderUpdate, textViewAboutUpdate;
    Context context;
    UpdateManger updateManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        domain=Adress.address(this);

        context=this;
        client=new OkHttpClient();
        updateManger=new UpdateManger(client, this);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if(connected) {
            downloadAboutInfo();
        }else {
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }
    }
    void downloadAboutInfo(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    if(checkUpdate()) {
                        Request request = new Request.Builder()
                                .url(domain + "/updates/aboutUpdateClient")
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            String stringResponse = response.body().string();
                            JSONObject jsonObject = new JSONObject(stringResponse);
                            String description = jsonObject.getString("description");
                            String version = jsonObject.getString("version");
                            UpdateActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    setContentView(R.layout.activity_check_updates);
                                    button=findViewById(R.id.buttonDownloadUpdate);
                                    textViewAboutUpdate=findViewById(R.id.textViewAboutUpdate);
                                    textViewHeaderUpdate=findViewById(R.id.textViewHeader);

                                    button.setOnClickListener(download);
                                    textViewAboutUpdate.setText(description);
                                    textViewHeaderUpdate.setText("Обновление " + version);
                                }
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    View.OnClickListener download = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(domain+"/updates/download"));
            startActivity(browserIntent);
        }
    };

    private boolean checkUpdate() throws PackageManager.NameNotFoundException {

        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        String version = pInfo.versionName;
        if(!updateManger.checkUpdate(version)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
            startActivity(intent);
            return false;
        }
        return true;
    }
}
