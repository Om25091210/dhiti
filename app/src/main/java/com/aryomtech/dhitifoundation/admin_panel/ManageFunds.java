package com.aryomtech.dhitifoundation.admin_panel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.edit_fluid_card_Adapter;
import com.aryomtech.dhitifoundation.admin_panel.model.fluid_Cards_Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class ManageFunds extends Fragment {

    View view;
    LottieAnimationView fab,refresh;
    RecyclerView recyclerView;
    DatabaseReference card_ref;
    ImageView image;
    TextView msg;
    CheckBox user_cards;
    EditText search;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayList<fluid_Cards_Data> list;
    ArrayList<fluid_Cards_Data> mylist;
    Query query;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_manage_funds, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        fab=view.findViewById(R.id.fab);
        recyclerView=view.findViewById(R.id.rv_content);
        image=view.findViewById(R.id.imageView13);
        msg=view.findViewById(R.id.textView112);
        search=view.findViewById(R.id.search);
        refresh=view.findViewById(R.id.refresh);
        user_cards=view.findViewById(R.id.checkBox2);
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        card_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        query=card_ref;
        list=new ArrayList<>();
        mylist=new ArrayList<>();

        view.findViewById(R.id.imageBack).setOnClickListener(v->back());
        refresh.pauseAnimation();
        refresh.setOnClickListener(v->{
            refresh.resumeAnimation();
            list.clear();
            mylist.clear();
            onstart();
        });
        user_cards.setOnClickListener(v->{
            if(user_cards.isChecked()) {
                query = card_ref.orderByChild("uid").equalTo(user.getUid());
                refresh.resumeAnimation();
                list.clear();
                mylist.clear();
                onstart();
            }
            else{
                query=card_ref;
                refresh.resumeAnimation();
                list.clear();
                mylist.clear();
                onstart();
            }
        });

        fab.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new Create_ManageFund())
                        .addToBackStack(null)
                        .commit());

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
        new Handler(Looper.myLooper()).postDelayed(this::onstart,1000);
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
    private void onstart() {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    image.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if(!list.contains(ds.getValue(fluid_Cards_Data.class)))
                            list.add(ds.getValue(fluid_Cards_Data.class));
                    }
                    Collections.reverse(list);

                    edit_fluid_card_Adapter edit_fluid_card_adapter=new edit_fluid_card_Adapter(getContext(),list);
                    edit_fluid_card_adapter.notifyDataSetChanged();
                    try {
                        recyclerView.setAdapter(edit_fluid_card_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    refresh.pauseAnimation();
                }
                else{
                    image.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.VISIBLE);
                    refresh.pauseAnimation();
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    private void search(String s) {
        mylist.clear();
        for(fluid_Cards_Data object:list){
            if (object.getTitle().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            }else if (object.getLocation().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            }
        }

        edit_fluid_card_Adapter edit_fluid_card_adapter=new edit_fluid_card_Adapter(getContext(),mylist);
        edit_fluid_card_adapter.notifyDataSetChanged();
        try {
            recyclerView.setAdapter(edit_fluid_card_adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}