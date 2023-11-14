package com.aryomtech.dhitifoundation.GroupTask;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import www.sanju.motiontoast.MotionToast;


public class view_group_task extends Fragment {

    View view;
    CardView cardView3;
    SimpleDraweeView lead_dp,per_1,per_2,per_3,photo_image;
    private Context contextNullSafe;
    String city_args,description_args,given_on_args,
            head_args,image_link_args,key_args,location_args
            ,state_args,status_args,task_deadline_args,task_title_args,from,for_join,submitted,creator;
    TextView city_txt,description_txt,given_on_txt
            ,task_title_txt,head_name_txt,location_txt,state_txt
            ,heading_status,leading_text,viewmore,seen,task_deadline_txt
            ,added_task_count_txt,plus,members_text,approved_txt;
    ImageView location_img,image_bck,recyclerView_img,save,undo,del;
    LinearLayout linearLayout;
    ArrayList<String> added_task_list;
    ArrayList<String> members_list,seen_members_list,device_tokens;
    FirebaseAuth auth;
    FirebaseUser user;
    String identity="";
    RecyclerView recyclerView;
    ValueEventListener listener_1;
    DatabaseReference users_ref,group_ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_group_task, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //opening Bundle from adapter class;
        try {
            city_args = getArguments().getString("sending_city");
            description_args = getArguments().getString("sending_description");
            given_on_args = getArguments().getString("sending_given_on");
            head_args = getArguments().getString("sending_head");
            image_link_args = getArguments().getString("sending_imagelink");
            key_args = getArguments().getString("sending_key") + "";
            location_args = getArguments().getString("sending_location");
            state_args = getArguments().getString("sending_state");
            status_args = getArguments().getString("sending_status");
            task_deadline_args = getArguments().getString("sending_task_deadline");
            task_title_args = getArguments().getString("sending_task_title");
            from = getArguments().getString("sending_from");
            for_join = getArguments().getString("joining_request");
            submitted = getArguments().getString("submitted_or_not");
            creator = getArguments().getString("sending_task_creator");
        } catch (Exception e) {
            e.printStackTrace();
        }
        members_list=new ArrayList<>();
        seen_members_list=new ArrayList<>();
        device_tokens=new ArrayList<>();
        //getting current user.
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");
        //Binding view
        city_txt=view.findViewById(R.id.textView45);
        del=view.findViewById(R.id.del);
        description_txt=view.findViewById(R.id.textView75);
        given_on_txt=view.findViewById(R.id.textView41);
        head_name_txt=view.findViewById(R.id.textView82);
        lead_dp=view.findViewById(R.id.circleImageView);
        cardView3=view.findViewById(R.id.cardView3);
        photo_image=view.findViewById(R.id.image);
        location_txt=view.findViewById(R.id.textView92);
        state_txt=view.findViewById(R.id.textView93);
        heading_status=view.findViewById(R.id.textView47);
        task_deadline_txt=view.findViewById(R.id.textView43);
        task_title_txt=view.findViewById(R.id.textView73);
        leading_text=view.findViewById(R.id.leading_text);
        linearLayout=view.findViewById(R.id.linearLayout9);
        location_img=view.findViewById(R.id.imageView17);
        image_bck=view.findViewById(R.id.imageBack);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView_img=view.findViewById(R.id.imageView10);
        added_task_count_txt=view.findViewById(R.id.textView76);
        members_text=view.findViewById(R.id.deadline_text);
        per_1=view.findViewById(R.id.head1);
        per_2=view.findViewById(R.id.head2);
        per_3=view.findViewById(R.id.head3);
        plus=view.findViewById(R.id.plus);
        viewmore=view.findViewById(R.id.viewmore);
        seen=view.findViewById(R.id.seen);
        save=view.findViewById(R.id.imageSave);
        undo=view.findViewById(R.id.undo);
        approved_txt=view.findViewById(R.id.textView97);

        undo.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        approved_txt.setVisibility(View.GONE);

