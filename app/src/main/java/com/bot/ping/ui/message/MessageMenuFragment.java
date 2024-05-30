package com.bot.ping.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bot.ping.R;
import com.bot.ping.databinding.FragmentMessageMenuBinding;
import com.bot.ping.model.User;
import com.bot.ping.ui.ContactAdapter;
import com.bot.ping.ui.RecyclerItemClickListener;

import java.util.ArrayList;

public class MessageMenuFragment extends Fragment {

    private FragmentMessageMenuBinding binding;
    Bundle arguments;
    View root;
    public RecyclerView recyclerView;
    public ArrayList<User> allContacts = new ArrayList<User>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.setArguments(new Bundle());
        MessageMenuViewModel homeViewModel =
                new ViewModelProvider(this).get(MessageMenuViewModel.class);

        binding = FragmentMessageMenuBinding.inflate(inflater, container, false);

        arguments = getArguments();
        root = binding.getRoot();

        if(allContacts.size()!=0){
            setContact();
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            int i=0;
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            int i=0;
                        }
                    })
            );
        }
        return root;
    }

    public void setContact() {
        recyclerView = root.findViewById(R.id.list);
        ContactAdapter adapter = new ContactAdapter(getActivity(), allContacts);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}