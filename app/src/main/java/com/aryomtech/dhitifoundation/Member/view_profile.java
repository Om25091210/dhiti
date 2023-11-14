package com.aryomtech.dhitifoundation.Member;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import soup.neumorphism.NeumorphCardView;


public class view_profile extends Fragment {

    View view;
    String identity="";
    Button button_progress;
    String user_uid;
    String imagelink,part_str,email_str,phone_str,aadhar_str,city_str,address_str;
    LottieAnimationView animate;
    CircleImageView profile_image;
    TextView name_txt,part_txt;
    ProgressBar progress_bar;
    DatabaseReference users_ref;
    NeumorphCardView fb_card,twitter_card,linkedin_card,whatsapp_card,insta_card;
    TextView email,phone,aadhar
            ,city,email_pr,phone_pr,aadhar_pr,city_pr
            ,address_pr,address;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_profile, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        profile_image=view.findViewById(R.id.profile_image);
        animate=view.findViewById(R.id.animate);
        name_txt=view.findViewById(R.id.textView47);
        part_txt=view.findViewById(R.id.textView48);
        email=view.findViewById(R.id.email);
        phone=view.findViewById(R.id.phone);
        aadhar=view.findViewById(R.id.aadhar);
        city=view.findViewById(R.id.city);
        email_pr=view.findViewById(R.id.textView49);
        phone_pr=view.findViewById(R.id.textView51);
        aadhar_pr=view.findViewById(R.id.textView53);
        city_pr=view.findViewById(R.id.textView54);
        address_pr=view.findViewById(R.id.textView55);
        address=view.findViewById(R.id.address);
        fb_card=view.findViewById(R.id.fb_card);
        twitter_card=view.findViewById(R.id.twitter_card);
        linkedin_card=view.findViewById(R.id.linkedin_card);
        whatsapp_card=view.findViewById(R.id.whatsapp_card);
        insta_card=view.findViewById(R.id.insta_card);
        progress_bar=view.findViewById(R.id.progress_bar);
        button_progress=view.findViewById(R.id.button);

