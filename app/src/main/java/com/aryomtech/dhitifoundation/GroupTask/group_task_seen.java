package com.aryomtech.dhitifoundation.GroupTask;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aryomtech.dhitifoundation.GroupTask.Adapter.ViewmemAdapter;
import com.aryomtech.dhitifoundation.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class group_task_seen extends Fragment {

    View view;
    ArrayList<String> members_list;
    ArrayList<String> dp_list;
    ArrayList<String> name_list;
    ArrayList<String> part_list;
    RecyclerView recyclerView;
    ImageView imageBack;
    ProgressBar progressBar;
    DatabaseReference users_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_group_task_seen, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_members);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        try {
            members_list = getArguments().getStringArrayList("seen_members_list");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dp_list=new ArrayList<>();
        name_list=new ArrayList<>();
        part_list=new ArrayList<>();

        imageBack.setOnClickListener(v->back());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
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
    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
    private void get_members() {
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                for (int i=0;i<members_list.size();i++){
                    String name=snapshot.child(members_list.get(i)).child("name").getValue(String.class);
                    name_list.add(name);
                    if (snapshot.child(members_list.get(i)).child("dplink").exists()) {
                        String dp = snapshot.child(members_list.get(i)).child("dplink").getValue(String.class);
                        dp_list.add(dp);
                    }
                    else
                        dp_list.add("");
                    if (snapshot.child(members_list.get(i)).child("part").exists()){
                        String part = snapshot.child(members_list.get(i)).child("part").getValue(String.class);
                        part_list.add(part);
                    }
                    else
                        part_list.add("");

                }
                ViewmemAdapter viewmemAdapter=new ViewmemAdapter(getContext(),dp_list,name_list,part_list);
                viewmemAdapter.notifyDataSetChanged();
                try {
                    recyclerView.setAdapter(viewmemAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                freeMemory();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
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
    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}