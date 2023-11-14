package com.aryomtech.dhitifoundation.events;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.events.adapter.AttAdapter;
import com.aryomtech.dhitifoundation.events.model.att_Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class View_attendance extends Fragment {

    View view;
    String date_as_key;
    ArrayList<att_Data> list;
    ArrayList<att_Data> mylist;
    ArrayList<String> name_list;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    DatabaseReference att_ref,users_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_attendance, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        progressBar=view.findViewById(R.id.progressBar2);
        try {
            date_as_key = getArguments().getString("date_key_25091210");
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView=view.findViewById(R.id.rv_att);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);
        list=new ArrayList<>();
        mylist=new ArrayList<>();
        name_list=new ArrayList<>();
        att_ref= FirebaseDatabase.getInstance().getReference().child("attendance");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

        view.findViewById(R.id.imageBack).setOnClickListener(v->onback());
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }

                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        new Handler(Looper.myLooper()).postDelayed(this::get_members,1000);
        return view;
    }
    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }

    private void get_members() {
        list.clear();
        att_ref.child(date_as_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    list.add(ds.getValue(att_Data.class));
                }
                Collections.reverse(list);
                getNames(list);
                if (list.size()==0)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNames(ArrayList<att_Data> list1) {
        name_list.clear();
        for(int i=0;i<list1.size();i++){
            int finalI = i;
            users_ref.child(list1.get(i).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name=snapshot.child("name").getValue(String.class);
                    name_list.add(name);
                    if (finalI ==list1.size()-1){
                        progressBar.setVisibility(View.GONE);
                        AttAdapter attAdapter=new AttAdapter(getContext(),list1,name_list);
                        attAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(attAdapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }

    private void onback() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }

        ft.commit();
    }
}