        animate.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        button_progress.setVisibility(View.GONE);
        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        if(identity.equals("admin")) {
            aadhar.setVisibility(View.VISIBLE);
            aadhar_pr.setVisibility(View.VISIBLE);
        }
        else {
            aadhar.setVisibility(View.GONE);
            aadhar_pr.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
            phone_pr.setVisibility(View.GONE);
            address_pr.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }

        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        try{
            user_uid=getArguments().getString("passing_list_uid_784521");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(email.getText().toString().trim().equals("")){
            email.setVisibility(View.GONE);
            email_pr.setVisibility(View.GONE);
        }
        if(phone.getText().toString().trim().equals("")){
            phone.setVisibility(View.GONE);
            phone_pr.setVisibility(View.GONE);
        }
        if(aadhar.getText().toString().trim().equals("")){
            aadhar.setVisibility(View.GONE);
            aadhar_pr.setVisibility(View.GONE);
        }
        if(city.getText().toString().trim().equals("")){
            city.setVisibility(View.GONE);
            city_pr.setVisibility(View.GONE);
        }
        if(part_txt.getText().toString().trim().equals("")){
            part_txt.setVisibility(View.GONE);
        }
        if(address.getText().toString().trim().equals("")){
            address_pr.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }
        fb_card.setOnClickListener(v->{
            String facebookUrl ="https://www.facebook.com/dhitifoundation/";
            Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(facebookAppIntent);
        });
        twitter_card.setOnClickListener(v->{
            String url = "https://twitter.com/DhitiFoundation?s=08";
            Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(twitterAppIntent);
        });
        linkedin_card.setOnClickListener(v->{
            String url = "https://www.linkedin.com/in/dhiti-welfare-foundation-9779981ab";
            Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(linkedInAppIntent);
        });
        whatsapp_card.setOnClickListener(v->{
            String url = "https://api.whatsapp.com/send?phone=" +"+91"+ "8359996222";
            try {
                PackageManager pm = getContextNullSafety().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        insta_card.setOnClickListener(v->{
            Intent insta_in;
            String scheme = "http://instagram.com/_u/"+"dhiti_foundation";
            String path = "https://instagram.com/"+"dhiti_foundation";
            String nomPackageInfo ="com.instagram.android";
            try {
                getContextNullSafety().getPackageManager().getPackageInfo(nomPackageInfo, 0);
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            } catch (Exception e) {
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            }
            startActivity(insta_in);
        });
        view.findViewById(R.id.back).setOnClickListener(v->onBackPressed());
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
        get_details();
        return view;
    }

    private void get_details() {
        if(user_uid!=null) {
            users_ref.child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null)
                        name_txt.setText(name);

                    if (snapshot.child("dplink").exists()) {
                        imagelink = snapshot.child("dplink").getValue(String.class);
                        animate.setVisibility(View.VISIBLE);
                        new Handler(Looper.myLooper()).postDelayed(() -> {
                            try {
                                Glide.with(getContextNullSafety()).asBitmap()
                                        .load(imagelink)
                                        .listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                animate.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(profile_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 500);
                    }
                    if (snapshot.child("part").exists()) {
                        part_str = snapshot.child("part").getValue(String.class);
                        assert part_str != null;
                        if (part_str.trim().equals(""))
                            part_txt.setVisibility(View.GONE);
                        else
                            part_txt.setVisibility(View.VISIBLE);
                        part_txt.setText(part_str);
                    }
                    if (snapshot.child("email").exists()) {
                        email_str = snapshot.child("email").getValue(String.class);
                        assert email_str != null;
                        if (email_str.trim().equals("")) {
                            email.setVisibility(View.GONE);
                            email_pr.setVisibility(View.GONE);
                        } else {
                            email.setVisibility(View.VISIBLE);
                            email_pr.setVisibility(View.VISIBLE);
                            email.setText(email_str);
                        }
                    }
                    if (snapshot.child("phone").exists()) {
                        phone_str = snapshot.child("phone").getValue(String.class);
                        assert phone_str != null;
                        if (phone_str.trim().equals("")) {
                            phone.setVisibility(View.GONE);
                            phone_pr.setVisibility(View.GONE);
                        } else if (identity.equals("admin")) {
                            phone.setVisibility(View.VISIBLE);
                            phone_pr.setVisibility(View.VISIBLE);
                            phone.setText(phone_str);
                        }
                    }
                    if (snapshot.child("aadhar").exists()) {
                        aadhar_str = snapshot.child("aadhar").getValue(String.class);
                        assert aadhar_str != null;
                        if (aadhar_str.trim().equals("")) {
                            aadhar.setVisibility(View.GONE);
                            aadhar_pr.setVisibility(View.GONE);
                        } else if (identity.equals("admin")) {
                            aadhar.setVisibility(View.VISIBLE);
                            aadhar_pr.setVisibility(View.VISIBLE);
                            aadhar.setText(aadhar_str);
                        }
                    }
                    if (snapshot.child("city").exists()) {
                        city_str = snapshot.child("city").getValue(String.class);
                        assert city_str != null;
                        if (city_str.trim().equals("")) {
                            city.setVisibility(View.GONE);
                            city_pr.setVisibility(View.GONE);
                        } else {
                            city.setVisibility(View.VISIBLE);
                            city_pr.setVisibility(View.VISIBLE);
                            city.setText(city_str);
                        }
                    }
                    if (snapshot.child("address").exists()) {
                        address_str = snapshot.child("address").getValue(String.class);
                        assert address_str != null;
                        if (address_str.trim().equals("")) {
                            address_pr.setVisibility(View.GONE);
                            address.setVisibility(View.GONE);
                        } else if (identity.equals("admin")) {
                            address_pr.setVisibility(View.VISIBLE);
                            address.setVisibility(View.VISIBLE);
                            address.setText(address_str);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
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
    private void onBackPressed() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}