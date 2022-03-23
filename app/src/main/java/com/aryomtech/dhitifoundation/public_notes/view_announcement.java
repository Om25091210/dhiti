package com.aryomtech.dhitifoundation.public_notes;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.public_notes.Adapter.seenAdapter;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;


public class view_announcement extends Fragment {

    View view;
    private Context contextNullSafe;
    String category,city,content,date,head
            ,imagelink,pdflink,writtenby,url,an_push_key,uid,dplink;
    TextView title_txt,created_by_txt,last_modified
            ,city_txt,category_txt,url_txt,pdf_button,content_txt;
    ImageView profile_image,info,copy_txt;
    FirebaseAuth auth;
    FirebaseUser user;
    Long xp;
    SimpleDraweeView draweeView;
    ArrayList<String> seen_people_pictures,seen_people_name,seen_people_key,seen_people_time;
    String dp="";
    boolean once=true,share_ready=false,first_run=true;
    DatabaseReference announcement_ref,user_ref;
    ValueEventListener listener,valueEventListener,eventListener,eventListener0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_announcement, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //TODO: add sorting for seen.
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        seen_people_pictures=new ArrayList<>();
        seen_people_name=new ArrayList<>();
        seen_people_key=new ArrayList<>();
        seen_people_time=new ArrayList<>();

        announcement_ref= FirebaseDatabase.getInstance().getReference().child("Announcement");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");

        //fetching values from another intent.
        assert getArguments() != null;
        category=getArguments().getString("category0101");
        city=getArguments().getString("city0202");
        content=getArguments().getString("content0303");
        date=getArguments().getString("date0404");
        head=getArguments().getString("head0505");
        imagelink=getArguments().getString("imagelink0606");
        pdflink=getArguments().getString("pdflink0707");
        writtenby=getArguments().getString("writtenby0808");
        url=getArguments().getString("url0909");
        an_push_key=getArguments().getString("an_push_key_1010");
        uid=getArguments().getString("uid_2020");
        dplink=getArguments().getString("profile_dp_3030");

        //binding views
        draweeView  = view.findViewById(R.id.imageNote);
        title_txt=view.findViewById(R.id.textView39);
        created_by_txt=view.findViewById(R.id.textView41);
        last_modified=view.findViewById(R.id.textView43);
        city_txt=view.findViewById(R.id.textView45);
        category_txt=view.findViewById(R.id.textView38);
        url_txt=view.findViewById(R.id.textWebURL);
        pdf_button=view.findViewById(R.id.textView25);
        content_txt=view.findViewById(R.id.textView46);
        //image_view=view.findViewById(R.id.imageNote);
        copy_txt = view.findViewById(R.id.imageView7);
        profile_image=view.findViewById(R.id.profile_image);
        info=view.findViewById(R.id.info);

