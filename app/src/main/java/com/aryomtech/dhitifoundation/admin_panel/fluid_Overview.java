package com.aryomtech.dhitifoundation.admin_panel;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.donating_other.creating_donation;
import com.aryomtech.dhitifoundation.admin_panel.donating_other.donation_Adapter;
import com.aryomtech.dhitifoundation.admin_panel.donating_other.donations_data;
import com.aryomtech.dhitifoundation.admin_panel.raised_by_share.refferal;
import com.aryomtech.dhitifoundation.donation_checkout;
import com.aryomtech.dhitifoundation.privateTask.ProgressBarAnimation;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import soup.neumorphism.NeumorphCardView;


public class fluid_Overview extends Fragment {

    View view;
    Long amount=0L;
    DatabaseReference fluid_ref,users_ref,donation_ref;
    //ImageView image;
    FirebaseAuth auth;
    String identity="";
    LottieAnimationView not_found;
    FirebaseUser user;
    ProgressBar progressBar;
    ConstraintLayout donate;
    EditText search;
    donation_Adapter donation_adapter;
    Button donate_but;
    LinearLayout linearLayout;
    NeumorphCardView search_neumorph;
    RecyclerView rv_donation;
    List<donations_data> mylist= new ArrayList<>();
    List<donations_data> list= new ArrayList<>();
    private Context contextNullSafe;
    String c_1,c_2,c_3,c_4,c_5,c_6;
    int sum=0;
    SimpleDraweeView draweeView;
    TextView msg_custom,cause_1,cause_2,cause_3,cause_4,cause_5,cause_6;
    TextView goal_txt,contributed_txt,target_txt,supporters_txt,raised_by_share
            ,sub_heading_1,content_1,sub_heading_2,content_2,location,date_or_msg,enable_donations,donating;
    String title_args,image_link_args,check_donation_args
            ,location_args,date_or_msg_args,target_args,contributed_args,key,deep_link_uid_value
            ,goal_args,sub_heading_1_args,sub_heading_2_args,content_1_args,content_2_args;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fluid__overview, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        fluid_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        draweeView  = view.findViewById(R.id.sampleImageView);
       // image=view.findViewById(R.id.sampleImageView);
        not_found=view.findViewById(R.id.not_found);
        goal_txt=view.findViewById(R.id.textView115);
        contributed_txt=view.findViewById(R.id.textView98);
        target_txt=view.findViewById(R.id.textView109);
        supporters_txt=view.findViewById(R.id.textView118);
        sub_heading_1=view.findViewById(R.id.textView116);
        content_1=view.findViewById(R.id.textView117);
        sub_heading_2=view.findViewById(R.id.about);
        content_2=view.findViewById(R.id.txtabout);
        progressBar=view.findViewById(R.id.progressBar);
        location=view.findViewById(R.id.textView120);
        date_or_msg=view.findViewById(R.id.date_or_msg);
        linearLayout=view.findViewById(R.id.linearLayout11);
        donate=view.findViewById(R.id.donate);
        donate_but=view.findViewById(R.id.button3);
        raised_by_share=view.findViewById(R.id.raised_by_share);
        enable_donations=view.findViewById(R.id.enable_donations);
        donating=view.findViewById(R.id.donating);
        rv_donation=view.findViewById(R.id.rv_donation);
        search=view.findViewById(R.id.search);
        search_neumorph=view.findViewById(R.id.neumorphCardView2);
        msg_custom=view.findViewById(R.id.textView140);
        cause_1=view.findViewById(R.id.cause_1);
        cause_2=view.findViewById(R.id.cause_2);
        cause_3=view.findViewById(R.id.cause_3);
        cause_4=view.findViewById(R.id.cause_4);
        cause_5=view.findViewById(R.id.cause_5);
        cause_6=view.findViewById(R.id.cause_6);

        msg_custom.setVisibility(View.GONE);
        cause_1.setVisibility(View.GONE);
        cause_2.setVisibility(View.GONE);
        cause_3.setVisibility(View.GONE);
        cause_4.setVisibility(View.GONE);
        cause_5.setVisibility(View.GONE);
        cause_6.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_donation.setItemViewCacheSize(20);
        rv_donation.setDrawingCacheEnabled(true);
        rv_donation.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rv_donation.setLayoutManager(layoutManager);

