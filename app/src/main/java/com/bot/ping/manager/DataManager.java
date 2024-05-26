package com.bot.ping.manager;

import static android.content.Context.MODE_PRIVATE;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.bot.ping.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Objects;

public class DataManager{
    Context context;
    SharedPreferences sharedPreferencesUserData;
    OutputStream outputStream;

    public DataManager(Context context){
        this.context = context;
        sharedPreferencesUserData = context.getSharedPreferences("AccountData", MODE_PRIVATE);
    }

    public void saveUser(User user) {
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        prefEditor.putString("name", user.getName());
        prefEditor.putString("email", user.getEmail());
        prefEditor.putString("uuid", user.getUuid());
        prefEditor.putString("password", user.getPassword());
        prefEditor.apply();
    }

    public User getUser() {
        SharedPreferences.Editor prefEditor = sharedPreferencesUserData.edit();
        User user = new User();
        user.setName(sharedPreferencesUserData.getString("name",null));
        user.setEmail(sharedPreferencesUserData.getString("email",null));
        user.setUuid(Objects.requireNonNull(sharedPreferencesUserData.getString("uuid", null)));
        user.setPassword(sharedPreferencesUserData.getString("password",null));
        return user;
    }

    public void deleteData(){
        SharedPreferences settings = context.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

//    public void saveImage(Bitmap bitmap) {
//
//        File dir = new File(Environment.getExternalStorageDirectory(),"SaveImage");
//
//        if (!dir.exists()){
//
//            dir.mkdir();
//
//        }
//
//        File file = new File(dir,System.currentTimeMillis()+".jpg");
//        try {
//            outputStream = new FileOutputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        runOnUiThread (new Thread(new Runnable() {
//            public void run() {
//                //bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
//                try {
//                    outputStream.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }));
//
//    }

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

    public void saveImage(Bitmap bitmap, String name){
        FileOutputStream fOut= null;
        try {
            fOut = new FileOutputStream(context.getFilesDir()+"/"+name);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bitmap.recycle();
    }

    public Bitmap getImage(String name) throws IOException {
        File imgFile = new  File(context.getFilesDir() +"/"+ name);

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
}
