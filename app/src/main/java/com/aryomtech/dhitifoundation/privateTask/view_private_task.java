package com.aryomtech.dhitifoundation.privateTask;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.aryomtech.dhitifoundation.privateTask.Adapter.addedtaskAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import soup.neumorphism.NeumorphCardView;


public class view_private_task extends Fragment {

    ImageView back_img;
    SimpleDraweeView profile_image,image;
    View view;
    Dialog dialog;
    FirebaseAuth auth;
    String submission;
    String undo_check=null,device_token="";
    TextView button,name,added_task_count_text,task_title,status,task_deadline,description,given_on;
    FirebaseUser user;
    NeumorphCardView but,but_approve;
    RecyclerView recyclerView;
    ArrayList added_task_list;
    Long xp,xp_minus,temp_xp,xp_plus;
    ValueEventListener listener_1,listener_2;
    DatabaseReference users_ref,private_approval;
    String dp;
    private Context contextNullSafe;
    ImageView button_sub;
    String creator,title_args,deadline_args,des_args,given_on_args,image_link_args,key_args,status_args
            ,added_task_count,submission_check,approval_check,approving,member_uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_private_task, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        back_img=view.findViewById(R.id.imageBack);
        profile_image=view.findViewById(R.id.profile_image);
        name=view.findViewById(R.id.name);
        recyclerView=view.findViewById(R.id.recyclerView);
        added_task_count_text=view.findViewById(R.id.textView76);
        task_title=view.findViewById(R.id.textView73);
        status=view.findViewById(R.id.status);
        task_deadline=view.findViewById(R.id.task_deadline);
        description=view.findViewById(R.id.textView75);
        given_on=view.findViewById(R.id.given_on);
        image=view.findViewById(R.id.image);
        but=view.findViewById(R.id.but);
        but_approve=view.findViewById(R.id.but_approve);
        button_sub=view.findViewById(R.id.button2);
        button=view.findViewById(R.id.button);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        private_approval= FirebaseDatabase.getInstance().getReference().child("private_approval");

        try {
            title_args = getArguments().getString("private_task_title");
            deadline_args = getArguments().getString("private_task_deadline");
            des_args = getArguments().getString("private_task_description");
            given_on_args = getArguments().getString("private_task_given_on");
            image_link_args = getArguments().getString("private_task_image_link");
            key_args = getArguments().getString("private_task_key");
            status_args = getArguments().getString("private_task_status");
            added_task_count = getArguments().getString("private_task_added_task_count");
            submission_check = getArguments().getString("private_task_submitted_or_not");
            approval_check = getArguments().getString("private_task_approved_or_not");
            approving = getArguments().getString("approving_private_task");
            member_uid = getArguments().getString("private_task_uid010");
            creator = getArguments().getString("private_task_creator");
        } catch (Exception e) {
            e.printStackTrace();
        }
        added_task_count_text.setText(added_task_count);
        task_title.setText(title_args);
        status.setText(status_args);
        task_deadline.setText(deadline_args);
        description.setText(des_args);
        given_on.setText(given_on_args);

        but.setVisibility(View.GONE);
        but_approve.setVisibility(View.GONE);

        if(approving==null)
            check_submission_approved();
        else
            but.setVisibility(View.GONE);
            approve_task();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        String uid;
        if (member_uid==null)
            uid=user.getUid();
        else
            uid=member_uid;

