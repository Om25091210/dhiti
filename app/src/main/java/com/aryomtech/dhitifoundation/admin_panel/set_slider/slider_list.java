package com.aryomtech.dhitifoundation.admin_panel.set_slider;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.Profile.external_expense.Adapter.exp_ext_Adapter;
import com.aryomtech.dhitifoundation.Profile.external_expense.Model.expenseData;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.slider.Model.ModelSmoolider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class slider_list extends Fragment {

    View view;
    LottieAnimationView fab;
    RecyclerView recyclerView;
    ImageView image;
    TextView msg;
    EditText search;
    ArrayList<ModelSmoolider> list,mylist;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_slider_list, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        fab=view.findViewById(R.id.fab);
        recyclerView=view.findViewById(R.id.rv_content);
        image=view.findViewById(R.id.imageView13);
        msg=view.findViewById(R.id.textView112);
        search=view.findViewById(R.id.search);
        progressBar=view.findViewById(R.id.progressBar2);

        list=new ArrayList<>();
        mylist=new ArrayList<>();
        auth= FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference().child("slider");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);
        new Handler(Looper.myLooper()).postDelayed(this::get_data,1000);
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
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

        }
        fab.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new slider_card())
                .addToBackStack(null)
                .commit());
        return view;
    }

    private void search(String s) {
        mylist.clear();
        for(ModelSmoolider Objects:list){
            if (Objects.getHead_text().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if (Objects.getDes_text().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
        }
        sliderAdapter sliderAdapter=new sliderAdapter(getContext(),mylist);
        sliderAdapter.notifyDataSetChanged();
        if (recyclerView!=null)
            recyclerView.setAdapter(sliderAdapter);
    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
    private void get_data() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    mylist.clear();
                    image.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot keys : snapshot.getChildren()) {
                        ModelSmoolider gift = new ModelSmoolider();
                        gift.setImage_url(snapshot.child(Objects.requireNonNull(keys.getKey())).child("image_url").getValue(String.class));
                        gift.setHead_text(snapshot.child(Objects.requireNonNull(keys.getKey())).child("head_text").getValue(String.class));
                        gift.setDes_text(snapshot.child(Objects.requireNonNull(keys.getKey())).child("des_text").getValue(String.class));
                        gift.setPushkey(snapshot.child(Objects.requireNonNull(keys.getKey())).child("pushkey").getValue(String.class));

                        list.add(gift);
                    }
                    Collections.reverse(list);
                    sliderAdapter sliderAdapter = new sliderAdapter(getContext(), list);
                    sliderAdapter.notifyDataSetChanged();
                    if (recyclerView != null)
                        recyclerView.setAdapter(sliderAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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