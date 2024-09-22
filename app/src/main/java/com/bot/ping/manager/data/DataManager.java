package com.bot.ping.manager.data;

import static android.content.Context.MODE_PRIVATE;

import static org.chromium.base.ThreadUtils.runOnUiThread;
import static org.chromium.base.compat.ApiHelperForM.getSystemService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;

import com.bot.ping.manager.download.Adress;
import com.bot.ping.model.MyUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.UUID;

public class DataManager{
    Context context;
    SharedPreferences sharedPreferencesUserData;
    public DataManager(Context context){
        this.context = context;
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
    }

    public static void saveUser(MyUser myUser, Context context) {
        SharedPreferences sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        if(!myUser.getUuid().equals("null")){
            prefEditor.putString("uuid", myUser.getUuid());
        }
        if(!Objects.equals(myUser.getName(), "null")){
            prefEditor.putString("name", myUser.getName());
        }
        if(!Objects.equals(myUser.getNickname(), "null")){
            prefEditor.putString("nickname", myUser.getNickname());
        }
        if(!Objects.equals(myUser.getEmail(), "null")){
            prefEditor.putString("email", myUser.getEmail());
        }
        if(!Objects.equals(myUser.getDescription(), "null")){
            prefEditor.putString("description", myUser.getDescription());
        }
        if(!Objects.equals(myUser.getPassword(), "null")){
            prefEditor.putString("password", myUser.getPassword());
        }
        prefEditor.apply();
    }

    public static void saveAndSendToken(String token, Context context){
        SharedPreferences sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putString("firebaseToken", token);
        prefEditor.apply();
    }

    public String getToken(){
        return sharedPreferencesUserData.getString("firebaseToken",null);
    }

    public void saveIsLocalServer(boolean token){
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putBoolean("localServer", token);
        prefEditor.apply();
    }
    public boolean isLocalServer(){
        return sharedPreferencesUserData.getBoolean("localServer",false);
    }

    public MyUser getMyUser() {
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);

        MyUser user = new MyUser();

        user.setUuid(sharedPreferencesUserData.getString("uuid", null));
        user.setName(sharedPreferencesUserData.getString("name",null));
        user.setNickname(sharedPreferencesUserData.getString("nickname",null));
        user.setEmail(sharedPreferencesUserData.getString("email",null));
        user.setDescription(sharedPreferencesUserData.getString("description",null));
        user.setPassword(sharedPreferencesUserData.getString("password",null));
        user.setFirebaseToken(sharedPreferencesUserData.getString("firebaseToken",null));
        try {
            user.setAvatar(getImage(user.getUuid(),context));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public static JSONObject getAddress(Context context) throws JSONException {

        SharedPreferences sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
        String address = sharedPreferencesUserData.getString("address", "https://koshmin.ru/ping");
        String addressWebsocket= sharedPreferencesUserData.getString("address", "wss://koshmin.ru/ping/websocket");

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("address",address);
        jsonObject.put("addressWebsocket",addressWebsocket);

        return jsonObject;
    }

    public void setAddress(String address, String addressWebsocket){
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putString("address", address);
        prefEditor.putString("addressWebsocket", addressWebsocket);
    }

    public void deleteData(){
        SharedPreferences settings = context.getSharedPreferences("AccountData", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }



    public static void saveImage(Bitmap bitmap, String name, Context context){
        FileOutputStream fOut= null;
        try {
            fOut = new FileOutputStream(context.getFilesDir()+"/"+name+".jpg");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);;
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap getImage(String name, Context context) throws IOException {
        File imgFile = new  File(context.getFilesDir() +"/"+ name+".jpg");

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return null;
    }

    public static byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        return bitmap;
    }

    @SuppressLint("HardwareIds")
    public static String getIdDevice(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