        info.setVisibility(View.GONE);

        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        user_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child("progress").exists()) {
                    xp=snapshot.child("progress").child("xp").getValue(Long.class);
                    if(xp!=null && xp-20L>=0) {
                        int level = Integer.parseInt(getLevel(xp / 100f));
                        if(level<=10)
                            xp = xp - 20L;
                        else if(level<=20)
                            xp = xp - 15L;
                        else if(level<=30)
                            xp = xp - 10L;
                        else if(level<=40)
                            xp = xp - 5L;
                        else if(level<=50)
                            xp = xp - 4L;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        onseen();
        fetchdata();
        if(dplink!=null){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try{
                    Glide.with(getContextNullSafety())
                            .asBitmap()
                            .load(dplink)
                            .override(80,80)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(profile_image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                try{
                    Glide.with(getContextNullSafety())
                            .asBitmap()
                            .load(dplink)
                            .override(40,40)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(profile_image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        info.setOnClickListener(v->trigger_seen());
        //back
        view.findViewById(R.id.imageBack).setOnClickListener(v->onBackPressed());
        //setting values
        title_txt.setText(head);
        created_by_txt.setText(writtenby);
        last_modified.setText(date);
        city_txt.setText(city);
        category_txt.setText(category);
        if(url!=null) {
            url_txt.setVisibility(View.VISIBLE);
            url_txt.setText(url);
        }
        else
            url_txt.setVisibility(View.GONE);

        content_txt.setText(content);
        copy_txt.setOnClickListener(v->{
            ClipboardManager clipboardManager= (ClipboardManager) getContextNullSafety().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData=ClipData.newPlainText("content",content_txt.getText().toString().trim());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        if(pdflink==null)
            pdf_button.setVisibility(View.GONE);
        pdf_button.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri fileuri =  Uri.parse(pdflink) ;
            intent.setDataAndType(fileuri,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent in = Intent.createChooser(intent,"open file");
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
        });
        if(imagelink==null)
            draweeView.setVisibility(View.GONE);
        else {
            draweeView.setVisibility(View.VISIBLE);
            try {
                System.gc();
                Uri uri = Uri.parse(imagelink);
                ImageRequest request = ImageRequest.fromUri(uri);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(draweeView.getController()).build();

                draweeView.setController(controller);
                /*Glide.with(getContextNullSafety())
                        .asBitmap()
                        .load(imagelink)
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
                                        Glide.with(getContextNullSafety())
                                                .asBitmap()
                                                .load(imagelink)
                                                .into(image_view);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, 2000);
                                return false;
                            }
                        })
                        .placeholder(R.drawable.ic_image_holder)
                        .into(image_view);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initMiscellaneous();

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

        return view;
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
    private void onBackPressed() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }

        ft.commit();

    }

    private void trigger_seen() {
        Dialog dialog_loading = new Dialog(getContext());
        dialog_loading.setCancelable(true);
        dialog_loading.setContentView(R.layout.loading_dialog);
        LottieAnimationView loading=dialog_loading.findViewById(R.id.animate);
        loading.setAnimation("circular_loading.json");
        dialog_loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_loading.show();
        final boolean[] restrict_once = {true};
        valueEventListener=user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(restrict_once[0]) {
                    for (int k = 0; k < seen_people_key.size(); k++) {
                        String name_str = snapshot.child(seen_people_key.get(k)).child("name").getValue(String.class);
                        if (!seen_people_name.contains(name_str)) {
                            seen_people_name.add(name_str);
                        }
                    }
                    Dialog dialog=new Dialog(getContext());
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.show_seen_info);
                    ImageView close=dialog.findViewById(R.id.imageView6);
                    close.setOnClickListener(v->{
                        dialog.dismiss();
                        freeMemory();
                        close.setOnClickListener(null);
                    });
                    RecyclerView seen_rv=dialog.findViewById(R.id.seen_rv);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.show_bg_seen);
                    dialog.getWindow().setGravity(Gravity.BOTTOM);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                    seen_rv.setItemViewCacheSize(20);
                    seen_rv.setLayoutManager(linearLayoutManager);
                    seenAdapter seenAdapter=new seenAdapter(seen_people_pictures,getContext(),seen_people_name,seen_people_time);
                    seen_rv.setAdapter(seenAdapter);
                    freeMemory();
                    dialog_loading.dismiss();
                    dialog.show();
                }
                restrict_once[0] =false;
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
    private void fetchdata() {

        listener=announcement_ref.child(an_push_key).child("seenby").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    if(Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("dp").getValue(String.class), "") && !seen_people_pictures.contains(snapshot.child(Objects.requireNonNull(ds.getKey())).child("dp").getValue(String.class))){
                        seen_people_pictures.add(snapshot.child(ds.getKey()).child("dp").getValue(String.class));
                        seen_people_time.add(snapshot.child(ds.getKey()).child("time").getValue(String.class));
                    }
                    else{
                        seen_people_pictures.add("");
                        seen_people_time.add(snapshot.child(ds.getKey()).child("time").getValue(String.class));
                    }
                    if(!seen_people_key.contains(ds.getKey())){
                        seen_people_key.add(ds.getKey());
                    }
                }
                if(seen_people_pictures.size()>0) {
                    info.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = view.findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutshare).setOnClickListener(v->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if(imagelink==null) {
                String title =head+"\n\n"+"Created by : "+writtenby+
                        "\n"+"Date : "+date+
                        "\n"+"Category : "+category+"\n\n"+"City : "+city+category+"\n\n"+content; //Text to be shared
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getContextNullSafety().getPackageName());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
            else{
                if(share_ready) {
                    /*BitmapDrawable drawable = (BitmapDrawable) image_view.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();*/
                    Uri bitmapUri = getLocalBitmapUri();
                    String title = head + "\n\n" + "Created by : " + writtenby +
                            "\n" + "Date : " + date +
                            "\n" + "Category : " + category + "\n\n" + "City : " + city + "\n\n" + content; //Text to be shared
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    intent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + "This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getContextNullSafety().getPackageName());

                    startActivity(Intent.createChooser(intent, "Share"));
                }
                else{
                    Toast.makeText(getContext(), "Image not loaded.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(uid.equals(user.getUid())){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutAddImage).setVisibility(View.VISIBLE);
        }
        else{
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.GONE);
            layoutMiscellaneous.findViewById(R.id.layoutAddImage).setVisibility(View.GONE);
        }
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v->{
            freeMemory();
            create_announcement create_announcement=new create_announcement();
            Bundle args=new Bundle();
            args.putString("category_edit_01",category);
            args.putString("city_edit_02",city);
            args.putString("content_edit_03",content);
            args.putString("date_edit_04",date);
            args.putString("head_edit_05",head);
            args.putString("imagelink_edit_06",imagelink);
            args.putString("pdflink_edit_07",pdflink);
            args.putString("url_edit_09",url);
            args.putString("writtenby_edit_01020",writtenby);
            args.putString("anpushkey_edit_10",an_push_key);
            args.putString("which_intent_789654","view_note");
            create_announcement.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,create_announcement)
                    .addToBackStack(null)
                    .commit();
        });
        layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(v->{
            if(uid.equals(user.getUid())) {
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
                    String url = "Announcement/" + writtenby + head + date + ".png";
                    String url_pdf = "Announcement/" + writtenby + head + date + ".pdf";

                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Deleted!!",
                            "Deleted successfully. Swipe down to refresh",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));

                    if (imagelink != null) {
                        StorageReference storageReference =
                                FirebaseStorage.getInstance().getReference().child(url);
                        storageReference.delete();
                    }
                    if (pdflink != null) {
                        StorageReference storageReference1 =
                                FirebaseStorage.getInstance().getReference().child(url_pdf);
                        storageReference1.delete();
                    }
                    user_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);
                    announcement_ref.child(an_push_key).removeValue();
                    dialog.dismiss();
                    onBackPressed();
                });
            }

        });
    }
/*    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        final String randomKey = UUID.randomUUID().toString();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, ""+randomKey, null);
        return Uri.parse(path);
    }*/
    public Uri getLocalBitmapUri() {
        // Extract Bitmap from ImageView drawable
        draweeView.setDrawingCacheEnabled(true);
        draweeView.buildDrawingCache();
        Bitmap bitmap = draweeView.getDrawingCache();
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

    private void onseen(){
        if (!user.getUid().equals(uid)) {
            eventListener0=announcement_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(first_run) {
                        if (snapshot.child(an_push_key).child("uid").exists() && !snapshot.child(an_push_key).child("seenby").child(user.getUid()).exists()) {
                            eventListener=user_ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (once) {
                                        if (snapshot.child(user.getUid()).child("dplink").exists()) {
                                            dp = snapshot.child(user.getUid()).child("dplink").getValue(String.class);
                                            announcement_ref.child(an_push_key).child("seenby")
                                                    .child(user.getUid()).child("dp").setValue(dp);
                                            announcement_ref.child(an_push_key).child("seenby")
                                                    .child(user.getUid()).child("time")
                                                    .setValue(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
                                        } else {
                                            announcement_ref.child(an_push_key).child("seenby").child(user.getUid())
                                                    .child("dp").setValue("");
                                            announcement_ref.child(an_push_key).child("seenby")
                                                    .child(user.getUid()).child("time")
                                                    .setValue(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
                                        }
                                    }
                                    once = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                    first_run=false;
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

        category=null;
        city=null;
        content=null;
        date=null;
        head=null;
        imagelink=null;
        pdflink=null;
        writtenby=null;
        url=null;
        an_push_key=null;
        uid=null;
        dplink=null;

        title_txt=null;
        created_by_txt=null;
        last_modified=null;
        city_txt=null;
        category_txt=null;
        url_txt=null;
        content_txt=null;

        profile_image.setImageBitmap(null);
        info.setOnClickListener(null);
        copy_txt.setOnClickListener(null);
        pdf_button.setOnClickListener(null);

        seen_people_pictures.clear();
        seen_people_name.clear();
        seen_people_key.clear();
        seen_people_time.clear();

        if(announcement_ref!=null && listener!=null)
            announcement_ref.removeEventListener(listener);
        if(user_ref!=null && valueEventListener!=null)
            user_ref.removeEventListener(valueEventListener);
        if(announcement_ref!=null && eventListener!=null)
            announcement_ref.removeEventListener(eventListener0);
        if(user_ref!=null && eventListener!=null)
            user_ref.removeEventListener(eventListener);

    }
}