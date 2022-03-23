package com.aryomtech.dhitifoundation.admin_panel.Files;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.Profile.external_expense.Adapter.exp_ext_Adapter;
import com.aryomtech.dhitifoundation.Profile.external_expense.Model.expenseData;
import com.aryomtech.dhitifoundation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class track_files extends Fragment {

    View view;
    EditText search;
    private Context contextNullSafe;
    ImageView image;
    TextView msg;
    DatabaseReference expense_record;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressBar progressBar;
    ArrayList<String> uidlist;
    ArrayList<expenseData> list,mylist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_track_files, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_content);
        image=view.findViewById(R.id.imageView13);
        msg=view.findViewById(R.id.textView112);
        progressBar=view.findViewById(R.id.progressBar2);
        search=view.findViewById(R.id.search);
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        list=new ArrayList<>();
        mylist=new ArrayList<>();
        uidlist=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        expense_record= FirebaseDatabase.getInstance().getReference().child("expense_record");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);
        view.findViewById(R.id.imageBack).setOnClickListener(v->back());
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        new Handler(Looper.myLooper()).postDelayed(this::get_data,1000);
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

        }
        return view;
    }
    private void search(String s) {
        mylist.clear();
        for(expenseData Objects:list){
            if (Objects.getFile_name().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if (Objects.getDescription().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if(Objects.getCity().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
        }
        exp_ext_Adapter exp_ext_adapter=new exp_ext_Adapter(getContext(),mylist,"admin");
        exp_ext_adapter.notifyDataSetChanged();
        if (recyclerView!=null)
            recyclerView.setAdapter(exp_ext_adapter);

    }

    private void get_data() {
        expense_record.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    mylist.clear();
                    uidlist.clear();
                    progressBar.setVisibility(View.GONE);
                    image.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        uidlist.add(ds.getKey());
                    }
                    for (int i=0;i<uidlist.size();i++){
                        for (DataSnapshot ds_val:snapshot.child(uidlist.get(i)).getChildren()){
                            list.add(ds_val.getValue(expenseData.class));
                        }
                    }
                    Collections.reverse(list);
                    exp_ext_Adapter exp_ext_adapter=new exp_ext_Adapter(getContext(),list,"admin");
                    exp_ext_adapter.notifyDataSetChanged();
                    if (recyclerView!=null)
                        recyclerView.setAdapter(exp_ext_adapter);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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
}