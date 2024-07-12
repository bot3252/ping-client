package com.bot.ping.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.model.User;

import java.util.ArrayList;
import java.util.List;
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private final LayoutInflater inflater;
    public final List<User> contacts;

    public ContactAdapter(Context context, ArrayList<User> contacts) {
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder holder, int position) {
        User user = contacts.get(position);
        holder.avatarView.setImageBitmap(user.getAvatar());
        holder.nameView.setText(user.getName());
        holder.nickName.setText(user.getNickname());
        holder.emailView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView avatarView;
        final TextView nameView, emailView, nickName;
        ViewHolder(View view){
            super(view);
            avatarView = view.findViewById(R.id.avatar);
            nameView = view.findViewById(R.id.name);
            emailView = view.findViewById(R.id.email);
            nickName = view.findViewById(R.id.nickName);
        }
    }

    public List<User> getUsers(){
        return contacts;
    }
}