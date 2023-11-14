package com.aryomtech.dhitifoundation.admin_panel.TasksType;

import android.os.Bundle;
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
import com.aryomtech.dhitifoundation.admin_panel.Adapter.personal_task_Adapter;
import com.aryomtech.dhitifoundation.privateTask.Model.privateTaskData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class private_tasks_approval extends Fragment {

    View view;
    RecyclerView recyclerView;
    DatabaseReference private_task_ref,users_ref;
    TextView msg;
    FirebaseAuth auth;
    FirebaseUser user;
    SearchView searchView;
    ArrayList<Long> list_tasks;
    ArrayList<Long> my_list_tasks;
    ArrayList<privateTaskData> privateTaskData_list;
    ArrayList<privateTaskData> my_privateTaskData_list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_private_tasks_approval, container, false);
        recyclerView=view.findViewById(R.id.rv_personal);
        msg=view.findViewById(R.id.textView71);
        searchView=view.findViewById(R.id.searchView);

        privateTaskData_list=new ArrayList<>();
        list_tasks=new ArrayList<>();
        my_privateTaskData_list=new ArrayList<>();
        my_list_tasks=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
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

        private_task_ref= FirebaseDatabase.getInstance().getReference().child("private_approval");
        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        getTasks();
        return view;
    }

    private void Search(String s) {
        my_privateTaskData_list.clear();
        my_list_tasks.clear();
        for(privateTaskData objects:privateTaskData_list){
            if (objects.getTask_title().toLowerCase().contains(s.toLowerCase().trim())){
                int index=privateTaskData_list.indexOf(objects);
                my_list_tasks.add(list_tasks.get(index));
                my_privateTaskData_list.add(objects);
            }
            else if (objects.getDescription().trim().toLowerCase().contains(s.toLowerCase().trim())){
                int index=privateTaskData_list.indexOf(objects);
                my_list_tasks.add(list_tasks.get(index));
                my_privateTaskData_list.add(objects);
            }
            else if (objects.getGiven_on().trim().toLowerCase().contains(s.toLowerCase().trim())){
                int index=privateTaskData_list.indexOf(objects);
                my_list_tasks.add(list_tasks.get(index));
                my_privateTaskData_list.add(objects);
            }
            else if (objects.getTask_deadline().trim().toLowerCase().contains(s.toLowerCase().trim())){
                int index=privateTaskData_list.indexOf(objects);
                my_list_tasks.add(list_tasks.get(index));
                my_privateTaskData_list.add(objects);
            }
            else if (objects.getStatus().toLowerCase().contains(s.toLowerCase().trim())){
                int index=privateTaskData_list.indexOf(objects);
                my_list_tasks.add(list_tasks.get(index));
                my_privateTaskData_list.add(objects);
            }
        }
        personal_task_Adapter personal_task_Adapter=new personal_task_Adapter(getContext(),my_privateTaskData_list,my_list_tasks);
        personal_task_Adapter.notifyDataSetChanged();
        try {
            recyclerView.setAdapter(personal_task_Adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTasks() {
        private_task_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                privateTaskData_list.clear();
                list_tasks.clear();
                if(snapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);

                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(!Objects.equals(ds.getKey(), "added_task")) {
                            if (Objects.equals(
                                    snapshot.child(Objects.requireNonNull(ds.getKey())).child("creator").getValue(String.class)
                                    , user.getUid())) {
                                privateTaskData_list.add(ds.getValue(privateTaskData.class));
                                msg.setVisibility(View.GONE);
                            }
                        }
                        if(snapshot.child(ds.getKey()).child("added_task").exists())
                            list_tasks.add(snapshot.child(ds.getKey()).child("added_task").getChildrenCount());
                        else
                            list_tasks.add(0L);
                    }
                    Collections.reverse(list_tasks);
                    Collections.reverse(privateTaskData_list);
                    personal_task_Adapter personal_task_Adapter=new personal_task_Adapter(getContext(),privateTaskData_list,list_tasks);
                    personal_task_Adapter.notifyDataSetChanged();
                    try {
                        recyclerView.setAdapter(personal_task_Adapter);
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
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}