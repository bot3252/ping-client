package com.bot.ping.manager;

import static android.content.Context.MODE_PRIVATE;

import static org.chromium.base.ThreadUtils.runOnUiThread;
import static org.chromium.base.ThreadUtils.setWillOverrideUiThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bot.ping.model.MyUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class DataManager{
    Context context;
    SharedPreferences sharedPreferencesUserData;

    public DataManager(Context context){
        this.context = context;
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
    }

    public void saveUser(MyUser myUser) {
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        if(!myUser.getUuid().equals("null")){
            prefEditor.putString("uuid", myUser.getUuid());
        }
        if(!Objects.equals(myUser.getName(), "null")){
            prefEditor.putString("name", myUser.getName());
        }
        if(!Objects.equals(myUser.getNickname(), "null")){
            prefEditor.putString("nickname", myUser.getEmail());
        }
        if(!Objects.equals(myUser.getEmail(), "null")){
            prefEditor.putString("email", myUser.getEmail());
        }
        if(!Objects.equals(myUser.getDescription(), "null")){
            prefEditor.putString("description", myUser.getNickname());
        }
        if(!Objects.equals(myUser.getPassword(), "null")){
            prefEditor.putString("password", myUser.getPassword());
        }
        prefEditor.apply();
    }

    public void saveToken(String token){
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putString("firebaseToken", token);
        prefEditor.apply();
    }

    public String getToken(){
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
        return sharedPreferencesUserData.getString("firebaseToken",null);
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

        try {
            user.setAvatar(getImage(user.getUuid(),context));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void deleteData(){
        SharedPreferences settings = context.getSharedPreferences("AccountData", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

    public void saveString(){
        File dir = new File(context.getFilesDir(),"");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, "myFile.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("hello! hello! hello! hello! hello! hello! hello! hello! hello! hello!");
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getString(String path) throws IOException {
        String s="";
        try {
            FileInputStream fileIn=context.openFileInput(String.valueOf(new File(path)));
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[5];

            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
    public void saveText(String file){
        File dir = new File(context.getFilesDir(),"");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {

            FileWriter writer = new FileWriter(file);
            writer.append(file);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveImage(Bitmap bitmap, String name, Context context){
        FileOutputStream fOut= null;
        Bitmap localBitmap = bitmap;
        try {
            fOut = new FileOutputStream(context.getFilesDir()+"/"+name+".jpg");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        localBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);;
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      //  localBitmap.recycle();
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
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
}
