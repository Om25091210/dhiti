package com.aryomtech.dhitifoundation.admin_panel;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.topic;
import com.aryomtech.dhitifoundation.privateTask.ProgressBarAnimation;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;


public class Create_ManageFund extends Fragment {

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 110;
    public static final int REQUEST_CODE_SELECT_IMAGE = 200;
    EditText title,city,deadline_date_field,total_target,initial_amount
            ,goal,sub_heading_1,content_1,sub_heading_2,content_2;
    View view;
    ProgressBar progressBar;
    ImageView photo;
    String selected_path="";
    String identity="",name;
    Bitmap bitmap;
    Uri selectedImageUri;

    FirebaseAuth auth;
    EditText custom_1,custom_2,custom_3,custom_4,custom_5,custom_6;
    FirebaseUser user;
    ImageView imageSave,del;
    TextView textViewBottom2,total,deadline_date
            ,title_txt,target_txt,initial_amount_txt,donation
            ,target_txt_parent,raised_txt,initial_amount_txt_parent;
    boolean donation_enabled=true;
    int first=0;
    CardView donate;
    Dialog dialog;
    private Context contextNullSafe;
    DatabaseReference users_ref,card_ref,Notifications_ref;
    String title_args,image_link_args,check_donation_args
            ,location_args,date_or_msg_args,target_args,contributed_args,key
            ,goal_args,sub_heading_1_args,sub_heading_2_args,content_1_args,content_2_args,creator_of_card;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_create__manage_fund, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        photo=view.findViewById(R.id.sampleImageView);
        progressBar=view.findViewById(R.id.progressBar);
        textViewBottom2=view.findViewById(R.id.textViewBottom2);
        total=view.findViewById(R.id.total);
        title=view.findViewById(R.id.editTextTextMultiLine);
        deadline_date=view.findViewById(R.id.deadline_date);
        title_txt=view.findViewById(R.id.sampleTextView);
        city=view.findViewById(R.id.editTextTextMultiLine14);
        deadline_date_field=view.findViewById(R.id.editTextTextMultiLine15);
        total_target=view.findViewById(R.id.editTextTextMultiLine1);
        initial_amount=view.findViewById(R.id.editTextTextMultiLine13);
        target_txt=view.findViewById(R.id.textView109);
        initial_amount_txt=view.findViewById(R.id.textView98);
        imageSave=view.findViewById(R.id.imageSave);
        donation=view.findViewById(R.id.donation);
        target_txt_parent=view.findViewById(R.id.textView12);
        raised_txt=view.findViewById(R.id.txt);
        initial_amount_txt_parent=view.findViewById(R.id.textView103);
        donate=view.findViewById(R.id.donate);
        del=view.findViewById(R.id.del);
        goal=view.findViewById(R.id.field_goal);
        sub_heading_1=view.findViewById(R.id.editTextTextMultiLine16);
        content_1=view.findViewById(R.id.editTextTextMultiLine17);
        sub_heading_2=view.findViewById(R.id.editTextTextMultiLine18);
        content_2=view.findViewById(R.id.editTextTextMultiLine19);

        custom_1=view.findViewById(R.id.custom_1);
        custom_2=view.findViewById(R.id.custom_2);
        custom_3=view.findViewById(R.id.custom_3);
        custom_4=view.findViewById(R.id.custom_4);
        custom_5=view.findViewById(R.id.custom_5);
        custom_6=view.findViewById(R.id.custom_6);

        card_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        Notifications_ref= FirebaseDatabase.getInstance().getReference().child("Notifications");
        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.child("name").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        target_txt_parent.setVisibility(View.GONE);
        total_target.setVisibility(View.GONE);
        target_txt.setVisibility(View.GONE);
        initial_amount_txt.setVisibility(View.GONE);
        raised_txt.setVisibility(View.GONE);
        initial_amount_txt_parent.setVisibility(View.GONE);
        initial_amount.setVisibility(View.GONE);
        donate.setVisibility(View.GONE);

