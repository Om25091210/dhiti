package com.aryomtech.dhitifoundation.admin_panel.request_manual_fee;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aryomtech.dhitifoundation.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;


public class pay_request extends Fragment {

    String uid,dp,name;
    SimpleDraweeView circleImageView;
    View view;
    Button request,paid;
    TextView name_txt;
    EditText amount_fee;
    DatabaseReference reference;
    private Context contextNullSafe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_pay_request, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        circleImageView=view.findViewById(R.id.circleImageView);

        uid=getArguments().getString("uid_request");
        dp=getArguments().getString("dp_request");
        name=getArguments().getString("name_request");

        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        name_txt=view.findViewById(R.id.textView9);
        request=view.findViewById(R.id.button4);
        paid=view.findViewById(R.id.button5);
        amount_fee=view.findViewById(R.id.editTextTextMultiLine);

        reference=FirebaseDatabase.getInstance().getReference().child("users");

        amount_fee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(amount_fee.getText().toString().equals("")) {
                    request.setBackgroundResource(R.drawable.pay_bg_2);
                    paid.setBackgroundResource(R.drawable.pay_bg_2);
                }
                else {
                    request.setBackgroundResource(R.drawable.pay_bg);
                    paid.setBackgroundResource(R.drawable.pay_bg);
                }
            }
        });
        request.setOnClickListener(v->{
            request_fee();
        });
        paid.setOnClickListener(v->{
            paid_fee();
        });
        name_txt.setText(name+"");

        if(dp!=null) {
            Uri uri = Uri.parse(dp);
            ImageRequest request = ImageRequest.fromUri(uri);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(circleImageView.getController()).build();

            circleImageView.setController(controller);
        }

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        return view;
    }

    private void paid_fee() {
        if(!amount_fee.getText().toString().equals("")){
            reference.child(uid).child("membership_info").child("due").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Long due=snapshot.getValue(Long.class);
                        due=due-Long.parseLong(amount_fee.getText().toString());
                        reference.child(uid).child("membership_info").child("due").setValue(due);
                        if(due==0L) {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            cal.add(Calendar.MONTH, 1);
                            cal.add(Calendar.DAY_OF_MONTH, 3);
                            reference.child(uid).child("membership_info").child("next_date").setValue(simpleDateFormat.format(cal.getTime()) + "");
                        }
                        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }

                        ft.commit();

                    }
                    else{
                        reference.child(uid).child("membership_info").child("due").setValue(-Long.parseLong(amount_fee.getText().toString()));

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        cal.add(Calendar.MONTH, 1);
                        cal.add(Calendar.DAY_OF_MONTH, 3);
                        reference.child(uid).child("membership_info").child("next_date").setValue(simpleDateFormat.format(cal.getTime()) + "");

                        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }

                        ft.commit();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }else {
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Amount Empty",
                    "Please enter amount to request fee",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        }
    }

    private void request_fee() {
        if(!amount_fee.getText().toString().equals("")){
            reference.child(uid).child("membership_info").child("due").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Long due=snapshot.getValue(Long.class);
                        due=due+Long.parseLong(amount_fee.getText().toString());
                        reference.child(uid).child("membership_info").child("due").setValue(due);

                        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }

                        ft.commit();

                    }
                    else{
                        reference.child(uid).child("membership_info").child("due").setValue(Long.parseLong(amount_fee.getText().toString()));

                        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                        FragmentTransaction ft=fm.beginTransaction();
                        if(fm.getBackStackEntryCount()>0) {
                            fm.popBackStack();
                        }

                        ft.commit();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }else {
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Amount Empty",
                    "Please enter amount to request fee",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        }
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
}