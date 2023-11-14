package com.aryomtech.dhitifoundation.admin_panel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.model.set_form_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import soup.neumorphism.NeumorphCardView;


public class Register_cause extends Fragment {

    View view;
    private Context contextNullSafe;
    NeumorphCardView save;
    DatabaseReference fluid_Cards;
    ProgressBar progressBar;
    String key;
    FirebaseAuth auth;
    FirebaseUser user;
    String h1,h2,h3,h4,h5,h6,h7,h8,h9,h10;
    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8,txt9,txt10;
    EditText head1_edt,head2_edt,head3_edt,head4_edt,head5_edt,head6_edt,head7_edt,head8_edt,head9_edt,head10_edt;
    set_form_data set_form_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_register_cause, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        try {
            key = getArguments().getString("keys_for_creating_donation");
            h1=getArguments().getString("head1");
            h2=getArguments().getString("head2");
            h3=getArguments().getString("head3");
            h4=getArguments().getString("head4");
            h5=getArguments().getString("head5");
            h6=getArguments().getString("head6");
            h7=getArguments().getString("head7");
            h8=getArguments().getString("head8");
            h9=getArguments().getString("head9");
            h10=getArguments().getString("head10");
        } catch (Exception e) {
            e.printStackTrace();
        }
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        progressBar=view.findViewById(R.id.progressBar2);
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

        txt1=view.findViewById(R.id.textView11);
        txt2=view.findViewById(R.id.textView12);
        txt3=view.findViewById(R.id.textView13);
        txt4=view.findViewById(R.id.textView14);
        txt5=view.findViewById(R.id.textView15);
        txt6=view.findViewById(R.id.textView16);
        txt7=view.findViewById(R.id.textView17);
        txt8=view.findViewById(R.id.textView18);
        txt9=view.findViewById(R.id.textView19);
        txt10=view.findViewById(R.id.textView20);

        save=view.findViewById(R.id.save);

        if(h1!=null) {
            progressBar.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            head1_edt.setText(h1);
            head1_edt.setEnabled(false);
        }
        if(h2!=null) {
            head2_edt.setText(h2);
            head2_edt.setEnabled(false);
        }
        if(h3!=null) {
            head3_edt.setText(h3);
            head3_edt.setEnabled(false);
        }
        if(h4!=null) {
            head4_edt.setText(h4);
            head4_edt.setEnabled(false);
        }
        if(h5!=null) {
            head5_edt.setText(h5);
            head5_edt.setEnabled(false);
        }
        if(h6!=null) {
            head6_edt.setText(h6);
            head6_edt.setEnabled(false);
        }
        if(h7!=null) {
            head7_edt.setText(h7);
            head7_edt.setEnabled(false);
        }
        if(h8!=null) {
            head8_edt.setText(h8);
            head8_edt.setEnabled(false);
        }
        if(h9!=null) {
            head9_edt.setText(h9);
            head9_edt.setEnabled(false);
        }
        if(h10!=null) {
            head10_edt.setText(h10);
            head10_edt.setEnabled(false);
        }

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
                    progressBar.setVisibility(View.GONE);
                    if(snapshot.child("head1").exists()) {
                        if(!Objects.equals(snapshot.child("head1").getValue(String.class), "")) {
                            txt1.setText(snapshot.child("head1").getValue(String.class));
                            head1_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head2").exists()) {
                        if(!Objects.equals(snapshot.child("head2").getValue(String.class), "")) {
                            txt2.setText(snapshot.child("head2").getValue(String.class));
                            head2_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head3").exists()) {
                        if(!Objects.equals(snapshot.child("head3").getValue(String.class), "")) {
                            txt3.setText(snapshot.child("head3").getValue(String.class));
                            head3_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head4").exists()) {
                        if(!Objects.equals(snapshot.child("head4").getValue(String.class), "")) {
                            txt4.setText(snapshot.child("head4").getValue(String.class));
                            head4_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head5").exists()) {
                        if(!Objects.equals(snapshot.child("head5").getValue(String.class), "")) {
                            txt5.setText(snapshot.child("head5").getValue(String.class));
                            head5_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head6").exists()) {
                        if(!Objects.equals(snapshot.child("head6").getValue(String.class), "")) {
                            txt6.setText(snapshot.child("head6").getValue(String.class));
                            head6_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head7").exists()) {
                        if(!Objects.equals(snapshot.child("head7").getValue(String.class), "")) {
                            txt7.setText(snapshot.child("head7").getValue(String.class));
                            head7_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head8").exists()) {
                        if(!Objects.equals(snapshot.child("head8").getValue(String.class), "")) {
                            txt8.setText(snapshot.child("head8").getValue(String.class));
                            head8_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head9").exists()) {
                        if(!Objects.equals(snapshot.child("head9").getValue(String.class), "")) {
                            txt9.setText(snapshot.child("head9").getValue(String.class));
                            head9_edt.setVisibility(View.VISIBLE);
                        }
                    }
                    if(snapshot.child("head10").exists()) {
                        if(!Objects.equals(snapshot.child("head10").getValue(String.class), "")) {
                            txt10.setText(snapshot.child("head10").getValue(String.class));
                            head10_edt.setVisibility(View.VISIBLE);
                        }
                    }
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
        set_form_data.setUid(user.getUid());

        fluid_Cards.child(key).child("registrations").child(user.getUid()).setValue(set_form_data);
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