        not_found.setVisibility(View.GONE);
        donating.setVisibility(View.GONE);
        enable_donations.setVisibility(View.GONE);
        search_neumorph.setVisibility(View.GONE);
        if(identity.equals("admin")) {
            raised_by_share.setVisibility(View.VISIBLE);
        }
        else {
            raised_by_share.setVisibility(View.GONE);
        }
        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        donating.setOnClickListener(v->{
            creating_donation creating_donation=new creating_donation();
            Bundle args=new Bundle();
            args.putString("keys_for_creating_donation",key);
            creating_donation.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, creating_donation)
                    .addToBackStack(null)
                    .commit();
        });
        raised_by_share.setOnClickListener(v->{
            refferal refferal=new refferal();
            Bundle args=new Bundle();
            args.putString("sending_key", key);
            refferal.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, refferal)
                    .addToBackStack(null)
                    .commit();
        });
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        donate.setVisibility(View.GONE);
        try {
            key = getArguments().getString("sending_key")+"";
            deep_link_uid_value = getArguments().getString("sending_deep_uid_value");
        } catch (Exception e) {
            e.printStackTrace();
        }

        get_cause();

        cause_1.setOnClickListener(v->{
            if(c_1==null){
                c_1="cause_1";
                try {
                    if(!cause_1.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_1.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_1.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_1=null;
                try {
                    if(!cause_1.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_1.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_1.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });
        cause_2.setOnClickListener(v->{
            if(c_2==null){
                c_2="cause_2";
                try {
                    if(!cause_2.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_2.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_2.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_2=null;
                try {
                    if(!cause_2.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_2.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_2.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });
        cause_3.setOnClickListener(v->{
            if(c_3==null){
                c_3="cause_3";
                try {
                    if(!cause_3.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_3.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_3.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_3=null;
                try {
                    if(!cause_3.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_3.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_3.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });
        cause_4.setOnClickListener(v->{
            if(c_4==null){
                c_4="cause_4";
                try {
                    if(!cause_4.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_4.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_4.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_4=null;
                try {
                    if(!cause_4.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_4.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_4.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });
        cause_5.setOnClickListener(v->{
            if(c_5==null){
                c_5="cause_5";
                try {
                    if(!cause_5.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_5.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_5.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_5=null;
                try {
                    if(!cause_5.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_5.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_5.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });
        cause_6.setOnClickListener(v->{
            if(c_6==null){
                c_6="cause_6";
                try {
                    if(!cause_6.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_6.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum + Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum+40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_6.setBackgroundResource(R.drawable.bg_of_add_donations);
            }
            else{
                c_6=null;
                try {
                    if(!cause_6.getText().toString().replaceAll("[^0-9]", "").trim().equals("")) {
                        String numberOnly = cause_6.getText().toString().replaceAll("[^0-9]", "");
                        sum = sum - Integer.parseInt(numberOnly);
                    }
                    else
                        sum=sum-40;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                String text="Contribute ₹"+sum;
                donate_but.setText(text);
                cause_6.setBackgroundResource(R.drawable.border_amount_bg);
            }
        });

        donation_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards").child(key).child("cause_donations");
        supporters_txt.setVisibility(View.GONE);
        donate.setOnClickListener(v->{
            donation_checkout donation_checkout=new donation_checkout();
            Bundle args=new Bundle();
            args.putString("sending_key", key);
            args.putString("deep_sending_link_uid_value", deep_link_uid_value);
            donation_checkout.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, donation_checkout)
                    .addToBackStack(null)
                    .commit();
        });

        enable_donations.setOnClickListener(v->{
            if(enable_donations.getText().toString().equals("Enable donation")) {
                fluid_ref.child(key).child("other_donation").setValue("enabled");
                String undo="Undo";
                enable_donations.setText(undo);
                Toast.makeText(getContext(), "Donations enabled for this event. Reopen to see", Toast.LENGTH_SHORT).show();
            }
            else{
                fluid_ref.child(key).child("other_donation").removeValue();
                String undo="Enable donation";
                enable_donations.setText(undo);
            }
        });
        check_other_donations();

        donate_but.setOnClickListener(v->{
            donation_checkout donation_checkout=new donation_checkout();
            Bundle args=new Bundle();
            args.putString("sending_key", key);
            args.putString("deep_sending_link_uid_value", deep_link_uid_value);
            args.putString("deep_sending_link_sum", sum+"");
            donation_checkout.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, donation_checkout)
                    .addToBackStack(null)
                    .commit();
        });
        supporters_txt.setOnClickListener(v->{
            fluid_view_supporters fluid_view_supporters=new fluid_view_supporters();
            Bundle args=new Bundle();
            args.putString("sending_key", key);
            fluid_view_supporters.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, fluid_view_supporters)
                    .addToBackStack(null)
                    .commit();
        });
        if(deep_link_uid_value!=null){
            users_ref.child(deep_link_uid_value).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        fetch_data();
                    }
                    else{
                        Toast.makeText(getContext(), "Wrong link \uD83D\uDE15", Toast.LENGTH_SHORT).show();
                        not_found.setVisibility(View.VISIBLE);
                        draweeView.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
        else
            fetch_data();

        view.findViewById(R.id.share).setOnClickListener(v->{
                /*BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();*/
                String dhiti_started="https://www.dhitifoundation.com/";
                //Uri bitmapUri = getImageUri(getContextNullSafety(), bitmap);
                Uri bitmapUri = getLocalBitmapUri();
                String title = "*❤Dhiti Foundation❤*"+"\n\n"+"*\uD83C\uDFC1 Our Goal* -"+goal_args + "\n\n"+"*"+ title_args+"*" +
                        "\n\n" + "*About Dhiti Foundation :* " + dhiti_started + "\n\n"  + "*“Selfless giving is the art of living.”*"
                        +"\n\n"+"With this thought by *Frederick Lenz* \nclick on the link below to show some *Love and Support*."
                        +"\n\n"+ "https://dhiti.herokuapp.com/aryomtech/" +user.getUid()+"/"+ key + "\n\n"; //Text to be shared
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                intent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getContextNullSafety().getPackageName());

                startActivity(Intent.createChooser(intent, "Share"));
        });
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

        }
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
        new Handler(Looper.myLooper()).postDelayed(this::get_data,1000);
        return view;
    }

    private void get_cause() {
        fluid_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("custom_1").exists()) {
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_1.setVisibility(View.VISIBLE);
                    cause_1.setText(snapshot.child("custom_1").getValue(String.class));
                }
                if(snapshot.child("custom_2").exists()){
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_2.setVisibility(View.VISIBLE);
                    cause_2.setText(snapshot.child("custom_2").getValue(String.class));
                }
                if(snapshot.child("custom_3").exists()){
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_3.setVisibility(View.VISIBLE);
                    cause_3.setText(snapshot.child("custom_3").getValue(String.class));
                }
                if(snapshot.child("custom_4").exists()){
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_4.setVisibility(View.VISIBLE);
                    cause_4.setText(snapshot.child("custom_4").getValue(String.class));
                }
                if(snapshot.child("custom_5").exists()){
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_5.setVisibility(View.VISIBLE);
                    cause_5.setText(snapshot.child("custom_5").getValue(String.class));
                }
                if(snapshot.child("custom_6").exists()){
                    msg_custom.setVisibility(View.VISIBLE);
                    cause_6.setVisibility(View.VISIBLE);
                    cause_6.setText(snapshot.child("custom_6").getValue(String.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_data() {
        donation_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                mylist.clear();
                if (snapshot.exists()) {
                    search_neumorph.setVisibility(View.VISIBLE);
                    for (DataSnapshot keys : snapshot.getChildren()) {
                        list.add(snapshot.child(keys.getKey()).getValue(donations_data.class));
                    }
                    if(identity.equals("member") || identity.equals("chapter-head") || identity.equals("admin")){
                        donation_adapter = new donation_Adapter(getContext(), list,"",key);
                    }else {
                        donation_adapter = new donation_Adapter(getContext(), list, "guest",key);
                    }
                    if(rv_donation!=null)
                        rv_donation.setAdapter(donation_adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void search(String s) {
        mylist.clear();
        for(donations_data Objects:list){
            if (Objects.getAddress().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if (Objects.getCategory().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
        }
        if(identity.equals("member") || identity.equals("chapter-head") || identity.equals("admin")){
            donation_adapter = new donation_Adapter(getContext(), list,"",key);
        }else {
            donation_adapter = new donation_Adapter(getContext(), list, "guest",key);
        }
        donation_adapter.notifyDataSetChanged();
        if (rv_donation!=null)
            rv_donation.setAdapter(donation_adapter);
    }
    private void check_other_donations() {
        fluid_ref.child(key).child("other_donation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    donating.setVisibility(View.VISIBLE);
                    if (identity.equals("admin")) {
                        enable_donations.setVisibility(View.VISIBLE);
                        String undo="Undo";
                        enable_donations.setText(undo);
                    }
                }
                else{
                    if(identity.equals("admin"))
                        enable_donations.setVisibility(View.VISIBLE);
                    donating.setVisibility(View.GONE);
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
    private void fetch_data() {
        fluid_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    image_link_args = snapshot.child("image_link_original").getValue(String.class);
                    title_args = snapshot.child("title").getValue(String.class);
                    check_donation_args = snapshot.child("donation").getValue(String.class);
                    location_args = snapshot.child("location").getValue(String.class);
                    date_or_msg_args = snapshot.child("date_or_msg").getValue(String.class);
                    target_args = snapshot.child("target").getValue(String.class);
                    contributed_args = snapshot.child("contributed").getValue(String.class);
                    goal_args = snapshot.child("goal").getValue(String.class);
                    sub_heading_1_args = snapshot.child("sub_heading_1").getValue(String.class);
                    sub_heading_2_args = snapshot.child("sub_heading_2").getValue(String.class);
                    content_1_args = snapshot.child("content_1").getValue(String.class);
                    content_2_args = snapshot.child("content_2").getValue(String.class);

                    if (snapshot.child("transactions").exists())
                        supporters_txt.setVisibility(View.VISIBLE);

                    if (image_link_args != null) {
                        Uri uri = Uri.parse(image_link_args);
                        ImageRequest request = ImageRequest.fromUri(uri);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(draweeView.getController()).build();

                        draweeView.setController(controller);
                        /*try {
                            Glide.with(getContextNullSafety())
                                    .asBitmap()
                                    .load(image_link_args)
                                    .placeholder(R.drawable.ic_image_holder)
                                    .thumbnail(0.1f)
                                    .override(150, 150)
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .listener(new RequestListener<Bitmap>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                            share_ready = false;
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                            share_ready = true;
                                            new Handler(Looper.myLooper()).postDelayed(() -> {
                                                try {
                                                    Glide.with(requireActivity())
                                                            .asBitmap()
                                                            .load(image_link_args)
                                                            .into(image);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }, 2000);
                                            return false;
                                        }
                                    })
                                    .into(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                    if (goal_args != null)
                        if (!goal_args.equals(""))
                            goal_txt.setText(goal_args);

                    if (sub_heading_1_args != null) {
                        if (!sub_heading_1_args.equals(""))
                            sub_heading_1.setText(sub_heading_1_args);
                        else
                            sub_heading_1.setVisibility(View.GONE);
                    }
                    if (sub_heading_2_args != null) {
                        if (!sub_heading_2_args.equals(""))
                            sub_heading_2.setText(sub_heading_2_args);
                        else
                            sub_heading_2.setText(R.string.about_dhiti_foundation);
                    }
                    if (content_1_args != null) {
                        if (!content_1_args.equals(""))
                            content_1.setText(content_1_args);
                        else
                            content_1.setVisibility(View.GONE);
                    }
                    if (content_2_args != null) {
                        if (!content_2_args.equals(""))
                            content_2.setText(content_2_args);
                        else
                            content_2.setText(R.string.dhiti_started);
                    }
                    if (target_args != null) {
                        String text = "₹ " + target_args;
                        target_txt.setText(text);
                    }
                    if(snapshot.child("transactions").exists()) {
                        for (DataSnapshot amount_ds : snapshot.child("transactions").getChildren()) {
                            amount =amount + Long.parseLong(Objects.requireNonNull(snapshot.child("transactions").child(Objects.requireNonNull(amount_ds.getKey())).child("amount_paid").getValue(String.class)));
                        }
                        contributed_args=amount+"";
                        String text="₹ "+contributed_args;
                        contributed_txt.setText(text);
                    }
                    else{
                        if (contributed_args != null) {
                            String text = "₹ " + contributed_args;
                            contributed_txt.setText(text);
                        }
                    }
                    if (location_args != null)
                        location.setText(location_args);
                    if (date_or_msg_args != null)
                        date_or_msg.setText(date_or_msg_args);

                    if (target_args != null && contributed_args != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        donate.setVisibility(View.VISIBLE);
                        float per = (float) (Integer.parseInt(contributed_args) * 100) / Integer.parseInt(target_args);
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, per);
                        anim.setDuration(1000);
                        progressBar.startAnimation(anim);
                    } else {
                        donate.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                    }
                }
                else{
                    not_found.setVisibility(View.VISIBLE);
                    draweeView.setVisibility(View.GONE);
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
    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
    /*public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        final String randomKey = UUID.randomUUID().toString();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ""+randomKey, null);
        return Uri.parse(path);
    }*/
    public Uri getLocalBitmapUri() {

        draweeView.setDrawingCacheEnabled(true);
        draweeView.buildDrawingCache();
        Bitmap bitmap = draweeView.getDrawingCache();
        // Extract Bitmap from ImageView drawable
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
