package com.aryomtech.dhitifoundation.Updates;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.Toast;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.Updates.Adapter.UpdateAdapter;
import com.aryomtech.dhitifoundation.Updates.model.notification_update;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class updates extends Fragment {

    View view;
    ArrayList<notification_update> list;
    RecyclerView recyclerView;
    ImageView imageBack,image_card;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    private Context contextNullSafe;
    DatabaseReference noti_ref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_updates, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_members);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        image_card=view.findViewById(R.id.imageView20);

        noti_ref= FirebaseDatabase.getInstance().getReference().child("Notifications");
        list=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

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
        new Handler(Looper.myLooper()).postDelayed(this::get_noti_data,1000);
        return view;
    }

    private void get_noti_data() {
        noti_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    try {
                        long total_notification = snapshot.getChildrenCount();
                        getContextNullSafety().getSharedPreferences("notification_count_seen", MODE_PRIVATE).edit()
                                .putLong("count_seen_notification4578", total_notification).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    image_card.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        list.add(ds.getValue(notification_update.class));
                    }
                    Collections.reverse(list);
                    UpdateAdapter updateAdapter=new UpdateAdapter(getContext(),list);
                    updateAdapter.notifyDataSetChanged();
                    try {
                        recyclerView.setAdapter(updateAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    image_card.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
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