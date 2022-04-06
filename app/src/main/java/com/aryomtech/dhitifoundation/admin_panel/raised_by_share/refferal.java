package com.aryomtech.dhitifoundation.admin_panel.raised_by_share;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.raised_by_share.adapter.refferal_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class refferal extends Fragment {

    View view;
    String key;
    ArrayList<String> list,uid_list,name_list,dp_list;
    RecyclerView recyclerView;
    ImageView imageBack,image_card;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference fluid_ref,users_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_refferal, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_refferal);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        image_card=view.findViewById(R.id.imageView20);

        fluid_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        list=new ArrayList<>();
        uid_list=new ArrayList<>();
        name_list=new ArrayList<>();
        dp_list=new ArrayList<>();

        try {
            key = getArguments().getString("sending_key")+"";
        } catch (Exception e) {
            e.printStackTrace();
        }
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        imageBack.setOnClickListener(v->back());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setLayoutManager(layoutManager);

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

    private void get_members() {
        fluid_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("raised_by_share").exists()){
                    uid_list.clear();
                    list.clear();
                    for (DataSnapshot ds:snapshot.child("raised_by_share").getChildren()){
                        uid_list.add(ds.getKey());
                        list.add(snapshot.child("raised_by_share").child(Objects.requireNonNull(ds.getKey())).getChildrenCount()+"");
                    }
                    get_name_dp();
                }
                else
                    progressBar.setVisibility(View.GONE);
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
    private void get_name_dp() {
        name_list.clear();
        dp_list.clear();
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0;i<uid_list.size();i++){
                    name_list.add(snapshot.child(uid_list.get(i)).child("name").getValue(String.class));
                    dp_list.add(snapshot.child(uid_list.get(i)).child("dplink").getValue(String.class)+"");
                }
                image_card.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                refferal_Adapter refferal_adapter=new refferal_Adapter(getContext(),list,name_list,dp_list,uid_list,key);
                refferal_adapter.notifyDataSetChanged();
                try {
                    if(recyclerView!=null)
                        recyclerView.setAdapter(refferal_adapter);
                } catch (Exception e) {
                    e.printStackTrace();
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
}