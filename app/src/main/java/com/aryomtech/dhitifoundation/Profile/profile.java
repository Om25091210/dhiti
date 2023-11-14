package com.aryomtech.dhitifoundation.Profile;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.Form_Fill;
import com.aryomtech.dhitifoundation.Locality;
import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.Member.displaying_members;
import com.aryomtech.dhitifoundation.Profile.external_expense.ext_expense;
import com.aryomtech.dhitifoundation.Profile.membership_payments.membership_payment_info;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Control_panel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.SmoothBottomBar;
import soup.neumorphism.NeumorphCardView;

public class profile extends Fragment {

    String identity="";
    Button button_progress,button_member,yes,no;
    ProgressBar progress_bar;
    TextView text_view_progress,email,phone,aadhar
            ,city,email_pr,phone_pr,aadhar_pr,city_pr,name_txt,part_dhiti
            ,address_pr,address,history,view_members,join_send,mem_pay_info,ext_expense_view;
    ImageView imagemenu;
    DatabaseReference user_reference;
    View view;
    String first_membership="";
    Long due;
    String part_str,email_str,phone_str,aadhar_str,city_str,address_str;
    String imagelink;
    CircleImageView profile_image;
    SmoothBottomBar smoothBottomBar;
    FirebaseAuth auth;
    FirebaseUser user;
    String paid_or_not="";
    private Context contextNullSafe;
    View query,member_lyaout,transaction_history,admin_layout,mem_pay_info_admin;
    String fb_link,twitter_link,linkedin_link,whatsapp_no,insta_link;
    NeumorphCardView fb_card,twitter_card,linkedin_card,whatsapp_card,insta_card;
    //Context context;
    LottieAnimationView pay_anim,p_anim,img_load;
    LottieAnimationView website,toolbar;
    ValueEventListener listener_value;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        //binding views.
        progress_bar=view.findViewById(R.id.progress_bar);
        imagemenu=view.findViewById(R.id.imagemenu);
        button_progress=view.findViewById(R.id.button);
        button_member=view.findViewById(R.id.button_member);
        pay_anim=view.findViewById(R.id.pay_anim);
        text_view_progress=view.findViewById(R.id.text_view_progress);
        email=view.findViewById(R.id.email);
        phone=view.findViewById(R.id.phone);
        profile_image=view.findViewById(R.id.profile_image);
        aadhar=view.findViewById(R.id.aadhar);
        city=view.findViewById(R.id.city);
        email_pr=view.findViewById(R.id.textView49);
        phone_pr=view.findViewById(R.id.textView51);
        aadhar_pr=view.findViewById(R.id.textView53);
        city_pr=view.findViewById(R.id.textView54);
        name_txt=view.findViewById(R.id.textView47);
        part_dhiti=view.findViewById(R.id.textView48);
        address_pr=view.findViewById(R.id.textView55);
        address=view.findViewById(R.id.address);
        p_anim=view.findViewById(R.id.animation_view);
        fb_card=view.findViewById(R.id.fb_card);
        twitter_card=view.findViewById(R.id.twitter_card);
        linkedin_card=view.findViewById(R.id.linkedin_card);
        whatsapp_card=view.findViewById(R.id.whatsapp_card);
        insta_card=view.findViewById(R.id.insta_card);
        img_load=view.findViewById(R.id.animate);
        member_lyaout=view.findViewById(R.id.member_lyaout);
        query=view.findViewById(R.id.query);
        yes=view.findViewById(R.id.yes);
        no=view.findViewById(R.id.no);
        admin_layout=view.findViewById(R.id.admin_layout);
        history=view.findViewById(R.id.history);
        transaction_history=view.findViewById(R.id.transaction_history);
        join_send=view.findViewById(R.id.join_send);
        view_members=view.findViewById(R.id.view_members);
        mem_pay_info_admin=view.findViewById(R.id.mem_pay_info_admin);
        mem_pay_info=view.findViewById(R.id.mem_pay_info);
        ext_expense_view=view.findViewById(R.id.ext_expense_view);