        users_ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child("token").exists())
                    device_token= snapshot.child("token").getValue(String.class);
                if(approving==null) {
                    String name_str = "Hi " + snapshot.child("name").getValue(String.class) + " !";
                    name.setText(name_str);
                }
                else{
                    String name_str =snapshot.child("name").getValue(String.class);
                    if(name_str!=null) {
                        name.setText(name_str);
                    }
                }
                   //xp part
                xp = snapshot.child("progress").child("xp").getValue(Long.class);
                if(xp==null)
                    xp=0L;
                temp_xp=xp;
                int level = Integer.parseInt(getLevel(xp / 100f));
                if(level<=10)
                    xp_minus = xp - 30L;
                else if(level<=20)
                    xp_minus = xp - 25L;
                else if(level<=30)
                    xp_minus = xp - 20L;
                else if(level<=40)
                    xp_minus = xp - 15L;
                else if(level<=50)
                    xp_minus = xp - 12L;
                else if(level<=53)
                    xp_minus = xp - 10L;


                if(level<=10)
                    xp_plus = xp + 30L;
                else if(level<=20)
                    xp_plus = xp + 25L;
                else if(level<=30)
                    xp_plus = xp + 20L;
                else if(level<=40)
                    xp_plus = xp + 15L;
                else if(level<=50)
                    xp_plus = xp + 12L;
                else if(level<=53)
                    xp_plus = xp + 10L;
                Log.e("xp_minus",""+xp);

                if(snapshot.child("dplink").exists()){
                        dp=snapshot.child("dplink").getValue(String.class);
                        Uri uri = Uri.parse(dp);
                        ImageRequest request = ImageRequest.fromUri(uri);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(profile_image.getController()).build();

                        profile_image.setController(controller);
                        /*try{
                            Glide.with(getContextNullSafety()).asBitmap()
                                    .load(dp)
                                    .thumbnail(0.1f)
                                    .override(100,100)
                                    .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                    .into(profile_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

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

        back_img.setOnClickListener(v->onBackPressed());
        but.setOnClickListener(v->{
            if(submission.equals("null"))
                send_for_approval();
            else
                unsubmit_task();
        });
        if(image_link_args==null)
            image.setVisibility(View.GONE);
        else {
            image.setVisibility(View.VISIBLE);
            try {
                System.gc();
                Uri uri = Uri.parse(image_link_args);
                ImageRequest request = ImageRequest.fromUri(uri);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(image.getController()).build();

                image.setController(controller);
                /*Glide.with(getContextNullSafety())
                        .asBitmap()
                        .load(image_link_args)
                        .thumbnail(0.1f)
                        .override(150, 150)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    try {
                                        Glide.with(getContextNullSafety())
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
                        .placeholder(R.drawable.ic_good_team)
                        .into(image);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(approving==null)
            show_task();
        else
            show_approve_task();

        but_approve.setOnClickListener(v->{
            if(button.getText().toString().equalsIgnoreCase("approve") && xp!=null){

                dialog = new Dialog(requireContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("submitted.json");
                dialog.show();
                private_approval.child(key_args).child("approved").setValue("true");
                private_approval.child(key_args).child("status").setValue("Finished");
                users_ref.child(member_uid).child("task").child(key_args).child("approved").setValue("true");
                users_ref.child(member_uid).child("task").child(key_args).child("status").setValue("Finished");
                if(undo_check.equals("true"))
                    xp=temp_xp;
                else if(undo_check.equals("false"))
                    xp=xp_plus;
                users_ref.child(member_uid).child("progress").child("xp").setValue(xp);
                Specific specific=new Specific();
                specific.noti("Task approved","Hooray! you got points for the task - "+task_title.getText().toString().trim()+" \nCurrent Xp = "+xp,device_token);
                new Handler(Looper.myLooper()).postDelayed(() -> dialog.dismiss(), 1700);
            }
            else if(button.getText().toString().equalsIgnoreCase("undo") && xp_minus!=null){
                private_approval.child(key_args).child("approved").setValue("false");
                private_approval.child(key_args).child("status").setValue("ongoing");
                users_ref.child(member_uid).child("task").child(key_args).child("approved").removeValue();
                users_ref.child(member_uid).child("task").child(key_args).child("status").setValue("ongoing");
                if (undo_check.equals("true"))
                    xp=xp_minus;
                else if(undo_check.equals("false"))
                    xp=temp_xp;
                users_ref.child(member_uid).child("progress").child("xp").setValue(xp);
            }
        });
        return view;
    }

    private void show_approve_task() {
        private_approval.child(key_args).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(approving!=null) {
                    if (snapshot.child("added_task").exists()) {
                        added_task_list = (ArrayList) snapshot.child("added_task").getValue();
                        addedtaskAdapter addedtaskAdapter = new addedtaskAdapter(getContext(), added_task_list,"green");
                        addedtaskAdapter.notifyDataSetChanged();
                        try {
                            if (recyclerView != null)
                                recyclerView.setAdapter(addedtaskAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
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
    public String getLevel(Float f){
        String txt = Float.toString(f);
        for(int k=0;k<txt.length();k++){
            if(txt.charAt(k)=='.'){
                txt=txt.substring(0,k);
            }
        }
        return txt;
    }

    private void approve_task() {
        listener_1=private_approval.child(key_args).child("approved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(approving!=null) {
                    String approved_or_not = snapshot.getValue(String.class);
                    if(approved_or_not!=null) {
                        if (approved_or_not.equals("false")) {
                            if (undo_check == null)
                                undo_check = "false";
                            button.setText(R.string.approve);
                            but_approve.setVisibility(View.VISIBLE);
                        } else {
                            if (undo_check == null)
                                undo_check = "true";
                            button.setText(R.string.undo);
                            but_approve.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void send_for_approval() {
            dialog = new Dialog(requireContext());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.loading_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
            lottieAnimationView.setAnimation("submitted.json");
            dialog.show();
            DatabaseReference private_approval = FirebaseDatabase.getInstance().getReference().child("private_approval");
            private_approval.child(key_args).child("key").setValue(key_args);
            private_approval.child(key_args).child("description").setValue(des_args);
            private_approval.child(key_args).child("given_on").setValue(given_on_args);
            private_approval.child(key_args).child("image_link").setValue(image_link_args);
            private_approval.child(key_args).child("status").setValue(status_args);
            private_approval.child(key_args).child("task_deadline").setValue(deadline_args);
            private_approval.child(key_args).child("task_title").setValue(title_args);
            private_approval.child(key_args).child("added_task").setValue(added_task_list);
            private_approval.child(key_args).child("approved").setValue("false");
            private_approval.child(key_args).child("dp").setValue(dp);
            private_approval.child(key_args).child("uid").setValue(user.getUid());
            private_approval.child(key_args).child("creator").setValue(creator);
            users_ref.child(user.getUid()).child("task").child(key_args).child("submitted").setValue("yes");
            button_sub.setImageResource(R.drawable.ic_undo);
            new Handler(Looper.myLooper()).postDelayed(() -> dialog.dismiss(), 1700);
    }
    private void unsubmit_task(){
        DatabaseReference private_approval = FirebaseDatabase.getInstance().getReference().child("private_approval");
        private_approval.child(key_args).removeValue();
        users_ref.child(user.getUid()).child("task").child(key_args).child("submitted").removeValue();
        button_sub.setImageResource(R.drawable.ic_done);
    }

    private void show_task() {
        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(approving==null) {
                    if (snapshot.child("task").child(key_args).child("added_task").exists()) {
                        added_task_list = (ArrayList) snapshot.child("task").child(key_args).child("added_task").getValue();
                        addedtaskAdapter addedtaskAdapter = new addedtaskAdapter(getContext(), added_task_list,"green");
                        addedtaskAdapter.notifyDataSetChanged();
                        try{
                            if(recyclerView!=null){
                                recyclerView.setAdapter(addedtaskAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void onBackPressed() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();

    }
    void check_submission_approved(){
        listener_2=users_ref.child(user.getUid()).child("task").child(key_args).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                submission=snapshot.child("submitted").getValue(String.class)+"";
                if(!snapshot.child("approved").exists()){
                    but_approve.setVisibility(View.GONE);
                    if(submission.equals("yes")) {
                        but.setVisibility(View.VISIBLE);
                        button_sub.setImageResource(R.drawable.ic_undo);
                    }
                    else{
                        but.setVisibility(View.VISIBLE);
                        button_sub.setImageResource(R.drawable.ic_done);
                    }
                }
                else{
                    but.setVisibility(View.GONE);
                    but_approve.setVisibility(View.VISIBLE);
                    button.setText(R.string.approved);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}