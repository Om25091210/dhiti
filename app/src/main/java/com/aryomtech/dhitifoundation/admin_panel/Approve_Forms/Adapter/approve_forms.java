package com.aryomtech.dhitifoundation.admin_panel.Approve_Forms.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.models.forms_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class approve_forms extends Fragment {

    View view;
    ArrayList<forms_data> list,my_list;
    RecyclerView recyclerView;
    ImageView imageBack,image_card;
    ProgressBar progressBar;
    FirebaseAuth auth;
    SearchView searchView;
    FirebaseUser user;
    DatabaseReference forms_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_approve_forms, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_members);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        searchView=view.findViewById(R.id.searchView);
        image_card=view.findViewById(R.id.imageView20);

        forms_ref= FirebaseDatabase.getInstance().getReference().child("forms");
        list=new ArrayList<>();
        my_list=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        imageBack.setOnClickListener(v->back());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Search(s);
                return false;
            }
        });
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
    private void Search(String s) {
        my_list.clear();
        for(forms_data objects:list){
            if (objects.getAddress().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if(objects.getAge().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if(objects.getBlood().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if(objects.getCity().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if(objects.getContact().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getDedicate_talent().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getDedicate_time().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getEmail().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getExperience_comm().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getGender().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getName().toLowerCase().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getName_of_school_college().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getQualification().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
            else if (objects.getStart_city_dhiti().contains(s.toLowerCase().trim())){
                my_list.add(objects);
            }
        }
        approve_forms_Adapter approve_forms_adapter=new approve_forms_Adapter(getContext(),my_list);
        approve_forms_adapter.notifyDataSetChanged();
        try{
            if (recyclerView!=null)
                recyclerView.setAdapter(approve_forms_adapter);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void get_members() {
        forms_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    image_card.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        list.add(ds.getValue(forms_data.class));
                    }
                    Collections.reverse(list);
                    approve_forms_Adapter approve_forms_adapter=new approve_forms_Adapter(getContext(),list);
                    approve_forms_adapter.notifyDataSetChanged();
                    try{
                        if (recyclerView!=null)
                            recyclerView.setAdapter(approve_forms_adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                    progressBar.setVisibility(View.GONE);
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