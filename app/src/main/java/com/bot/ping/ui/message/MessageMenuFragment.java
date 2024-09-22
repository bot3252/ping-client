package com.bot.ping.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.activity.main.ChatActivity;
import com.bot.ping.activity.main.MainActivity;
import com.bot.ping.databinding.FragmentMessageMenuBinding;
import com.bot.ping.model.User;
import com.bot.ping.ui.adapter.ContactAdapter;
import com.bot.ping.ui.listiner.RecyclerItemClickListener;

import java.util.ArrayList;

public class MessageMenuFragment extends Fragment {

    private FragmentMessageMenuBinding binding;
    Bundle arguments;
    View root;
    public RecyclerView recyclerView;
    public ArrayList<User> allContacts;
    public ContactAdapter adapter;
    public Intent intent;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.setArguments(new Bundle());
        MessageMenuViewModel homeViewModel =
                new ViewModelProvider(this).get(MessageMenuViewModel.class);

        binding = FragmentMessageMenuBinding.inflate(inflater, container, false);

        arguments = getArguments();
        root = binding.getRoot();
        recyclerView = root.findViewById(R.id.listContacts);
        recyclerView.addOnItemTouchListener(recyclerItemClickListener);
        if(allContacts!=null){
//            if(!allContacts.isEmpty()) {
                setAdapter();
//            }
        }
        return root;
    }
    RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
        @Override public void onItemClick(View view, int position) {
            MainActivity mainActivity = (MainActivity) getActivity();
            intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
            if (mainActivity != null) {
                mainActivity.setIntent(intent);
            }
            intent.putExtra("uuid",allContacts.get(position).getUuid());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_slide_right, R.anim.anim_slide_out_left);
        }

        @Override public void onLongItemClick(View view, int position) {

        }
    });
    public void setAdapter() {
        adapter = new ContactAdapter(getActivity() ,allContacts);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}