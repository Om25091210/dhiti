package com.aryomtech.dhitifoundation.admin_panel;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.registrationAdapter;
import com.aryomtech.dhitifoundation.admin_panel.model.set_form_data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class see_registration extends Fragment {

    View view;
    String key;
    DatabaseReference fluid_Cards;
    private Context contextNullSafe;
    ImageView imageView20;
    RecyclerView recyclerView;
    List<set_form_data> list=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_see_registration, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        try {
            key = getArguments().getString("keys_for_creating_donation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView20=view.findViewById(R.id.imageView20);
        fluid_Cards= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");

        recyclerView=view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setLayoutManager(layoutManager);

        get_data();

        view.findViewById(R.id.back).setOnClickListener(v->back());
        return view;
    }

    private void get_data() {
        fluid_Cards.child(key).child("registrations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       imageView20.setVisibility(View.VISIBLE);
                       for(DataSnapshot ds:snapshot.getChildren()){
                           list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getValue(set_form_data.class));
                       }
                       registrationAdapter registrationAdapter=new registrationAdapter(getContextNullSafety(),key,list);
                       registrationAdapter.notifyDataSetChanged();
                       if(recyclerView!=null)
                           recyclerView.setAdapter(registrationAdapter);
                   }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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