        ext_expense_view.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new ext_expense())
                .addToBackStack(null)
                .commit());

        join_send.setOnClickListener(v->{
            if (!identity.equals("admin")) {
                Intent mainIntent = new Intent(getContextNullSafety(), Form_Fill.class);
                mainIntent.putExtra("sending_from_profile_identity", identity);
                startActivity(mainIntent);
            }
            else{
                Toast.makeText(getContextNullSafety(), "You're an admin, sir.", Toast.LENGTH_SHORT).show();
            }
        });
        history.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new transaction_history())
                .addToBackStack(null)
                .commit());

        view_members.setOnClickListener(v-> ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new displaying_members())
                .addToBackStack(null)
                .commit());

        if(identity.equals("member") || identity.equals("chapter-head") || identity.equals("admin")) {
            transaction_history.setVisibility(View.VISIBLE);
            member_lyaout.setVisibility(View.VISIBLE);
            query.setVisibility(View.VISIBLE);
            //first payment query.
            first_membership = getContextNullSafety().getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE)
                    .getString("0_or_1_first_payment", "");
            if (first_membership.equals(""))
                member_lyaout.setVisibility(View.GONE);
            else
                query.setVisibility(View.GONE);
        }
        else{
            transaction_history.setVisibility(View.GONE);
            member_lyaout.setVisibility(View.GONE);
            query.setVisibility(View.GONE);
        }
        if(identity.equals("chapter-head") || identity.equals("admin"))
            admin_layout.setVisibility(View.VISIBLE);
        else
            admin_layout.setVisibility(View.GONE);

        if (identity.equals("admin"))
            mem_pay_info_admin.setVisibility(View.VISIBLE);

        mem_pay_info.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer, new membership_payment_info())
                .addToBackStack(null)
                .commit());

        //TODO: Remove Shared preferences
        yes.setOnClickListener(v->{
            getContextNullSafety().getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE).edit()
                        .putString("0_or_1_first_payment","true").apply();
                member_lyaout.setVisibility(View.VISIBLE);
                query.setVisibility(View.GONE);
                due=200L;
        });

        no.setOnClickListener(v->{
            getContextNullSafety().getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE).edit()
                        .putString("0_or_1_first_payment","false").apply();
                member_lyaout.setVisibility(View.VISIBLE);
                query.setVisibility(View.GONE);
                due=350L;
         });

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
        if(part_dhiti.getText().toString().trim().equals("")){
            part_dhiti.setVisibility(View.GONE);
        }
        if(address.getText().toString().trim().equals("")){
            address_pr.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }
        fb_card.setVisibility(View.GONE);
        twitter_card.setVisibility(View.GONE);
        linkedin_card.setVisibility(View.GONE);
        whatsapp_card.setVisibility(View.GONE);
        insta_card.setVisibility(View.GONE);
        img_load.setVisibility(View.GONE);
        new Handler(Looper.myLooper()).postDelayed(() -> pay_anim.pauseAnimation(),3000);

        listener_value=user_reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String name = snapshot.child("name").getValue(String.class);
                    name_txt.setText(name);
                    //due
                    if(snapshot.child("membership_info").child("due").exists()) {
                        due = snapshot.child("membership_info").child("due").getValue(Long.class);
                        paid_or_not=snapshot.child("membership_info").child("paid").getValue(String.class);
                    }
                    else {
                        paid_or_not="no";
                        if(due==null) {
                            String check_due = getContextNullSafety().getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE)
                                    .getString("0_or_1_first_payment", "");
                            if(check_due.equals("true"))
                                due=200L;
                            else if(check_due.equals("false"))
                                due=350L;
                        }
                    }
                    if(Objects.equals(paid_or_not, "yes") && due==0L){
                        String msg = "Membership fee paid";
                        button_member.setText(msg);
                        button_member.setBackgroundResource(R.drawable.paid_but_bg);
                        pay_anim.setAnimation("well_done.json");
                        pay_anim.resumeAnimation();
                    }
                     else{
                         String but_text="Pay Membership Fee";
                         button_member.setText(but_text);
                         button_member.setBackgroundResource(R.drawable.button_profile_complete);
                         pay_anim.setAnimation("pay_mem.json");
                     }
                    if (snapshot.child("dplink").exists()) {
                        imagelink = snapshot.child("dplink").getValue(String.class);
                        img_load.setVisibility(View.VISIBLE);
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
                                                img_load.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .into(profile_image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },500);
                    }
                    if (snapshot.child("part").exists()) {
                        part_str = snapshot.child("part").getValue(String.class);
                        assert part_str != null;
                        if(part_str.trim().equals(""))
                            part_dhiti.setVisibility(View.GONE);
                        else
                            part_dhiti.setVisibility(View.VISIBLE);
                            part_dhiti.setText(part_str);
                    }
                    if (snapshot.child("email").exists()) {
                        email_str = snapshot.child("email").getValue(String.class);
                        assert email_str != null;
                        if(email_str.trim().equals("")) {
                            email.setVisibility(View.GONE);
                            email_pr.setVisibility(View.GONE);
                        }
                        else{
                            email.setVisibility(View.VISIBLE);
                            email_pr.setVisibility(View.VISIBLE);
                            email.setText(email_str);
                        }
                    }
                    if (snapshot.child("phone").exists()) {
                        phone_str = snapshot.child("phone").getValue(String.class);
                        assert phone_str!=null;
                        if(phone_str.trim().equals("")){
                            phone.setVisibility(View.GONE);
                            phone_pr.setVisibility(View.GONE);
                        }
                        else {
                            phone.setVisibility(View.VISIBLE);
                            phone_pr.setVisibility(View.VISIBLE);
                            phone.setText(phone_str);
                        }
                    }
                    if (snapshot.child("aadhar").exists()) {
                        aadhar_str = snapshot.child("aadhar").getValue(String.class);
                        assert aadhar_str != null;
                        if (aadhar_str.trim().equals("")){
                            aadhar.setVisibility(View.GONE);
                            aadhar_pr.setVisibility(View.GONE);
                        }
                        else{
                            aadhar.setVisibility(View.VISIBLE);
                            aadhar_pr.setVisibility(View.VISIBLE);
                            aadhar.setText(aadhar_str);
                        }
                    }
                    if (snapshot.child("city").exists()) {
                        city_str = snapshot.child("city").getValue(String.class);
                        assert city_str!=null;
                        if(city_str.trim().equals("")){
                            city.setVisibility(View.GONE);
                            city_pr.setVisibility(View.GONE);
                        }
                        else{
                            city.setVisibility(View.VISIBLE);
                            city_pr.setVisibility(View.VISIBLE);
                            city.setText(city_str);
                        }
                    }
                    if (snapshot.child("address").exists()) {
                        address_str = snapshot.child("address").getValue(String.class);
                        assert address_str != null;
                        if (address_str.trim().equals("")){
                            address_pr.setVisibility(View.GONE);
                            address.setVisibility(View.GONE);
                        }
                        else{
                            address_pr.setVisibility(View.VISIBLE);
                            address.setVisibility(View.VISIBLE);
                            address.setText(address_str);
                        }
                    }
                    if (snapshot.child("facebook").exists()) {
                        fb_link = snapshot.child("facebook").getValue(String.class);
                        assert fb_link != null;
                        if (fb_link.trim().equals(""))
                            fb_card.setVisibility(View.GONE);
                        else
                            fb_card.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.child("twitter").exists()) {
                        twitter_link = snapshot.child("twitter").getValue(String.class);
                        assert twitter_link!=null;
                        if(twitter_link.trim().equals(""))
                            twitter_card.setVisibility(View.GONE);
                        else
                            twitter_card.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.child("linkedin").exists()) {
                        linkedin_link = snapshot.child("linkedin").getValue(String.class);
                        assert linkedin_link != null;
                        if (linkedin_link.trim().equals(""))
                            linkedin_card.setVisibility(View.GONE);
                        else
                            linkedin_card.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.child("whatsapp").exists()) {
                        whatsapp_no = snapshot.child("whatsapp").getValue(String.class);
                        assert whatsapp_no!=null;
                        if (whatsapp_no.trim().equals(""))
                            whatsapp_card.setVisibility(View.GONE);
                        else
                            whatsapp_card.setVisibility(View.VISIBLE);
                    }
                    if (snapshot.child("instagram").exists()) {
                        insta_link = snapshot.child("instagram").getValue(String.class);
                        assert insta_link!=null;
                        if (insta_link.trim().equals(""))
                            insta_card.setVisibility(View.GONE);
                        else
                            insta_card.setVisibility(View.VISIBLE);
                    }
                    int count=0;
                    for (DataSnapshot ds:snapshot.getChildren()){
                        if(!Objects.equals(ds.getKey(), "membership_info")) {
                            if(!Objects.equals(ds.getKey(),"task")) {
                                if(!Objects.equals(ds.getKey(),"progress")) {
                                    if (Objects.equals(snapshot
                                            .child(Objects.requireNonNull(ds.getKey()))
                                            .getValue(String.class), "")) {
                                        count++;
                                    }
                                }
                            }
                        }
                    }
                    long total_childs;
                    if(snapshot.child("task").exists()
                        && snapshot.child("membership_info").exists()
                        && snapshot.child("progress").exists())
                         total_childs=snapshot.getChildrenCount() - 5;

                    else if(snapshot.child("membership_info").exists() && snapshot.child("task").exists()
                        || snapshot.child("membership_info").exists() && snapshot.child("progress").exists()
                        || snapshot.child("progress").exists() && snapshot.child("task").exists())
                          total_childs=snapshot.getChildrenCount() -4;

                    else if(snapshot.child("membership_info").exists()
                            || snapshot.child("task").exists()
                            || snapshot.child("progress").exists())
                              total_childs = snapshot.getChildrenCount() - 3;
                    else
                        total_childs=snapshot.getChildrenCount() - 2;

                    if (count == 0 && total_childs>=12) {
                        text_view_progress.setVisibility(View.GONE);
                        progress_bar.setVisibility(View.GONE);
                        button_progress.setVisibility(View.GONE);
                        if(!snapshot.child("progress").exists()) {
                            user_reference.child(user.getUid()).child("progress").child("xp").setValue(10);
                            user_reference.child(user.getUid()).child("progress").child("profile").setValue("completed");
                        }
                        else {
                            Long xp_long = snapshot.child("progress").child("xp").getValue(Long.class);
                            if (xp_long != null && !snapshot.child("progress").child("profile").exists()) {
                                xp_long = xp_long + 10;
                                user_reference.child(user.getUid()).child("progress").child("xp").setValue(xp_long);
                                user_reference.child(user.getUid()).child("progress").child("profile").setValue("completed");
                            }
                        }
                    }
                    else {
                        if(snapshot.child("progress").exists()) {
                            Long xp_long = snapshot.child("progress").child("xp").getValue(Long.class);
                            if (xp_long != null && snapshot.child("progress").child("profile").exists()) {
                                xp_long = xp_long - 10;
                                user_reference.child(user.getUid()).child("progress").child("xp").setValue(xp_long);
                                user_reference.child(user.getUid()).child("progress").child("profile").removeValue();
                            }
                        }
                        else{
                            Long xp_long = snapshot.child("progress").child("xp").getValue(Long.class);
                            if(xp_long!=null && xp_long>0)
                                xp_long=xp_long-10;
                            user_reference.child(user.getUid()).child("progress").child("xp").setValue(xp_long);
                            user_reference.child(user.getUid()).child("progress").child("profile").removeValue();
                        }
                        text_view_progress.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.VISIBLE);
                        button_progress.setVisibility(View.VISIBLE);
                        float percentage = (float) ((total_childs * 100) / 13);
                        float percentage_reverse = (float) ((count * 100) / 13);
                        float final_percent=percentage-percentage_reverse;
                        int percent = (int) final_percent;
                        String str_per = percent + "%";
                        text_view_progress.setText(str_per);
                        progress_bar.setProgress(percent);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        smoothBottomBar=getActivity().findViewById(R.id.bottomBar);
        website=getActivity().findViewById(R.id.website);
        toolbar=getActivity().findViewById(R.id.toolbar);

        smoothBottomBar.setVisibility(View.VISIBLE);
        website.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        smoothBottomBar.setItemActiveIndex(2);

        admin_layout.setOnClickListener(v->{
            if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction().
                        remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
            }
            ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer,new Control_panel())
                    .commit();
        });

        String but_text="Pay Membership Fee";
        button_member.setText(but_text);
        button_member.setOnClickListener(v-> {
            if(due!=null) {
                Intent pay = new Intent(getContext(), Mem_payment.class);
                pay.putExtra("member due 32345", due);
                getContextNullSafety().startActivity(pay);
                /*if(paid_or_not.equals("no") && due!=0L) {
                    Intent pay = new Intent(getContext(), Mem_payment.class);
                    pay.putExtra("member due 32345", due);
                    getContextNullSafety().startActivity(pay);
                }*/
            }
            else{
                Toast.makeText(getContext(), "Please wait for server response.", Toast.LENGTH_SHORT).show();
            }
        });

        fb_card.setOnClickListener(v->{
            String facebookUrl =fb_link;
            Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(facebookAppIntent);
        });
        twitter_card.setOnClickListener(v->{
            String url = twitter_link;
            Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(twitterAppIntent);
        });
        linkedin_card.setOnClickListener(v->{
            String url = linkedin_link;
            Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(linkedInAppIntent);
        });
        whatsapp_card.setOnClickListener(v->{
            String url = "https://api.whatsapp.com/send?phone=" +"+91"+ whatsapp_no;
            try {
                PackageManager pm = v.getContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                v.getContext().startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        insta_card.setOnClickListener(v->{
            Intent insta_in;
            String scheme = "http://instagram.com/_u/"+insta_link;
            String path = "https://instagram.com/"+insta_link;
            String nomPackageInfo ="com.instagram.android";
            try {
                requireContext().getPackageManager().getPackageInfo(nomPackageInfo, 0);
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            } catch (Exception e) {
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            }
            startActivity(insta_in);

        });
        imagemenu.setOnClickListener(v->{
            Intent fill_profile=new Intent(getContext(),Edit_profile.class);
            fill_profile.putExtra("name_passing_301",name_txt.getText().toString().trim());
            fill_profile.putExtra("part_passing_302",part_str);
            fill_profile.putExtra("email_passing_303",email_str);
            fill_profile.putExtra("phone_passing_304",phone_str);
            fill_profile.putExtra("aadhar_passing_305",aadhar_str);
            fill_profile.putExtra("city_passing_306",city_str);
            fill_profile.putExtra("address_passing_307",address_str);//
            fill_profile.putExtra("imagelink_passing_308",imagelink);
            fill_profile.putExtra("facebook_passing_309",fb_link);
            fill_profile.putExtra("twitter_passing_310",twitter_link);
            fill_profile.putExtra("linkedin_passing_311",linkedin_link);
            fill_profile.putExtra("whatsapp_passing_312",whatsapp_no);
            fill_profile.putExtra("instagram_passing_313",insta_link);
            startActivity(fill_profile);
        });
        button_progress.setOnClickListener(v->{
            Intent fill_profile=new Intent(getContext(),Edit_profile.class);
            fill_profile.putExtra("name_passing_301",name_txt.getText().toString().trim());
            fill_profile.putExtra("part_passing_302",part_str);
            fill_profile.putExtra("email_passing_303",email_str);
            fill_profile.putExtra("phone_passing_304",phone_str);
            fill_profile.putExtra("aadhar_passing_305",aadhar_str);
            fill_profile.putExtra("city_passing_306",city_str);
            fill_profile.putExtra("address_passing_307",address_str);//
            fill_profile.putExtra("imagelink_passing_308",imagelink);
            fill_profile.putExtra("facebook_passing_309",fb_link);
            fill_profile.putExtra("twitter_passing_310",twitter_link);
            fill_profile.putExtra("linkedin_passing_311",linkedin_link);
            fill_profile.putExtra("whatsapp_passing_312",whatsapp_no);
            fill_profile.putExtra("instagram_passing_313",insta_link);
            startActivity(fill_profile);
        });

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,new MainFragment())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        return view;
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

}