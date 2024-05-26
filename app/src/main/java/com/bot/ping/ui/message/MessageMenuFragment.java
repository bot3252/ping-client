package com.bot.ping.ui.message;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.databinding.FragmentMessageMenuBinding;
import com.bot.ping.databinding.FragmentSettingsBinding;
import com.bot.ping.model.User;
import com.bot.ping.ui.ContactAdapter;

import java.util.ArrayList;

public class MessageMenuFragment extends Fragment {

    private FragmentMessageMenuBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageMenuViewModel homeViewModel =
                new ViewModelProvider(this).get(MessageMenuViewModel.class);

        binding = FragmentMessageMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    public void setContacts(){

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}