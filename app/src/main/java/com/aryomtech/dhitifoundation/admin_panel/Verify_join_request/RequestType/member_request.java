package com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.RequestType;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.Adapter.requestAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class member_request extends Fragment {

    View view;
    TextView msg;
    ArrayList<String> name_list,detail_list,my_name_list
            ,my_detail_list,key_list,my_key_list
            ,uid_list,my_uid_list;
    RecyclerView recyclerView;
    SearchView searchView;
    DatabaseReference member_ref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_member_request, container, false);
        recyclerView=view.findViewById(R.id.rv_personal);
        msg=view.findViewById(R.id.textView71);
        searchView=view.findViewById(R.id.searchView);

        member_ref= FirebaseDatabase.getInstance().getReference().child("member_Request");

        name_list=new ArrayList<>();
        detail_list=new ArrayList<>();
        my_name_list=new ArrayList<>();
        my_detail_list=new ArrayList<>();
        key_list=new ArrayList<>();
        my_key_list=new ArrayList<>();
        uid_list=new ArrayList<>();
        my_uid_list=new ArrayList<>();

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        new Handler(Looper.myLooper()).postDelayed(this::getTasks,1000);
        return view;
    }

    private void getTasks() {
        member_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    name_list.clear();
                    detail_list.clear();
                    key_list.clear();
                    uid_list.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.GONE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        key_list.add(ds.getKey());
                        uid_list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).child("uid").getValue(String.class));
                        name_list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).child("name").getValue(String.class));
                        if (snapshot.child(ds.getKey()).child("email").exists()){
                            detail_list.add(snapshot.child(ds.getKey()).child("email").getValue(String.class));
                        }
                        else if (snapshot.child(ds.getKey()).child("phone").exists()){
                            detail_list.add(snapshot.child(ds.getKey()).child("phone").getValue(String.class));
                        }
                    }
                    requestAdapter requestAdapter=new requestAdapter("member",getContext(),name_list,detail_list,key_list,uid_list);
                    requestAdapter.notifyDataSetChanged();
                    try {
                        recyclerView.setAdapter(requestAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    recyclerView.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void Search(String s) {
        my_name_list.clear();
        my_detail_list.clear();
        my_key_list.clear();
        my_uid_list.clear();
        for(String object:name_list){
            if (object.toLowerCase().contains(s.toLowerCase().trim())){
                my_name_list.add(object);
                int index=name_list.indexOf(object);
                my_detail_list.add(detail_list.get(index));
                my_key_list.add(key_list.get(index));
                my_uid_list.add(uid_list.get(index));
            }
        }
        requestAdapter requestAdapter=new requestAdapter("member",getContext(),my_name_list,my_detail_list,my_key_list,my_uid_list);
        requestAdapter.notifyDataSetChanged();
        try {
            recyclerView.setAdapter(requestAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}