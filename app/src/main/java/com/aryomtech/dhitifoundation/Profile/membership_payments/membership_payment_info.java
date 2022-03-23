package com.aryomtech.dhitifoundation.Profile.membership_payments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.Profile.Adapter.year_filter_Adapter;
import com.aryomtech.dhitifoundation.Profile.membership_payments.Adapter.membersAdapter;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.cities.onClickInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class membership_payment_info extends Fragment {

    View view;
    ArrayList<String> list,uid_list,name_list,dp_list,year_list,filtered_list;
    RecyclerView recyclerView,rv_years;
    ImageView imageBack,image_card;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    String search="";
    TextView title;
    private onClickInterface onClick;
    DatabaseReference membership_info,users_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_membership_payment_info, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_refferal);
        rv_years=view.findViewById(R.id.rv_years);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        image_card=view.findViewById(R.id.imageView20);
        title=view.findViewById(R.id.textView47);

        membership_info= FirebaseDatabase.getInstance().getReference().child("membership_payment");
        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        list=new ArrayList<>();
        uid_list=new ArrayList<>();
        name_list=new ArrayList<>();
        dp_list=new ArrayList<>();
        year_list=new ArrayList<>();
        filtered_list=new ArrayList<>();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        imageBack.setOnClickListener(v->back());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager1=new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_years.setItemViewCacheSize(20);
        rv_years.setLayoutManager(layoutManager1);

        onClick=removePosition -> {
            String filter = year_list.get(removePosition);
            if(search.equals("") || !search.equals(filter)){
                search=filter;
                title.setText(search);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                get_payments_card(filter);
            }
            else{
                search="";
                String t="Members paid";
                title.setText(t);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                get_name_dp(uid_list);
            }
        };

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

    private void get_payments_card(String filter) {
        filtered_list.clear();
        list.clear();
        membership_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0;i<uid_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.child(uid_list.get(i)).getChildren()){
                        String pay_date=snapshot.child(uid_list.get(i)).child(ds_key.getKey()).child("paid_on").getValue(String.class);
                        list.add(snapshot.child(uid_list.get(i)).getChildrenCount()+"");
                        String sliced_date=trim_date(pay_date);
                        if(sliced_date.equals(filter)){
                            if(!filtered_list.contains(uid_list)){
                                filtered_list.add(uid_list.get(i));
                                break;
                            }
                        }
                    }
                }
                Log.e("filtered == array ",filtered_list+"");
                get_name_dp(filtered_list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_members() {
        membership_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    uid_list.clear();
                    list.clear();
                    for (DataSnapshot ds:snapshot.getChildren()){
                        uid_list.add(ds.getKey());
                        list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).getChildrenCount()+"");
                    }
                    get_name_dp(uid_list);
                    get_date_of_payment();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_date_of_payment() {

        membership_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i=0;i<uid_list.size();i++){
                    for(DataSnapshot ds_key:snapshot.child(uid_list.get(i)).getChildren()){
                        String pay_date=snapshot.child(uid_list.get(i)).child(ds_key.getKey()).child("paid_on").getValue(String.class);
                        String sliced_date=trim_date(pay_date);
                        Log.e("sliced_date",sliced_date);
                        if(!year_list.contains(sliced_date)){
                            year_list.add(sliced_date);
                        }
                    }
                }
                year_filter_Adapter year_filter_adapter=new year_filter_Adapter(getContextNullSafety(),year_list,onClick);
                if(rv_years!=null)
                    rv_years.setAdapter(year_filter_adapter);
                Log.e("year_list",year_list+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private String trim_date(String str) {
        String[] str_arr=str.split(" ");
        return str_arr[2]+" "+str_arr[3];
    }

    private void get_name_dp(ArrayList<String> uid_list) {
        recyclerView.setVisibility(View.VISIBLE);
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
                membersAdapter membersAdapter=new membersAdapter(getContext(),list,name_list,dp_list,uid_list);
                membersAdapter.notifyDataSetChanged();
                try {
                    if(recyclerView!=null)
                        recyclerView.setAdapter(membersAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
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
    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}