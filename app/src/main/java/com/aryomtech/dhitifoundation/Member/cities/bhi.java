package com.aryomtech.dhitifoundation.Member.cities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.aryomtech.dhitifoundation.Member.cities.Adapter.memberAdpater_public;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.cities.model.member_data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import soup.neumorphism.NeumorphButton;

public class bhi extends Fragment {

    View view;
    ImageView imageView;
    RecyclerView recyclerView;
    ArrayList<member_data> list;
    ArrayList<member_data> mylist;
    DatabaseReference users_ref;
    memberAdpater_public memberAdapter;
    NeumorphButton proceed;
    ArrayList<String> added_peoples_list;
    SearchView searchView;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_bhi, container, false);
        progressBar=view.findViewById(R.id.progressBar2);
        list=new ArrayList<>();
        mylist=new ArrayList<>();
        added_peoples_list=new ArrayList<>();
        imageView=view.findViewById(R.id.img);
        imageView.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.rv_bsp);
        proceed=view.findViewById(R.id.proceed);
        searchView=view.findViewById(R.id.searchView);

        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        new Handler(Looper.myLooper()).postDelayed(this::onstart,1000);
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
        return view;
    }
    private void Search(String s) {
        mylist.clear();
        for (member_data objects : list) {
            if (objects.getName().toLowerCase().contains(s.toLowerCase().trim()))
                mylist.add(objects);
        }
        memberAdapter = new memberAdpater_public(mylist, getContext());
        try {
            recyclerView.setAdapter(memberAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        memberAdapter.notifyDataSetChanged();
    }
    private void onstart() {
        if(users_ref!=null){
            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").exists()) {
                                if (Objects.equals(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").getValue(String.class)).toLowerCase(), "bhilai")) {
                                    if (snapshot.child(ds.getKey()).child("identity").exists()) {
                                        if (Objects.requireNonNull(snapshot.child(ds.getKey()).child("identity").getValue(String.class)).equalsIgnoreCase("member")
                                                || Objects.requireNonNull(snapshot.child(ds.getKey()).child("identity").getValue(String.class)).equalsIgnoreCase("chapter-head")) {
                                            list.add(ds.getValue(member_data.class));
                                            imageView.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        memberAdapter=new memberAdpater_public(list,getContext());
                        try {
                            recyclerView.setAdapter(memberAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        memberAdapter.notifyDataSetChanged();
                    }
                    else{
                        imageView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }
}