        per_1.setVisibility(View.GONE);
        per_2.setVisibility(View.GONE);
        per_3.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        plus.setVisibility(View.GONE);
        viewmore.setVisibility(View.GONE);
        seen.setVisibility(View.GONE);
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        group_ref= FirebaseDatabase.getInstance().getReference().child("group_task");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        if(creator!=null) {
            if (user.getUid().equals(creator) || identity.equals("admin")){
                del.setVisibility(View.VISIBLE);
            }
            else{
                del.setVisibility(View.GONE);
            }
        }
        if(head_args!=null) {
            linearLayout.setVisibility(View.VISIBLE);
            leading_text.setVisibility(View.VISIBLE);
            set_head();
        }else{
            leading_text.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
        location_img.setVisibility(View.GONE);
        location_txt.setVisibility(View.GONE);
        description_txt.setVisibility(View.GONE);
        if (location_args!=null) {
            if (!location_args.equals("")) {
                location_txt.setVisibility(View.VISIBLE);
                location_img.setVisibility(View.VISIBLE);
            }
        }
        if(description_args!=null) {
            if(!description_args.equals(""))
                description_txt.setVisibility(View.VISIBLE);
        }
        //setting values
        city_txt.setText(city_args);
        description_txt.setText(description_args);
        given_on_txt.setText(given_on_args);
        location_txt.setText(location_args);
        state_txt.setText(state_args);
        task_deadline_txt.setText(task_deadline_args);
        task_title_txt.setText(task_title_args);
        del.setOnClickListener(v->delete());
        if(image_link_args==null)
            photo_image.setVisibility(View.GONE);
        else {
            photo_image.setVisibility(View.VISIBLE);
            try {
                System.gc();
                Uri uri = Uri.parse(image_link_args);
                ImageRequest request = ImageRequest.fromUri(uri);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(photo_image.getController()).build();

                photo_image.setController(controller);
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
                        .placeholder(R.drawable.ic_image_holder)
                        .into(image);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                group_ref.removeEventListener(listener_1);
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        image_bck.setOnClickListener(v->onBackPressed());
        save.setOnClickListener(v->{
            if(from.equals("group_section")){
                if (head_args!=null)
                    if(user.getUid().equals(head_args))
                        submit();
                    else
                        MotionToast.Companion.darkColorToast(getActivity(),
                                "Submission denied",
                                "only group head can submit this task.",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                else
                    submit();
            }
            else if(from.equals("admin_grp_task_approval")){
                submit();
            }
        });
        undo.setOnClickListener(v->{
            if(from.equals("group_section")){
                if (head_args!=null)
                    if(user.getUid().equals(head_args))
                        Undo();
                    else
                        MotionToast.Companion.darkColorToast(getActivity(),
                                "Submission denied",
                                "only group head can undo this task.",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                else
                    Undo();
            }
            else if(from.equals("admin_grp_task_approval")){
                Undo();
            }
        });
        check_for_submission();
        viewmore.setOnClickListener(v->{
            if(members_list!=null) {
                ViewMembers viewMembers = new ViewMembers();
                Bundle args = new Bundle();
                args.putStringArrayList("members_list", members_list);
                viewMembers.setArguments(args);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer, viewMembers)
                        .addToBackStack(null)
                        .commit();
            }
        });
        if(identity.equals("admin") || identity.equals("chapter-head"))
            seen.setVisibility(View.VISIBLE);
        seen.setOnClickListener(v->{
            if(seen_members_list!=null) {
                group_task_seen group_task_seen = new group_task_seen();
                Bundle args = new Bundle();
                args.putStringArrayList("seen_members_list", seen_members_list);
                group_task_seen.setArguments(args);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer, group_task_seen)
                        .addToBackStack(null)
                        .commit();
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Group task",
                        "Not even a single member has seen this task.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
            }
        });
        new Handler(Looper.myLooper()).postDelayed(this::show_approve_task,1000);
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
            group_ref.child(key_args).removeValue();
            String path = "GroupTask/" + key_args  + ".png";
            if (image_link_args != null) {
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReference().child(path);
                storageReference.delete();

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
            else{
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    private void Undo() {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_for_sure);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.textView96);
        TextView yes=dialog.findViewById(R.id.textView95);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(v-> dialog.dismiss());
        yes.setOnClickListener(v-> {
            if(from.equals("group_section")) {
                if (members_list.contains(user.getUid())) {
                    group_ref.child(key_args).child("submitted").setValue("no");
                    group_ref.child(key_args).child("status").setValue("ongoing");
                }
                else{
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Access denied",
                            "Since you're not a member of this task.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
            }
            else if(from.equals("admin_grp_task_approval")){
                final boolean[] one_run = {true};
                group_ref.child(key_args).child("approved").removeValue();
                users_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (one_run[0]) {
                            for (int i = 0; i < members_list.size(); i++) {
                                if (snapshot.child(members_list.get(i)).child("progress").child("xp").exists()) {
                                    long xp = snapshot.child(members_list.get(i)).child("progress").child("xp").getValue(Long.class);
                                    if (xp - 20 >= 0)
                                        users_ref.child(members_list.get(i)).child("progress").child("xp").setValue(xp - 20);
                                }
                            }
                        }
                        one_run[0] =false;
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });
            }
            dialog.dismiss();
        });
    }

    private void check_for_submission() {
        listener_1= group_ref.child(key_args).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(from.equals("group_section")){
                    String check_submission=snapshot.child("submitted").getValue(String.class);
                    if(!snapshot.child("approved").exists()) {
                        approved_txt.setVisibility(View.GONE);
                        if (Objects.equals(check_submission, "no")) {
                            if(!identity.equals("guest")) {
                                save.setVisibility(View.VISIBLE);
                                undo.setVisibility(View.GONE);
                            }
                        } else if (Objects.equals(check_submission, "yes")) {
                            if(!identity.equals("guest")) {
                                undo.setVisibility(View.VISIBLE);
                                save.setVisibility(View.GONE);
                            }
                        }
                    }
                    else{
                        approved_txt.setVisibility(View.VISIBLE);
                    }
                }
                else if(from.equals("admin_grp_task_approval")){
                    if(!snapshot.child("approved").exists()) {
                        approved_txt.setVisibility(View.GONE);
                        save.setVisibility(View.VISIBLE);
                        undo.setVisibility(View.GONE);
                    }
                    else{
                        approved_txt.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                        undo.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }
    private void joining_request(){
        if(for_join!=null){
            if (for_join.equals("joining_request")){
                if(members_list.contains(user.getUid())){
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Already in group",
                            "You're already a member of this task.",
                            MotionToast.TOAST_INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
                else if(submitted!=null){
                    if (submitted.equals("no")) {
                        Dialog dialog = new Dialog(getContext());
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.dialog_for_sure);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView cancel=dialog.findViewById(R.id.textView96);
                        TextView yes=dialog.findViewById(R.id.textView95);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show();
                        cancel.setOnClickListener(v-> dialog.dismiss());
                        yes.setOnClickListener(v->{
                            int count_mem = members_list.size();
                            if (!members_list.contains(user.getUid())) {
                                group_ref.child(key_args).child("members").child(count_mem + "").setValue(user.getUid());
                                MotionToast.Companion.darkColorToast(getActivity(),
                                        "Joined Task",
                                        "You're now a member of this task'.",
                                        MotionToast.TOAST_SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        }
    }
    private void submit() {
        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_for_sure);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.textView96);
        TextView yes=dialog.findViewById(R.id.textView95);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(v-> dialog.dismiss());
        yes.setOnClickListener(v-> {
            if(from.equals("group_section")) {
                if (members_list.contains(user.getUid())) {
                    group_ref.child(key_args).child("submitted").setValue("yes");
                    group_ref.child(key_args).child("status").setValue("finished");
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Successfull",
                            "Task Submitted.",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
                else{
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Access denied",
                            "Since you're not a member of this task.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
            }
            else if(from.equals("admin_grp_task_approval")){
                final boolean[] one_run = {true};
                for (int tokens=0;tokens<device_tokens.size();tokens++){
                    Specific specific=new Specific();
                    specific.noti("Group task approved","Cheer's you got 20 points for the task - "+task_title_txt.getText().toString().trim(),
                            device_tokens.get(tokens));
                }
                group_ref.child(key_args).child("approved").setValue("true");
                users_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (one_run[0]) {
                            for (int i = 0; i < members_list.size(); i++) {
                                if (snapshot.child(members_list.get(i)).child("progress").child("xp").exists()) {
                                    long xp = snapshot.child(members_list.get(i)).child("progress").child("xp").getValue(Long.class);
                                    users_ref.child(members_list.get(i)).child("progress").child("xp").setValue(xp + 20);
                                } else {
                                    users_ref.child(members_list.get(i)).child("progress").child("xp").setValue(20);
                                }
                            }
                        }
                        one_run[0] =false;
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });
            }
            dialog.dismiss();
        });
    }

    private void onBackPressed() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        group_ref.removeEventListener(listener_1);
        ft.commit();

    }
    private void show_approve_task() {
        group_ref.child(key_args).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (snapshot.child("added_task").exists()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView_img.setVisibility(View.VISIBLE);
                        added_task_count_txt.setVisibility(View.VISIBLE);
                        String added_txt=snapshot.child("added_task").getChildrenCount()+" Added Task";
                        added_task_count_txt.setText(added_txt);
                        added_task_list = (ArrayList<String>) snapshot.child("added_task").getValue();
                        addedtaskAdapter addedtaskAdapter = new addedtaskAdapter(getContext(), added_task_list,"yellow");
                        addedtaskAdapter.notifyDataSetChanged();
                        try {
                            recyclerView.setAdapter(addedtaskAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        added_task_count_txt.setVisibility(View.GONE);
                        recyclerView_img.setVisibility(View.GONE);
                    }
                    if(snapshot.child("members").exists()){
                        members_list= (ArrayList<String>) snapshot.child("members").getValue();
                        seen_members_list= (ArrayList<String>) snapshot.child("seen").getValue();
                        if (members_list.contains(user.getUid()))
                        {
                            if (seen_members_list!=null){
                                if (!seen_members_list.contains(user.getUid()))
                                {
                                    int count_mem = seen_members_list.size();
                                    group_ref.child(key_args).child("seen").child(count_mem + "").setValue(user.getUid());
                                }
                            }
                            else{
                                group_ref.child(key_args).child("seen").child(0 + "").setValue(user.getUid());
                            }
                        }
                        joining_request();
                        assert members_list != null;
                        get_members(members_list);
                        get_device_tokens();
                    }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void get_device_tokens() {
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int device_token=0;device_token<members_list.size();device_token++){
                    if(snapshot.child(members_list.get(device_token)).child("token").exists())
                        device_tokens.add(snapshot.child(members_list.get(device_token)).child("token").getValue(String.class));
                    else
                        device_tokens.add("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void get_members(ArrayList<String> members_list) {
        String txt="Members"+"("+members_list.size()+")";
        members_text.setText(txt);
        freeMemory();
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (int j=0;j<members_list.size();j++){
                    if (j == 0) {
                        per_1.setVisibility(View.VISIBLE);
                        String dp1=snapshot.child(members_list.get(j)).child("dplink").getValue(String.class)+"";
                        Uri uri = Uri.parse(dp1);
                        ImageRequest request = ImageRequest.fromUri(uri);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(per_1.getController()).build();

                        per_1.setController(controller);
                        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp1)
                                        .override(80,80)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp1)
                                        .override(40,40)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    else if(j==1){
                        per_2.setVisibility(View.VISIBLE);
                        String dp2=snapshot.child(members_list.get(j)).child("dplink").getValue(String.class)+"";
                        Uri uri = Uri.parse(dp2);
                        ImageRequest request = ImageRequest.fromUri(uri);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(per_2.getController()).build();

                        per_2.setController(controller);
                        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp2)
                                        .override(80,80)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp2)
                                        .override(40,40)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    else if(j==2){
                        per_3.setVisibility(View.VISIBLE);
                        cardView3.setVisibility(View.VISIBLE);
                        String dp3=snapshot.child(members_list.get(j)).child("dplink").getValue(String.class)+"";
                        Uri uri = Uri.parse(dp3);
                        ImageRequest request = ImageRequest.fromUri(uri);

                        DraweeController controller = Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(per_3.getController()).build();

                        per_3.setController(controller);
                        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp3)
                                        .override(80,80)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head3);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            try{
                                Glide.with(getContextNullSafety())
                                        .asBitmap()
                                        .load(dp3)
                                        .override(40,40)
                                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                        .into(head3);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    else if (j==3){
                        viewmore.setVisibility(View.VISIBLE);
                        plus.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void set_head() {
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String head_name=snapshot.child(head_args).child("name").getValue(String.class);
                head_name_txt.setText(head_name);
                if(snapshot.child(head_args).child("dplink").exists()){
                    String headdp=snapshot.child(head_args).child("dplink").getValue(String.class);
                    Uri uri = Uri.parse(headdp);
                    ImageRequest request = ImageRequest.fromUri(uri);

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(lead_dp.getController()).build();

                    lead_dp.setController(controller);
                    /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try{
                            Glide.with(getContextNullSafety())
                                    .asBitmap()
                                    .load(headdp)
                                    .override(80,80)
                                    .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                    .into(head_dp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try{
                            Glide.with(getContextNullSafety())
                                    .asBitmap()
                                    .load(headdp)
                                    .override(40,40)
                                    .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                    .into(head_dp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}