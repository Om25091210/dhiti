package com.aryomtech.dhitifoundation.admin_panel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.model.set_form_data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class reg_form extends Fragment {

    View view;
    LottieAnimationView save;
    DatabaseReference fluid_Cards;
    String key;
    EditText head1_edt,head2_edt,head3_edt,head4_edt,head5_edt,head6_edt,head7_edt,head8_edt,head9_edt,head10_edt;
    private Context contextNullSafe;
    set_form_data set_form_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_reg_form, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        try {
            key = getArguments().getString("keys_for_creating_donation");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fluid_Cards= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");

        head1_edt=view.findViewById(R.id.editTextTextMultiLine);
        head2_edt=view.findViewById(R.id.editTextTextMultiLine2);
        head3_edt=view.findViewById(R.id.editTextTextMultiLine4);
        head4_edt=view.findViewById(R.id.editTextTextMultiLine6);
        head5_edt=view.findViewById(R.id.editTextTextMultiLine8);
        head6_edt=view.findViewById(R.id.editTextTextMultiLine10);
        head7_edt=view.findViewById(R.id.editTextTextMultiLine189);
        head8_edt=view.findViewById(R.id.editTextTextMultiLine191);
        head9_edt=view.findViewById(R.id.editTextTextMultiLine193);
        head10_edt=view.findViewById(R.id.editTextTextMultiLine195);
        save=view.findViewById(R.id.save);
        set_form_data=new set_form_data();
        save.setOnClickListener(v->{
            Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vi-> dialog.dismiss());
            yes.setOnClickListener(vi-> {
                save();
                dialog.dismiss();
                back();
            });
        });
        get_data();
        view.findViewById(R.id.back).setOnClickListener(v->back());
        return view;
    }

    private void get_data() {
        fluid_Cards.child(key).child("form_data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("head1").exists())
                        head1_edt.setText(snapshot.child("head1").getValue(String.class));
                    if(snapshot.child("head2").exists())
                        head2_edt.setText(snapshot.child("head2").getValue(String.class));
                    if(snapshot.child("head3").exists())
                        head3_edt.setText(snapshot.child("head3").getValue(String.class));
                    if(snapshot.child("head4").exists())
                        head4_edt.setText(snapshot.child("head4").getValue(String.class));
                    if(snapshot.child("head5").exists())
                        head5_edt.setText(snapshot.child("head5").getValue(String.class));
                    if(snapshot.child("head6").exists())
                        head6_edt.setText(snapshot.child("head6").getValue(String.class));
                    if(snapshot.child("head7").exists())
                        head7_edt.setText(snapshot.child("head7").getValue(String.class));
                    if(snapshot.child("head8").exists())
                        head8_edt.setText(snapshot.child("head8").getValue(String.class));
                    if(snapshot.child("head9").exists())
                        head9_edt.setText(snapshot.child("head9").getValue(String.class));
                    if(snapshot.child("head10").exists())
                        head10_edt.setText(snapshot.child("head10").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void save() {
        set_form_data.setHead1(head1_edt.getText().toString().trim());
        set_form_data.setHead2(head2_edt.getText().toString().trim());
        set_form_data.setHead3(head3_edt.getText().toString().trim());
        set_form_data.setHead4(head4_edt.getText().toString().trim());
        set_form_data.setHead5(head5_edt.getText().toString().trim());
        set_form_data.setHead6(head6_edt.getText().toString().trim());
        set_form_data.setHead7(head7_edt.getText().toString().trim());
        set_form_data.setHead8(head8_edt.getText().toString().trim());
        set_form_data.setHead9(head9_edt.getText().toString().trim());
        set_form_data.setHead10(head10_edt.getText().toString().trim());

        fluid_Cards.child(key).child("form_data").setValue(set_form_data);
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