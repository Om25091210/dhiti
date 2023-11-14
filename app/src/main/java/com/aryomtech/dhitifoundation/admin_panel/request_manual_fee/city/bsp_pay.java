package com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Assign_Group_task;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.Adapter.memAdapter;
import com.aryomtech.dhitifoundation.cities.adapter.memberAdpater;
import com.aryomtech.dhitifoundation.cities.model.member_data;
import com.aryomtech.dhitifoundation.cities.onAgainClickInterface;
import com.aryomtech.dhitifoundation.cities.onClickInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import soup.neumorphism.NeumorphButton;
import www.sanju.motiontoast.MotionToast;

public class bsp_pay extends Fragment {

    View view;
    ImageView imageView;
    RecyclerView recyclerView;
    ArrayList<member_data> list;
    ArrayList<member_data> mylist;
    DatabaseReference users_ref;
    memAdapter memberAdapter;
    String mode_of_task="";
    NeumorphButton proceed;
    ArrayList<String> added_peoples_list;
    private onClickInterface onclickInterface;
    private com.aryomtech.dhitifoundation.cities.onAgainClickInterface onAgainClickInterface;
    SearchView searchView;
    CheckBox select_all;
    ProgressBar progressBar;
    ImageView filter_on,filter_off;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_bsp_pay, container, false);
        progressBar=view.findViewById(R.id.progressBar2);
        filter_on=view.findViewById(R.id.filter_on);
        filter_off=view.findViewById(R.id.filter_off);
        filter_on.setVisibility(View.GONE);
        filter_on.setOnClickListener(v->{
            Toast.makeText(getContext(), "Showing all.", Toast.LENGTH_SHORT).show();
            filter_off.setVisibility(View.VISIBLE);
            filter_on.setVisibility(View.GONE);
            onstart();
        });
        filter_off.setOnClickListener(v->{
            filter_guest();
            Toast.makeText(getContext(), "Showing members only.", Toast.LENGTH_SHORT).show();
            filter_off.setVisibility(View.GONE);
            filter_on.setVisibility(View.VISIBLE);
        });
        list=new ArrayList<>();
        mylist=new ArrayList<>();
        added_peoples_list=new ArrayList<>();
        imageView=view.findViewById(R.id.img);
        imageView.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.rv_bsp);
        proceed=view.findViewById(R.id.proceed);
        searchView=view.findViewById(R.id.searchView);
        select_all=view.findViewById(R.id.checkBox4);

        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);


        onclickInterface = position -> {
            added_peoples_list.add(list.get(position).getUid());
            String txt="Proceed "+"("+added_peoples_list.size()+")";
            proceed.setText(txt);
        };

        onAgainClickInterface=removePosition -> {
            added_peoples_list.remove(list.get(removePosition).getUid());
            String txt="Proceed "+"("+added_peoples_list.size()+")";
            proceed.setText(txt);
        };

        memberAdapter=new memAdapter(list,getContext(),"bilaspur",mode_of_task,onclickInterface,onAgainClickInterface);
        try {
            recyclerView.setAdapter(memberAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        memberAdapter.notifyDataSetChanged();

        select_all.setOnClickListener(v->{
            if (select_all.isChecked()){
                for (int i=0;i<list.size();i++){
                    added_peoples_list.add(list.get(i).getUid());
                }
                String txt="Proceed"+"("+added_peoples_list.size()+")";
                proceed.setText(txt);
                memberAdapter.selectAll();
                memberAdapter.notifyDataSetChanged();
            }
            else{
                added_peoples_list.clear();
                String txt="Proceed"+"("+added_peoples_list.size()+")";
                proceed.setText(txt);
                memberAdapter.unselectall();
                memberAdapter.notifyDataSetChanged();
            }
        });

        proceed.setOnClickListener(v->{
            if(added_peoples_list.size()>1){

                Assign_Group_task assign_group_task=new Assign_Group_task();
                Bundle args=new Bundle();
                args.putStringArrayList("uids_array_list",added_peoples_list);
                args.putString("location","bilaspur");
                assign_group_task.setArguments(args);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in,
                                R.anim.fade_out)
                        .add(R.id.drawer, assign_group_task)
                        .addToBackStack(null)
                        .commit();
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Empty ☹️",
                        "Please select at least 2 people!!",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
            }
        });

        mode_of_task= getContext().getSharedPreferences("mode_of_task_preference", Context.MODE_PRIVATE)
                .getString("789_mode_of_task","");

        if(mode_of_task.equals("private task")){
            proceed.setVisibility(View.GONE);
            select_all.setVisibility(View.GONE);
        }

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
        return  view;
    }
    private void onstart() {

        if(users_ref!=null){
            list.clear();
            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").exists()) {
                                if (Objects.equals(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").getValue(String.class)).toLowerCase(), "bilaspur")) {
                                    list.add(ds.getValue(member_data.class));
                                    imageView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        memberAdapter=new memAdapter(list,getContext(),"bilaspur",mode_of_task,onclickInterface,onAgainClickInterface);
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
    private void filter_guest(){
        if(users_ref!=null){
            list.clear();
            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for (DataSnapshot ds:snapshot.getChildren()){
                            if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").exists()) {
                                if (Objects.equals(Objects.requireNonNull(snapshot.child(Objects.requireNonNull(ds.getKey())).child("city").getValue(String.class)).toLowerCase(), "bilaspur")) {
                                    if(snapshot.child(ds.getKey()).child("identity").exists()){
                                        if (!Objects.equals(snapshot.child(ds.getKey()).child("identity").getValue(String.class), "guest")){
                                            list.add(ds.getValue(member_data.class));
                                            imageView.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                        memberAdapter=new memAdapter(list,getContext(),"bilaspur",mode_of_task,onclickInterface,onAgainClickInterface);
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
    private void Search(String s) {
        mylist.clear();
        for (member_data objects : list) {
            if (objects.getName().toLowerCase().contains(s.toLowerCase().trim()))
                mylist.add(objects);
        }
        memberAdapter = new memAdapter(mylist, getContext(), "bilaspur", mode_of_task, onclickInterface, onAgainClickInterface);
        try {
            recyclerView.setAdapter(memberAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        memberAdapter.notifyDataSetChanged();
    }
}