        try {
            image_link_args = getArguments().getString("sending_image");
            title_args = getArguments().getString("sending_title");
            check_donation_args = getArguments().getString("check_for_donation");
            location_args = getArguments().getString("sending_location");
            date_or_msg_args = getArguments().getString("sending_date-or-msg");
            target_args = getArguments().getString("sending_target_amount");
            contributed_args = getArguments().getString("sending_contributed");
            key = getArguments().getString("sending_key");
            goal_args = getArguments().getString("sending_goal");
            sub_heading_1_args=getArguments().getString("sending_sub_heading_101");
            sub_heading_2_args=getArguments().getString("sending_sub_heading_202");
            content_1_args=getArguments().getString("sending_content_101");
            content_2_args=getArguments().getString("sending_content_202");
            creator_of_card=getArguments().getString("sending_uid_of_creator");
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(image_link_args!=null) {
            try {
                Glide.with(getContextNullSafety()).load(image_link_args)
                        .placeholder(R.drawable.ic_image_holder)
                        .into(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(goal_args!=null){
            if(!goal_args.equals(""))
                goal.setText(goal_args);
        }
        if(sub_heading_1_args!=null){
            if (!sub_heading_1_args.equals(""))
                sub_heading_1.setText(sub_heading_1_args);
        }
        if (sub_heading_2_args!=null){
            if (!sub_heading_2_args.equals(""))
                sub_heading_2.setText(sub_heading_2_args);
        }
        if (content_1_args!=null){
            if (!content_1_args.equals(""))
                content_1.setText(content_1_args);
        }
        if (content_2_args!=null){
            if (!content_2_args.equals(""))
                content_2.setText(content_2_args);
        }
        if(title_args!=null) {
            title.setText(title_args);
            title_txt.setText(title_args);
        }
        if (check_donation_args!=null){
            first=1;
            if(check_donation_args.equals("enabled")){
                donation_enabled=false;
                String text="Donation enabled";
                donation.setText(text);
                target_txt_parent.setVisibility(View.VISIBLE);
                total_target.setVisibility(View.VISIBLE);
                target_txt.setVisibility(View.VISIBLE);
                initial_amount_txt.setVisibility(View.VISIBLE);
                raised_txt.setVisibility(View.VISIBLE);
                initial_amount_txt_parent.setVisibility(View.VISIBLE);
                initial_amount.setVisibility(View.VISIBLE);
                donate.setVisibility(View.VISIBLE);
            }
            else if(check_donation_args.equals("disabled")){
                donation_enabled=true;
                String text="Enable donation";
                donation.setText(text);
                target_txt_parent.setVisibility(View.GONE);
                total_target.setVisibility(View.GONE);
                target_txt.setVisibility(View.GONE);
                initial_amount_txt.setVisibility(View.GONE);
                raised_txt.setVisibility(View.GONE);
                initial_amount_txt_parent.setVisibility(View.GONE);
                initial_amount.setVisibility(View.GONE);
                donate.setVisibility(View.GONE);
            }
        }
        if (location_args!=null){
            city.setText(location_args);
            textViewBottom2.setText(location_args);
        }
        if (date_or_msg_args!=null){
            deadline_date_field.setText(date_or_msg_args);
            deadline_date.setText(date_or_msg_args);
        }
        if (target_args!=null){
            total_target.setText(target_args);
            String text="₹ "+target_args;
            target_txt.setText(text);
        }
        if (contributed_args!=null){
            initial_amount.setText(contributed_args);
            String text="₹ "+contributed_args;
            initial_amount_txt.setText(text);
        }

        if (target_args!=null && contributed_args!=null){
            float per=(float) (Integer.parseInt(contributed_args)*100)/Integer.parseInt(target_args);
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, per);
            anim.setDuration(1000);
            progressBar.startAnimation(anim);
        }
        else {
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, 75f);
            anim.setDuration(1000);
            progressBar.startAnimation(anim);
        }

        view.findViewById(R.id.image).setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(getContextNullSafety().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });

        donation.setOnClickListener(v->{
            if (first==0){
                first=1;
                donation_enabled=false;
                String text="Donation enabled";
                donation.setText(text);
                target_txt_parent.setVisibility(View.VISIBLE);
                total_target.setVisibility(View.VISIBLE);
                target_txt.setVisibility(View.VISIBLE);
                initial_amount_txt.setVisibility(View.VISIBLE);
                raised_txt.setVisibility(View.VISIBLE);
                initial_amount_txt_parent.setVisibility(View.VISIBLE);
                initial_amount.setVisibility(View.VISIBLE);
                donate.setVisibility(View.VISIBLE);
            }
            else if (donation_enabled){
                donation_enabled=false;
                String text="Donation enabled";
                donation.setText(text);
                target_txt_parent.setVisibility(View.VISIBLE);
                total_target.setVisibility(View.VISIBLE);
                target_txt.setVisibility(View.VISIBLE);
                initial_amount_txt.setVisibility(View.VISIBLE);
                raised_txt.setVisibility(View.VISIBLE);
                initial_amount_txt_parent.setVisibility(View.VISIBLE);
                initial_amount.setVisibility(View.VISIBLE);
                donate.setVisibility(View.VISIBLE);
            }else{
                donation_enabled=true;
                String text="Enable donation";
                donation.setText(text);
                target_txt_parent.setVisibility(View.GONE);
                total_target.setVisibility(View.GONE);
                target_txt.setVisibility(View.GONE);
                initial_amount_txt.setVisibility(View.GONE);
                raised_txt.setVisibility(View.GONE);
                initial_amount_txt_parent.setVisibility(View.GONE);
                initial_amount.setVisibility(View.GONE);
                donate.setVisibility(View.GONE);
            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                title_txt.setText(editable);
            }
        });
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                textViewBottom2.setText(editable);
            }
        });
        deadline_date_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                    deadline_date.setText(editable);
            }
        });
        total_target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String text="₹ "+editable;
                target_txt.setText(text);
            }
        });
        initial_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String text="₹ "+editable;
                initial_amount_txt.setText(text);
            }
        });
        imageSave.setVisibility(View.GONE);
        del.setVisibility(View.GONE);
        if(creator_of_card!=null){
            if (!creator_of_card.equals(user.getUid())){
                if(identity.equals("admin")){
                    imageSave.setVisibility(View.GONE);
                    del.setVisibility(View.VISIBLE);
                }
            }
            else{
                imageSave.setVisibility(View.VISIBLE);
                del.setVisibility(View.VISIBLE);
            }
        }
        else{
            imageSave.setVisibility(View.VISIBLE);
            del.setVisibility(View.VISIBLE);
        }
        if (image_link_args!=null)
            del.setVisibility(View.VISIBLE);
        else
            del.setVisibility(View.GONE);

        imageSave.setOnClickListener(v-> save());
        view.findViewById(R.id.imageBack).setOnClickListener(v->back());
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
        del.setOnClickListener(v->delete());
        get_customs();

        return view;
    }

    private void get_customs() {
        if(key!=null) {
            card_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("custom_1").exists())
                        custom_1.setText(snapshot.child("custom_1").getValue(String.class));
                    if(snapshot.child("custom_2").exists())
                        custom_2.setText(snapshot.child("custom_2").getValue(String.class));
                    if(snapshot.child("custom_3").exists())
                        custom_3.setText(snapshot.child("custom_3").getValue(String.class));
                    if(snapshot.child("custom_4").exists())
                        custom_4.setText(snapshot.child("custom_4").getValue(String.class));
                    if(snapshot.child("custom_5").exists())
                        custom_5.setText(snapshot.child("custom_5").getValue(String.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }


    private void delete() {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_for_sure);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.textView96);
        TextView yes=dialog.findViewById(R.id.textView95);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(v-> dialog.dismiss());
        yes.setOnClickListener(v-> {
            card_ref.child(key).removeValue();
            String path = "fluidEvent/" + key + "_cardimg" + ".png";
            String path_original = "fluidEvent/" + key + "_cardimg_original" + ".png";
            if (image_link_args != null) {
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReference().child(path);
                storageReference.delete();
                StorageReference storageReference_original =
                        FirebaseStorage.getInstance().getReference().child(path_original);
                storageReference_original.delete();
                dialog.dismiss();
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Success",
                        "Deleted successfully.",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        });

    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
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
    private void save() {
        String pushkey;
        if (key != null)
            pushkey = key;
        else
            pushkey = card_ref.push().getKey();

        if (!(content_1.getText().toString().trim().equals("")
                && !sub_heading_1.getText().toString().trim().equals(""))) {

            if (!(content_2.getText().toString().trim().equals("")
                    && !sub_heading_2.getText().toString().trim().equals(""))) {

                if (!goal.getText().toString().trim().equals("")) {
                    if (!title.getText().toString().trim().equals("")) {
                        if (!selected_path.trim().equals("") || image_link_args != null) {
                            if (!city.getText().toString().trim().equals("")) {
                                if (!donation_enabled) {
                                    if (!total_target.getText().toString().trim().equals("")) {
                                        if (!initial_amount.getText().toString().trim().equals("")) {
                                            assert pushkey != null;
                                            dialog = new Dialog(requireContext());
                                            dialog.setCancelable(false);
                                            dialog.setContentView(R.layout.loading_dialog);
                                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                                            lottieAnimationView.setAnimation("uploaded.json");
                                            dialog.show();

                                            String noti_pushkey=Notifications_ref.push().getKey();
                                            Calendar cal = Calendar.getInstance();
                                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                            cal.add(Calendar.DAY_OF_MONTH,2);

                                            Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("title").setValue("New cause");
                                            Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("delete_on").setValue(simpleDateFormat.format(cal.getTime())+"");
                                            Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("creator").setValue(user.getUid());
                                            Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("body").setValue(title.getText().toString().trim());

                                            if(creator_of_card!=null){
                                                if (creator_of_card.equals(user.getUid())) {
                                                    if (name != null) {
                                                        topic topic = new topic();
                                                        topic.noti(name, "edited cause - " + title.getText().toString().trim());
                                                    }
                                                }
                                            }
                                            else {
                                                topic topic = new topic();
                                                topic.noti("New Cause", title.getText().toString().trim());
                                            }
                                            card_ref.child(pushkey).child("title").setValue(title.getText().toString().trim());
                                            card_ref.child(pushkey).child("location").setValue(city.getText().toString().trim());
                                            card_ref.child(pushkey).child("date_or_msg").setValue(deadline_date_field.getText().toString().trim());
                                            card_ref.child(pushkey).child("donation").setValue("enabled");
                                            card_ref.child(pushkey).child("key").setValue(pushkey);

                                            card_ref.child(pushkey).child("goal").setValue(goal.getText().toString().trim());
                                            card_ref.child(pushkey).child("sub_heading_1").setValue(sub_heading_1.getText().toString().trim());
                                            card_ref.child(pushkey).child("content_1").setValue(content_1.getText().toString().trim());

                                            card_ref.child(pushkey).child("sub_heading_2").setValue(sub_heading_2.getText().toString().trim());
                                            card_ref.child(pushkey).child("content_2").setValue(content_2.getText().toString().trim());

                                            card_ref.child(pushkey).child("uid").setValue(user.getUid());
                                            card_ref.child(pushkey).child("target").setValue(total_target.getText().toString().trim());
                                            card_ref.child(pushkey).child("contributed").setValue(initial_amount.getText().toString().trim());

                                            if(!custom_1.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_1").setValue(custom_1.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_1").removeValue();
                                            if(!custom_2.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_2").setValue(custom_2.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_2").removeValue();
                                            if(!custom_3.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_3").setValue(custom_3.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_3").removeValue();
                                            if(!custom_4.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_4").setValue(custom_4.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_4").removeValue();
                                            if(!custom_5.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_5").setValue(custom_5.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_5").removeValue();
                                            if(!custom_6.getText().toString().equals(""))
                                                card_ref.child(pushkey).child("custom_6").setValue(custom_6.getText().toString());
                                            else
                                                card_ref.child(pushkey).child("custom_6").removeValue();

                                            if (image_link_args == null) {
                                                String imagepath = "fluidEvent/" + pushkey + "_cardimg" + ".png";
                                                String imagepath_original = "fluidEvent/" + pushkey + "_cardimg_original" + ".png";

                                                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                                                /*Bitmap bitmap_up = BitmapFactory.decodeFile(selected_path);
                                                final String randomKey = UUID.randomUUID().toString();
                                                String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

                                                try {
                                                    InputStream stream = new FileInputStream(new File(selected_path));

                                                    storageReference.putStream(stream)
                                                            .addOnSuccessListener(taskSnapshot ->
                                                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                            task -> {
                                                                                String image_link = Objects.requireNonNull(task.getResult()).toString();
                                                                                card_ref.child(pushkey).child("image_link").setValue(image_link);

                                                                                StorageReference storageReference_original = FirebaseStorage.getInstance().getReference().child(imagepath_original);
                                                                                final String randomKey_original = UUID.randomUUID().toString();
                                                                                String path_original = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap, "" + randomKey_original, null);
                                                                                storageReference_original.putFile(Uri.parse(path_original))
                                                                                        .addOnSuccessListener(taskSnapshot1 ->
                                                                                                taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                                                        task1 -> {
                                                                                                            String image_link_original = Objects.requireNonNull(task1.getResult()).toString();
                                                                                                            card_ref.child(pushkey).child("image_link_original").setValue(image_link_original);

                                                                                                            dialog.dismiss();
                                                                                                            Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                                                                            FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                                                                                            FragmentTransaction ft = fm.beginTransaction();
                                                                                                            if (fm.getBackStackEntryCount() > 0) {
                                                                                                                fm.popBackStack();
                                                                                                            }
                                                                                                            ft.commit();

                                                                                                        }));

                                                                            }));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                card_ref.child(pushkey).child("image_link").setValue(image_link_args);
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), "Edited successfully", Toast.LENGTH_SHORT).show();
                                                FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                                FragmentTransaction ft = fm.beginTransaction();
                                                if (fm.getBackStackEntryCount() > 0) {
                                                    fm.popBackStack();
                                                }
                                                ft.commit();
                                            }
                                        } else {
                                            MotionToast.Companion.darkColorToast(getActivity(),
                                                    "Empty",
                                                    "Add amount contributed till now.",
                                                    MotionToast.TOAST_WARNING,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.SHORT_DURATION,
                                                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                        }
                                    } else {
                                        MotionToast.Companion.darkColorToast(getActivity(),
                                                "Empty",
                                                "Add target amount.",
                                                MotionToast.TOAST_WARNING,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.SHORT_DURATION,
                                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                    }
                                } else {
                                    assert pushkey != null;
                                    dialog = new Dialog(requireContext());
                                    dialog.setCancelable(false);
                                    dialog.setContentView(R.layout.loading_dialog);
                                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                                    lottieAnimationView.setAnimation("uploaded.json");
                                    dialog.show();

                                    String noti_pushkey=Notifications_ref.push().getKey();
                                    Calendar cal = Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    cal.add(Calendar.DAY_OF_MONTH,2);

                                    Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("title").setValue("New cause");
                                    Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("delete_on").setValue(simpleDateFormat.format(cal.getTime())+"");
                                    Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("creator").setValue(user.getUid());
                                    Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("body").setValue(title.getText().toString().trim());

                                    topic topic=new topic();
                                    topic.noti("New Cause",title.getText().toString().trim());

                                    card_ref.child(pushkey).child("title").setValue(title.getText().toString().trim());
                                    card_ref.child(pushkey).child("location").setValue(city.getText().toString().trim());
                                    card_ref.child(pushkey).child("date_or_msg").setValue(deadline_date_field.getText().toString().trim());
                                    card_ref.child(pushkey).child("donation").setValue("disabled");
                                    if (check_donation_args != null) {
                                        card_ref.child(pushkey).child("contributed").removeValue();
                                        card_ref.child(pushkey).child("target").removeValue();
                                    }
                                    card_ref.child(pushkey).child("key").setValue(pushkey);

                                    card_ref.child(pushkey).child("goal").setValue(goal.getText().toString().trim());
                                    card_ref.child(pushkey).child("sub_heading_1").setValue(sub_heading_1.getText().toString().trim());
                                    card_ref.child(pushkey).child("content_1").setValue(content_1.getText().toString().trim());

                                    card_ref.child(pushkey).child("sub_heading_2").setValue(sub_heading_2.getText().toString().trim());
                                    card_ref.child(pushkey).child("content_2").setValue(content_2.getText().toString().trim());

                                    card_ref.child(pushkey).child("uid").setValue(user.getUid());

                                    if(!custom_1.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_1").setValue(custom_1.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_1").removeValue();
                                    if(!custom_2.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_2").setValue(custom_2.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_2").removeValue();
                                    if(!custom_3.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_3").setValue(custom_3.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_3").removeValue();
                                    if(!custom_4.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_4").setValue(custom_4.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_4").removeValue();
                                    if(!custom_5.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_5").setValue(custom_5.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_5").removeValue();
                                    if(!custom_6.getText().toString().equals(""))
                                        card_ref.child(pushkey).child("custom_6").setValue(custom_6.getText().toString());
                                    else
                                        card_ref.child(pushkey).child("custom_6").removeValue();
                                    
                                    if (image_link_args == null) {
                                        String imagepath = "fluidEvent/" + pushkey + "_cardimg" + ".png";
                                        String imagepath_original = "fluidEvent/" + pushkey + "_cardimg_original" + ".png";

                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                                        /*Bitmap bitmap_up = BitmapFactory.decodeFile(selected_path);
                                        final String randomKey = UUID.randomUUID().toString();
                                        String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

                                        try {
                                            InputStream stream = new FileInputStream(new File(selected_path));

                                            storageReference.putStream(stream)
                                                    .addOnSuccessListener(taskSnapshot ->
                                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                    task -> {
                                                                        String image_link = Objects.requireNonNull(task.getResult()).toString();
                                                                        card_ref.child(pushkey).child("image_link").setValue(image_link);
                                                                        StorageReference storageReference_original = FirebaseStorage.getInstance().getReference().child(imagepath_original);
                                                                        final String randomKey_original = UUID.randomUUID().toString();
                                                                        String path_original = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap, "" + randomKey_original, null);
                                                                        storageReference_original.putFile(Uri.parse(path_original))
                                                                                .addOnSuccessListener(taskSnapshot1 ->
                                                                                        taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                                                task1 -> {
                                                                                                    String image_link_original = Objects.requireNonNull(task1.getResult()).toString();
                                                                                                    card_ref.child(pushkey).child("image_link_original").setValue(image_link_original);
                                                                                                    if (getActivity() != null && !getActivity().isFinishing()) {
                                                                                                        dialog.dismiss();
                                                                                                    }
                                                                                                    Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                                                                    FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                                                                                    FragmentTransaction ft = fm.beginTransaction();
                                                                                                    if (fm.getBackStackEntryCount() > 0) {
                                                                                                        fm.popBackStack();
                                                                                                    }
                                                                                                    ft.commit();

                                                                                                }));
                                                                    }));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        card_ref.child(pushkey).child("image_link").setValue(image_link_args);
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Edited successfully", Toast.LENGTH_SHORT).show();
                                        FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        if (fm.getBackStackEntryCount() > 0) {
                                            fm.popBackStack();
                                        }
                                        ft.commit();
                                    }
                                }
                            } else {
                                MotionToast.Companion.darkColorToast(getActivity(),
                                        "Empty",
                                        "Add location.",
                                        MotionToast.TOAST_WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                            }
                        } else {
                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Empty",
                                    "Add an image.",
                                    MotionToast.TOAST_WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                        }
                    } else {
                        MotionToast.Companion.darkColorToast(getActivity(),
                                "Empty",
                                "Title is empty.",
                                MotionToast.TOAST_WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                    }
                } else {
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Empty",
                            "Set a Goal.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
            } else {
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Empty Content",
                        "you cannot proceed with just sub heading and no content.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Empty Content",
                    "you cannot proceed with just sub heading and no content.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        }
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    addImageNote(selectedImageUri);
                }
            }
        }
    }
    private void addImageNote(Uri imageUri){
        try {
            InputStream inputStream = getContextNullSafety().getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
            selected_path=compressImage(imageUri+"");
            photo.setImageBitmap(BitmapFactory.decodeFile(selected_path));
            image_link_args=null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            assert scaledBitmap != null;
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(getContextNullSafety().getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContextNullSafety().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}