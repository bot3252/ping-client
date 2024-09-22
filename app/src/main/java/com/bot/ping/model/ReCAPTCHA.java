package com.bot.ping.model;

import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

public class ReCAPTCHA implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    final String SiteKey = "6LdilM4pAAAAAAl15ZMchzxG6Wl9aMu5ofQSLvLe";
    final String SecretKey  = "6LdilM4pAAAAALb9i6zrj2oymfqhcwfFG638LfY_";
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    public ReCAPTCHA(Context context){
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }
    public void startCaptcha(CheckBox checkBox){
        checkBox.setChecked(!checkBox.isChecked());
        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, SiteKey)
                .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult result) {
                        String status = result.getStatus().zza().toString();
                        checkBox.setChecked(status.equals("SUCCESS"));
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
