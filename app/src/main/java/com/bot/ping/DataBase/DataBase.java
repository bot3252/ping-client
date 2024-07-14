package com.bot.ping.DataBase;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bot.ping.manager.DataManager;
import com.bot.ping.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    Context context;
    SQLiteDatabase db;
    public DataBase(Context context, String myUuid){
        super(context, myUuid+".db", null, 21);
        this.context = context;
        db = context.openOrCreateDatabase(myUuid+".db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (uuid TEXT, name TEXT, nickname TEXT, email TEXT, description TEXT)");
    }


    public void saveContact(User user){
        String uuid = user.getUuid();
        String name = user.getName();
        String nickname = user.getNickname();
        String email = user.getEmail();
        String description = user.getDescription();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("uuid", uuid);
        contentValues.put("name", name);
        contentValues.put("nickname", nickname);
        contentValues.put("email", email);
        contentValues.put("description", description);

        DataManager.saveImage(user.getAvatar(), uuid, context);

        sqLiteDatabase.insert("contacts", null, contentValues);
    }
    public boolean checkUser(String uuid){
        ArrayList<User> contacts = new ArrayList<User>();
        Cursor query = db.rawQuery("SELECT * FROM contacts WHERE uuid='"+uuid+"'", null);
        String name = null;
        while(query.moveToNext()){
            name = query.getString(1);
        }
        query.close();
        db.close();

        return name==null;
    }
    public void saveContacts(List<User> users){
        for (int i = 0; i<=users.size()-1;i++){
            saveContact(users.get(i));
        }
    }

    public ArrayList<User> getContacts(){
        ArrayList<User> contacts = new ArrayList<User>();
        Cursor query = db.rawQuery("SELECT * FROM contacts", null);
        if(query!=null) {
            while (query.moveToNext()) {
                String uuid = query.getString(0);
                String name = query.getString(1);
                String nickname = query.getString(2);
                String email = query.getString(3);
                String description = query.getString(4);
                User user = new User(uuid, email, name, nickname, description);
                try {
                    user.setAvatar(DataManager.getImage(uuid, context));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                contacts.add(user);
            }
        }
        query.close();
        db.close();
        return contacts;
    }

    public User getContact(String uuid){
        User user = new User();
        Cursor query = db.rawQuery("SELECT * FROM contacts WHERE uuid='"+uuid+"'", null);
        while(query.moveToNext()){
            user.setUuid(uuid);
            user.setName(query.getString(1));
            user.setNickname(query.getString(2));
            user.setEmail(query.getString(3));
            user.setDescription(query.getString(4));
            try {
                user.setAvatar(DataManager.getImage(uuid, context));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        query.close();
        db.close();
        return user;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAllContacts(String uuid) {
        context.deleteDatabase(uuid);
    }
}
