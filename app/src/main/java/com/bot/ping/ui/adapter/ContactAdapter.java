package com.bot.ping.ui.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.manager.data.DataManager;
import com.bot.ping.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    public List<User> contacts=new ArrayList<>();
    Activity activity;
    public ContactAdapter(Activity activity, ArrayList<User> contacts) {
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.contact, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {
        User user = contacts.get(position);
        holder.avatarView.setImageBitmap(user.getAvatar());
        holder.nameView.setText(user.getName());
        holder.nickName.setText(user.getNickname());
        if(Objects.equals(user.getStatus(), User.TYPE_STATUS_ONLINE)){
            Drawable drawable=ContextCompat.getDrawable(activity, R.drawable.status_online);
            holder.statusAvatar.setImageDrawable(drawable);
        }else {
            Drawable drawable=ContextCompat.getDrawable(activity, R.drawable.status_offline);
            holder.statusAvatar.setImageDrawable(drawable);
        }
    }
    public void updateContactStatus(String uuid, String status) {
        int index = getIndexView(uuid);
        if(index>-1) {
            User user = contacts.get(index);
            user.setStatus(status);
            contacts.set(index, user);
            notifyItemChanged(index);
        }
    }
    public void addItem(User user){
        contacts.add(user);
        notifyDataSetChanged();
    }
    int getIndexView(String uuid){
        for (int index = 0; index<=contacts.size()-1;index++){
            if(contacts.get(index).getUuid().equals(uuid))
                return index;
        }
        return -1;
    }
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView avatarView, statusAvatar;
        final TextView nameView, nickName;
        ViewHolder(View view){
            super(view);
            avatarView = view.findViewById(R.id.avatar);
            nameView = view.findViewById(R.id.name);
            nickName = view.findViewById(R.id.nickName);
            statusAvatar=view.findViewById(R.id.imageViewStatus);
        }
    }

    public List<User> getUsers(){
        return contacts;
    }

     public void updateContacts(List<User> users){
        contacts=users;
     }
}