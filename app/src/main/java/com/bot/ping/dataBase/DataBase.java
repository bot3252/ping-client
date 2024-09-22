package com.bot.ping.dataBase;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.Message;
import com.bot.ping.model.User;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataBase extends SQLiteOpenHelper {
    Context context;
    SQLiteDatabase db;
    public DataBase(Context context, String myUuid){
        super(context, myUuid+".db", null, 21);
        this.context = context;
        db = context.openOrCreateDatabase(myUuid+".db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (uuid TEXT, name TEXT, nickname TEXT, email TEXT, description TEXT)");
        db = context.openOrCreateDatabase(myUuid+".db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS messages (uuid TEXT, data TEXT, time TEXT, message TEXT, to_ TEXT, from_ TEXT, status TEXT)");
    }


    public void saveContact(User user){
        String uuid = user.getUuid();
        String name = user.getName();
        String nickname = user.getNickname();
        String description = user.getDescription();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("uuid", uuid);
        contentValues.put("name", name);
        contentValues.put("nickname", nickname);
        contentValues.put("description", description);

        DataManager.saveImage(user.getAvatar(), uuid, context);

        sqLiteDatabase.insert("contacts", null, contentValues);
    }
    public boolean checkUser(String uuid){
        Cursor query = db.rawQuery("SELECT * FROM contacts WHERE uuid='"+uuid+"'", null);
        String name = null;
        while(query.moveToNext()){
            name = query.getString(1);
        }
        query.close();

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
                String description = query.getString(3);
                User user = new User(uuid, name, nickname, description);
                try {
                    user.setAvatar(DataManager.getImage(uuid, context));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                contacts.add(user);
            }
        }
        query.close();
        return contacts;
    }

    public User getContact(String uuid){
        User user = new User();
        Cursor query = db.rawQuery("SELECT * FROM contacts WHERE uuid='"+uuid+"'", null);
        while(query.moveToNext()){
            user.setUuid(uuid);
            user.setName(query.getString(1));
            user.setNickname(query.getString(2));
            user.setDescription(query.getString(3));
            try {
                user.setAvatar(DataManager.getImage(uuid, context));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        query.close();
        return user;
    }

    public void saveMessages(ArrayList<Message> messages){
        for (int i = 0; i<=messages.size()-1;i++) {
            saveMessage(messages.get(i));
        }
    }

    public void saveMessage(Message message){
        String uuid = message.getUuid();
        String data = message.getDate();
        String time = message.getTime();
        String messageString = message.getMessage();
        String to = message.getTo();
        String from = message.getFrom();
        String status = message.getStatus();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("uuid", uuid);
        contentValues.put("data", data);
        contentValues.put("time", time);
        contentValues.put("message", messageString);
        contentValues.put("to_", to);
        contentValues.put("from_", from);
        contentValues.put("status", status);

        sqLiteDatabase.insert("messages", null, contentValues);
    }

    public ArrayList<Message> getMessages(String myUuid, String chatUuid){
        ArrayList<Message> messages = new ArrayList<Message>();
        Cursor query = db.rawQuery("SELECT * FROM messages where to_ in ('"+myUuid+"', '"+chatUuid+"') and from_ in ('"+myUuid+"', '"+chatUuid+"') ORDER BY data", null);
        if(query!=null) {
            while (query.moveToNext()) {
                String uuid = query.getString(0);
                String data = query.getString(1);
                String time = query.getString(2);
                String messageString = query.getString(3);
                String to = query.getString(4);
                String from = query.getString(5);
                String status = query.getString(6);
                Message message = new Message(uuid, from, to, messageString, time, data);
                if(Objects.equals(myUuid, from)){
                    message.setMsgType(Message.MSG_TYPE_SENT);
                }else {
                    message.setMsgType(Message.MSG_TYPE_RECEIVED);
                }
                messages.add(message);
            }
        }
        query.close();
        return messages;
    }


    public void deleteMessages(String myUuid, String chatUuid){
        ArrayList<Message> messages = getMessages(myUuid, chatUuid);
        ArrayList<String> uuids = new ArrayList<>();
        String size="";
        for(int i=0;i<=messages.size();i++){
            uuids.add(messages.get(i).getUuid());
            if(i!=messages.size()) {
                size += "?, ";
            }else {
                size += "?";
            }
        }
        db.execSQL("DELETE FROM messages WHERE recno IN ("+size+")", new ArrayList[]{uuids});
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addNewContacts(ArrayList<User> allContacts){
        ArrayList<User> localContacts = getContacts();
        if(allContacts.size()>localContacts.size()) {
            for (int i = 0; i <= allContacts.size() - 1; i++) {
                User currentUser = allContacts.get(i);
                if (currentUser != null) {
                    if (localContacts.size() <= i) {
                        saveContact(currentUser);
                    } else {
                        User currentLocalUser = localContacts.get(i);
                        if (currentLocalUser != null) {
                            if (currentLocalUser.getUuid().equals(currentUser.getUuid())) {
                                saveContact(currentUser);
                            }
                        }
                    }
                }
            }
        }
    }

    public void deleteAllData(String uuid) {
        context.deleteDatabase(uuid);
    }

    public boolean checkUsers(ArrayList<User> allContacts, ArrayList<User> downloadContacts) {
        return !(allContacts.size() ==downloadContacts.size());
    }
}
