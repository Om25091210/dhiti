package com.aryomtech.dhitifoundation.Profile.membership_payments.show_transactions;

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

import com.aryomtech.dhitifoundation.Profile.membership_payments.Adapter.transactionAdapter;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.card_donation_history_Adapter;
import com.aryomtech.dhitifoundation.admin_panel.model.card_donation_history_data;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import soup.neumorphism.NeumorphCardView;


public class mem_transactions extends Fragment {

    View view;
    String uid_ref,name_ref;
    ArrayList<card_donation_history_data> list;
    RecyclerView recyclerView;
    ImageView imageBack,image_card;
    ProgressBar progressBar;
    FirebaseAuth auth;
    NeumorphCardView download_card;
    FirebaseUser user;
    DatabaseReference membership_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_mem_transactions, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        recyclerView=view.findViewById(R.id.rv_members);
        progressBar=view.findViewById(R.id.progressBar2);
        imageBack=view.findViewById(R.id.imageBack);
        image_card=view.findViewById(R.id.imageView20);
        download_card=view.findViewById(R.id.download_card);

        membership_ref= FirebaseDatabase.getInstance().getReference().child("membership_payment");
        list=new ArrayList<>();
        try {
            uid_ref = getArguments().getString("sending_uid_6580")+"";
            name_ref = getArguments().getString("sending_name_6548256")+"";
        } catch (Exception e) {
            e.printStackTrace();
        }
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        download_card.setOnClickListener(v->
                PdfGenerator.getBuilder()
                .setContext(getContextNullSafety())
                .fromViewSource()
                .fromView(recyclerView)
                .setFileName("dhiti_membership_payments-PDF")
                .setFolderName("dhiti-PDF-folder")
                .openPDFafterGeneration(true)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                    }

                    @Override
                    public void showLog(String log) {
                        super.showLog(log);
                    }

                    @Override
                    public void onStartPDFGeneration() {
                        /*When PDF generation begins to start*/
                    }

                    @Override
                    public void onFinishPDFGeneration() {
                        /*When PDF generation is finished*/
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                    }
                }));
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
    private void get_members() {
        membership_ref.child(uid_ref).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                image_card.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                for (DataSnapshot ds:snapshot.getChildren()){
                    list.add(ds.getValue(card_donation_history_data.class));
                }
                Collections.reverse(list);
                transactionAdapter transactionAdapter=new transactionAdapter(getContext(),list);
                transactionAdapter.notifyDataSetChanged();
                try {
                    recyclerView.setAdapter(transactionAdapter);
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