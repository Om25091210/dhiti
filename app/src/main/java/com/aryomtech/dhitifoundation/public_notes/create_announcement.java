package com.aryomtech.dhitifoundation.public_notes;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;

public class create_announcement extends Fragment {

    View view;
    private Context contextNullSafe;
    String category_in,city_in,content_in,date_in,head_in
            ,imagelink_in,pdflink_in,url_in,an_push_key_in,writtenby;

    EditText edtNoteContent;
    private ImageView imageNote;
    private EditText inputNoteTitle, category,city;
    private TextView textDataView, textWebURL;
    private LinearLayout layoutWebURL;
    Bitmap imageBitmap;
    TextView attachpdf;
    FirebaseAuth auth;
    FirebaseUser user;

    //TODO: save progress writting when going back.
    //TODO: SEEN OR NOT.(note public).

    //TODO: nsa help donation and respond feature.

    //TODO: guest after form website.
    //TODO>>> user data will also carry wheather the user is guest or member

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    public static final int PDF_SELECTION_CODE = 99;
    public static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private AlertDialog dialogAddURL;
    DatabaseReference announcement_ref,user_ref;
    Uri selected_uri_pdf=Uri.parse("");
    ImageView remove_pdf,profile_image,removeImage,copy_txt;
    TextView name;
    String pdf_link,image_link,selectedImagePath="";
    LottieAnimationView save,voice_text;
    String dp="";
    String which_intent;
    Dialog dialog;
    Long xp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_create_announcement, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));

        freeMemory();
        //getting intent from view_note when edit announcement is clicked.

        try {
            category_in = getArguments().getString("category_edit_01");
            city_in = getArguments().getString("city_edit_02");
            content_in = getArguments().getString("content_edit_03");
            date_in = getArguments().getString("date_edit_04");
            head_in = getArguments().getString("head_edit_05");
            imagelink_in = getArguments().getString("imagelink_edit_06");
            pdflink_in = getArguments().getString("pdflink_edit_07");
            url_in = getArguments().getString("url_edit_09");
            an_push_key_in = getArguments().getString("anpushkey_edit_10");
            which_intent = getArguments().getString("which_intent_789654");
            writtenby = getArguments().getString("writtenby_edit_01020");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        announcement_ref= FirebaseDatabase.getInstance().getReference().child("Announcement");
        user_ref=FirebaseDatabase.getInstance().getReference().child("users");

        ImageView imageBack =view.findViewById(R.id.imageBack);
        inputNoteTitle = view.findViewById(R.id.inputNoteTitle);
        category=view.findViewById(R.id.editTextTextMultiLine13);
        textDataView = view.findViewById(R.id.textDateTime);
        city = view.findViewById(R.id.city);
        voice_text = view.findViewById(R.id.voice_text);
        save=view.findViewById(R.id.save);
        imageNote = view.findViewById(R.id.imageNote);
        textWebURL = view.findViewById(R.id.textWebURL);
        name=view.findViewById(R.id.textView24);
        removeImage=view.findViewById(R.id.imageRemoveImage);
        profile_image=view.findViewById(R.id.profile_image);
        remove_pdf = view.findViewById(R.id.remove_pdf);
        copy_txt = view.findViewById(R.id.imageView7);
        remove_pdf.setVisibility(View.GONE);
        layoutWebURL = view.findViewById(R.id.layoutWebURL);
        layoutWebURL.setVisibility(View.GONE);

        edtNoteContent= view.findViewById(R.id.inputNote);

        attachpdf=view.findViewById(R.id.textView25);
        attachpdf.setVisibility(View.GONE);

        remove_pdf.setOnClickListener(v->remove_PDF());
        name.setText(user.getDisplayName());
        save.setOnClickListener(v->save());

        copy_txt.setOnClickListener(v->{
            ClipboardManager clipboardManager= (ClipboardManager) getContextNullSafety().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData=ClipData.newPlainText("content",edtNoteContent.getText().toString().trim());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "copied to clipboard", Toast.LENGTH_SHORT).show();
        });
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        ArrayList<String> result_voice = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String voice_text_Str=edtNoteContent.getText().toString().trim()+" "+result_voice.get(0)+"";
                        edtNoteContent.setText(voice_text_Str);
                    }
                }
        );
        voice_text.setOnClickListener(v->{
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");

            try {
                startActivityForResult.launch(intent);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getContext(), "Sorry your device not supported", Toast.LENGTH_SHORT).show();
            }
        });
        user_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name_str = snapshot.child(user.getUid()).child("name").getValue(String.class);
                try {
                    name.setText(name_str);
                }catch (Exception e) {
                        e.printStackTrace();
                }
                    if (snapshot.child(user.getUid()).child("progress").exists()) {
                        xp = snapshot.child(user.getUid()).child("progress").child("xp").getValue(Long.class);
                        if (xp != null) {
                            int level = Integer.parseInt(getLevel(xp / 100f));
                            if (level <= 10)
                                xp = xp + 20L;
                            else if (level <= 20)
                                xp = xp + 15L;
                            else if (level <= 30)
                                xp = xp + 10L;
                            else if (level <= 40)
                                xp = xp + 5L;
                            else if (level <= 50)
                                xp = xp + 4L;
                        }
                    } else
                        xp = 20L;
                    if (snapshot.child(user.getUid()).child("dplink").exists()) {
                        dp = snapshot.child(user.getUid()).child("dplink").getValue(String.class);
                        try {
                            Glide.with(getContextNullSafety()).asBitmap()
                                    .load(dp)
                                    .thumbnail(0.1f)
                                    .override(80, 80)
                                    .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                    .into(profile_image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initMiscellaneous();

        imageBack.setOnClickListener(v -> onBackPressed());

        new Handler(Looper.myLooper()).postDelayed(this::freeMemory,2500);

        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        textDataView.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        view.findViewById(R.id.imageRemoveWebURL).setOnClickListener(v -> {
            textWebURL.setText("");
            layoutWebURL.setVisibility(View.GONE);

        });
        removeImage.setOnClickListener(v -> removeImage());
        if(which_intent!=null){
            inputNoteTitle.setText(head_in);
            if(url_in!=null)
                layoutWebURL.setVisibility(View.VISIBLE);
            textWebURL.setText(url_in);
            if(category_in!=null)
                category.setText(category_in);
            if(city_in!=null)
                city.setText(city_in);
            if(pdflink_in!=null) {
                String str_pdf = "PDF";
                attachpdf.setVisibility(View.VISIBLE);
                remove_pdf.setVisibility(View.VISIBLE);
                attachpdf.setText(str_pdf);
            }
            if(content_in!=null)
                edtNoteContent.setText(content_in);
            if(imagelink_in!=null) {
                imageNote.setVisibility(View.VISIBLE);
                removeImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(requireActivity())
                            .asBitmap()
                            .load(imagelink_in)
                            .placeholder(R.drawable.ic_good_team)
                            .into(imageNote);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                removeImage.setVisibility(View.GONE);
            }
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

    public String getLevel(Float f){
        String txt = Float.toString(f);
        for(int k=0;k<txt.length();k++){
            if(txt.charAt(k)=='.'){
                txt=txt.substring(0,k);
            }
        }
        return txt;
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

    void remove_PDF(){
        attachpdf.setVisibility(View.GONE);
        remove_pdf.setVisibility(View.GONE);
        selected_uri_pdf=Uri.parse("");
        if(pdflink_in!=null){
            pdflink_in="";
        }
    }
    void removeImage(){
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(View.GONE);
        view.findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        selectedImagePath = "";
        if(imagelink_in!=null)
            imagelink_in="";
    }
    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void save() {
        String push_key;
        String control_val;
        if(which_intent==null) {
            //save
            push_key = announcement_ref.push().getKey();
            control_val="no";
        }
        else{
            //update
            push_key = an_push_key_in;
            control_val="yes";
        }
        update_or_save(push_key,control_val);
    }
    private void update_or_save(String push_key, String control_val){
        if (!inputNoteTitle.getText().toString().trim().equals("")) {
            if (!category.getText().toString().trim().equals("")) {
                if (!city.getText().toString().trim().equals("")) {
                    if (!edtNoteContent.getText().toString().trim().equals("")) {

                        if (!selectedImagePath.equals("") && !Objects.equals(selected_uri_pdf,Uri.parse(""))) {      //both are attached

                            topic topic=new topic();
                            topic.noti("New announcement",name.getText().toString().trim()+" posted a new announcement- "+inputNoteTitle.getText().toString().trim());

                            dialog = new Dialog(requireContext());
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("uploaded.json");
                            dialog.show();
                            assert push_key != null;
                            announcement_ref.child(push_key).child("writtenby").setValue(name.getText().toString().trim());
                            announcement_ref.child(push_key).child("an_push_key").setValue(push_key);
                            announcement_ref.child(push_key).child("uid").setValue(user.getUid());
                            announcement_ref.child(push_key).child("dplink").setValue(dp);
                            user_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);
                            announcement_ref.child(push_key).child("head").setValue(inputNoteTitle.getText().toString().trim());
                            announcement_ref.child(push_key).child("date").setValue(textDataView.getText().toString());
                            announcement_ref.child(push_key).child("category").setValue(category.getText().toString().trim());
                            announcement_ref.child(push_key).child("city").setValue(city.getText().toString().trim());
                            announcement_ref.child(push_key).child("content").setValue(edtNoteContent.getText().toString().trim());
                            if (!textWebURL.getText().toString().trim().equals(""))
                                announcement_ref.child(push_key).child("url").setValue(textWebURL.getText().toString().trim());
                            if(control_val.equals("yes")){
                                if(textWebURL.getText().toString().trim().equals("") && url_in!=null)
                                    announcement_ref.child(push_key).child("url").removeValue();
                            }
                            String imagepath = "Announcement/" + name.getText().toString().trim() + inputNoteTitle.getText().toString().trim() + textDataView.getText().toString() + ".png";

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                            /*final String randomKey = UUID.randomUUID().toString();
                            BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                            Bitmap bitmap_up = drawable.getBitmap();
                            String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

                            try {
                                InputStream stream = new FileInputStream(new File(selectedImagePath));

                                storageReference.putStream(stream)
                                        .addOnSuccessListener(taskSnapshot ->
                                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                        task -> {
                                                            image_link = Objects.requireNonNull(task.getResult()).toString();

                                                            String pdfstamp = name.getText().toString().trim() + inputNoteTitle.getText().toString().trim() + textDataView.getText().toString();
                                                            String pdfpath = "Announcement/";
                                                            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);
                                                            final StorageReference filepath = storageReference1.child(pdfstamp + "." + "pdf");
                                                            filepath.putFile(selected_uri_pdf)
                                                                    .addOnSuccessListener(taskSnapshot1 ->
                                                                            taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                                    task1 -> {
                                                                                        pdf_link = Objects.requireNonNull(task1.getResult()).toString();

                                                                                        announcement_ref.child(push_key).child("imagelink").setValue(image_link);
                                                                                        announcement_ref.child(push_key).child("pdflink").setValue(pdf_link);
                                                                                        dialog.dismiss();
                                                                                        MotionToast.Companion.darkColorToast(getActivity(),
                                                                                                "Done!!",
                                                                                                "Swipe down to refresh",
                                                                                                MotionToast.TOAST_SUCCESS,
                                                                                                MotionToast.GRAVITY_BOTTOM,
                                                                                                MotionToast.LONG_DURATION,
                                                                                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
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
                        } else if (!selectedImagePath.equals("")) {      //pdf is not attached

                            topic topic=new topic();
                            topic.noti("New announcement",name.getText().toString().trim()+" posted a new announcement- "+inputNoteTitle.getText().toString().trim());

                            dialog = new Dialog(requireContext());
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("uploaded.json");
                            dialog.show();

                            assert push_key != null;
                            announcement_ref.child(push_key).child("writtenby").setValue(name.getText().toString().trim());
                            announcement_ref.child(push_key).child("an_push_key").setValue(push_key);
                            announcement_ref.child(push_key).child("uid").setValue(user.getUid());
                            announcement_ref.child(push_key).child("dplink").setValue(dp);
                            user_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);
                            announcement_ref.child(push_key).child("head").setValue(inputNoteTitle.getText().toString().trim());
                            announcement_ref.child(push_key).child("date").setValue(textDataView.getText().toString());
                            announcement_ref.child(push_key).child("category").setValue(category.getText().toString().trim());
                            announcement_ref.child(push_key).child("city").setValue(city.getText().toString().trim());
                            announcement_ref.child(push_key).child("content").setValue(edtNoteContent.getText().toString().trim());
                            if (!textWebURL.getText().toString().trim().equals(""))
                                announcement_ref.child(push_key).child("url").setValue(textWebURL.getText().toString().trim());
                            if(control_val.equals("yes")){
                                if(textWebURL.getText().toString().trim().equals("") && url_in!=null)
                                    announcement_ref.child(push_key).child("url").removeValue();
                            }
                            String imagepath = "Announcement/" + name.getText().toString().trim() + inputNoteTitle.getText().toString().trim() + textDataView.getText().toString() + ".png";

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                            /*final String randomKey = UUID.randomUUID().toString();
                            BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                            Bitmap bitmap_up = drawable.getBitmap();
                            String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/
                            try {
                                InputStream stream = new FileInputStream(new File(selectedImagePath));

                                storageReference.putStream(stream)
                                        .addOnSuccessListener(taskSnapshot ->
                                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                        task -> {
                                                            image_link = Objects.requireNonNull(task.getResult()).toString();
                                                            announcement_ref.child(push_key).child("imagelink").setValue(image_link);
                                                            if (control_val.equals("yes") && pdflink_in != null) {
                                                                if (pdflink_in.equals(""))
                                                                    announcement_ref.child(push_key).child("pdflink").removeValue();
                                                            }
                                                            dialog.dismiss();
                                                            MotionToast.Companion.darkColorToast(getActivity(),
                                                                    "Done!!",
                                                                    "Swipe down to refresh",
                                                                    MotionToast.TOAST_SUCCESS,
                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                    MotionToast.LONG_DURATION,
                                                                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                                            FragmentManager fm = ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                                            FragmentTransaction ft = fm.beginTransaction();
                                                            if (fm.getBackStackEntryCount() > 0) {
                                                                fm.popBackStack();
                                                            }

                                                            ft.commit();
                                                        }));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!Objects.equals(selected_uri_pdf,Uri.parse(""))) {     //image is not attached

                            topic topic=new topic();
                            topic.noti("New announcement",name.getText().toString().trim()+" posted a new announcement- "+inputNoteTitle.getText().toString().trim());

                            dialog = new Dialog(requireContext());
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("uploaded.json");
                            dialog.show();
                            assert push_key != null;
                            announcement_ref.child(push_key).child("writtenby").setValue(name.getText().toString().trim());
                            announcement_ref.child(push_key).child("an_push_key").setValue(push_key);
                            announcement_ref.child(push_key).child("uid").setValue(user.getUid());
                            announcement_ref.child(push_key).child("dplink").setValue(dp);
                            user_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);
                            announcement_ref.child(push_key).child("head").setValue(inputNoteTitle.getText().toString().trim());
                            announcement_ref.child(push_key).child("date").setValue(textDataView.getText().toString());
                            announcement_ref.child(push_key).child("category").setValue(category.getText().toString().trim());
                            announcement_ref.child(push_key).child("city").setValue(city.getText().toString().trim());
                            announcement_ref.child(push_key).child("content").setValue(edtNoteContent.getText().toString().trim());
                            if (!textWebURL.getText().toString().trim().equals(""))
                                announcement_ref.child(push_key).child("url").setValue(textWebURL.getText().toString().trim());
                            if(control_val.equals("yes")){
                                if(textWebURL.getText().toString().trim().equals("") && url_in!=null)
                                    announcement_ref.child(push_key).child("url").removeValue();
                            }
                            String pdfstamp = name.getText().toString().trim() + inputNoteTitle.getText().toString().trim() + textDataView.getText().toString();
                            String pdfpath = "Announcement/";
                            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);
                            final StorageReference filepath = storageReference1.child(pdfstamp + "." + "pdf");
                            filepath.putFile(selected_uri_pdf)
                                    .addOnSuccessListener(taskSnapshot1 ->
                                            taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                                    task1 -> {
                                                        pdf_link = Objects.requireNonNull(task1.getResult()).toString();
                                                        announcement_ref.child(push_key).child("pdflink").setValue(pdf_link);
                                                        if (control_val.equals("yes") && imagelink_in != null) {
                                                            if (imagelink_in.equals(""))
                                                                announcement_ref.child(push_key).child("imagelink").removeValue();
                                                        }
                                                        dialog.dismiss();
                                                        MotionToast.Companion.darkColorToast(getActivity(),
                                                                "Done!!",
                                                                "Swipe down to refresh",
                                                                MotionToast.TOAST_SUCCESS,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.LONG_DURATION,
                                                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                                        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                                        FragmentTransaction ft=fm.beginTransaction();
                                                        if(fm.getBackStackEntryCount()>0) {
                                                            fm.popBackStack();
                                                        }

                                                        ft.commit();
                                                    }));
                        } else {    // both are not attached

                            topic topic=new topic();
                            topic.noti("New announcement",name.getText().toString().trim()+" posted a new announcement- "+inputNoteTitle.getText().toString().trim());

                            dialog = new Dialog(requireContext());
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("uploaded.json");
                            dialog.show();

                            assert push_key != null;
                            announcement_ref.child(push_key).child("writtenby").setValue(name.getText().toString().trim());
                            announcement_ref.child(push_key).child("an_push_key").setValue(push_key);
                            announcement_ref.child(push_key).child("uid").setValue(user.getUid());
                            announcement_ref.child(push_key).child("dplink").setValue(dp);
                            user_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);
                            announcement_ref.child(push_key).child("head").setValue(inputNoteTitle.getText().toString().trim());
                            announcement_ref.child(push_key).child("date").setValue(textDataView.getText().toString());
                            announcement_ref.child(push_key).child("category").setValue(category.getText().toString().trim());
                            announcement_ref.child(push_key).child("city").setValue(city.getText().toString().trim());
                            announcement_ref.child(push_key).child("content").setValue(edtNoteContent.getText().toString().trim());
                            if (!textWebURL.getText().toString().trim().equals(""))
                                announcement_ref.child(push_key).child("url").setValue(textWebURL.getText().toString().trim());
                            if(control_val.equals("yes")){
                                if(textWebURL.getText().toString().trim().equals("") && url_in!=null)
                                    announcement_ref.child(push_key).child("url").removeValue();
                                if(pdflink_in!=null) {
                                    if (pdflink_in.equals(""))
                                        announcement_ref.child(push_key).child("pdflink").removeValue();
                                }
                                if(imagelink_in!=null) {
                                    if (imagelink_in.equals(""))
                                        announcement_ref.child(push_key).child("imagelink").removeValue();
                                }
                            }
                            long delayInMillis = 1700;
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    MotionToast.Companion.darkColorToast(getActivity(),
                                            "Done!!",
                                            "Swipe down to refresh",
                                            MotionToast.TOAST_SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                                    FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                                    FragmentTransaction ft=fm.beginTransaction();
                                    if(fm.getBackStackEntryCount()>0) {
                                        fm.popBackStack();
                                    }

                                    ft.commit();
                                }
                            }, delayInMillis);
                        }

                    } else {
                        MotionToast.Companion.darkColorToast(requireActivity(),
                                "Empty!",
                                "Announcement content required.",
                                MotionToast.TOAST_WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
                    }
                } else {
                    MotionToast.Companion.darkColorToast(requireActivity(),
                            "Empty!",
                            "City required.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
                }
            } else {
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Empty!",
                        "Category required.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
            }
        } else {
            MotionToast.Companion.darkColorToast(requireActivity(),
                    "Empty!",
                    "Announcement title required.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
        }
    }

    public void initMiscellaneous(){
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

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            //Ask for permission
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });


        layoutMiscellaneous.findViewById(R.id.layoutshare).setOnClickListener(v->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if(imageBitmap==null && !inputNoteTitle.getText().toString().trim().equals("")
                    && !category.getText().toString().trim().equals("") && !city.getText().toString().trim().equals("")
                    && !edtNoteContent.getText().toString().trim().equals("")) {
                String title=inputNoteTitle.getText().toString().trim()+"\n\n"+"Date : "+textDataView.getText().toString().trim()+"\nCategory : "+category.getText().toString().trim()+"\n"+"City : "+city.getText().toString().trim()+
                        "\n\n"+edtNoteContent.getText().toString().trim()+"\n\n\n"+"by - "+name;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + requireContext().getPackageName());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
            else if(!inputNoteTitle.getText().toString().trim().equals("")
                    && !category.getText().toString().trim().equals("") && !city.getText().toString().trim().equals("")
                    && !edtNoteContent.getText().toString().trim().equals("")){
                /*BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                Bitmap bitmap = drawable.getBitmap();*/
                Uri bitmapUri = getLocalBitmapUri();
                String title=inputNoteTitle.getText().toString().trim()+"\n\n"+"Date : "+textDataView.getText().toString().trim()+"\nCategory : "+category.getText().toString().trim()+
                        "\n"+"City : "+city.getText().toString().trim()+"\n\n"+edtNoteContent.getText().toString().trim()+"\n\n\n"+"by - "+name;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                intent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + requireContext().getPackageName());

                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddpdf).setOnClickListener(v->{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("application/pdf");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            startActivityForResult(chooseFile, PDF_SELECTION_CODE);
        });

    }// ends layoutMiscellaneous
/*    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/
    public Uri getLocalBitmapUri() {
        // Extract Bitmap from ImageView drawable
        BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    private void showAddURLDialog(){
        if (dialogAddURL == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View view0 = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_url, view.findViewById(R.id.layoutAddUrlContainer));

            builder.setView(view0);
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view0.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view0.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if (inputURL.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(), "Enter URL", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                    Toast.makeText(getContext(), "Enter valid URL", Toast.LENGTH_SHORT).show();
                }
                else{
                    textWebURL.setText(inputURL.getText().toString().trim());
                    layoutWebURL.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });

            view0.findViewById(R.id.textCancel).setOnClickListener(v -> dialogAddURL.dismiss());
        }

        dialogAddURL.show();


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
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        addImageNote(selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (requestCode == PDF_SELECTION_CODE) {
            if (resultCode == -1) {
                attachpdf.setVisibility(View.VISIBLE);
                remove_pdf.setVisibility(View.VISIBLE);
                assert data != null;
                selected_uri_pdf= data.getData();
                String uriString = selected_uri_pdf.toString();

                Cursor cursor = getContextNullSafety().getContentResolver()
                        .query(selected_uri_pdf, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();

                String name = cursor.getString(nameIndex);
                String size = Long.toString(cursor.getLong(sizeIndex));
                String str_txt=name+" ("+size+" bytes)";
                attachpdf.setText("");
                attachpdf.setText(str_txt);
            }
        }
    }
    private void addImageNote(Uri imageUri) throws IOException {

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath = compressImage(imageUri+"");
            imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
            //imageBitmap = SiliCompressor.with(note_create.this).getCompressBitmap(String.valueOf(imageUri));
            view.findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        category_in=null;
        city_in=null;
        content_in=null;
        date_in=null;
        head_in=null;
        imagelink_in=null;
        pdflink_in=null;
        url_in=null;
        an_push_key_in=null;
        writtenby=null;
        edtNoteContent=null;
        imageNote.setImageBitmap(null);
        inputNoteTitle=null;
        category=null;
        city=null;
        textDataView=null;
        textWebURL=null;
        imageBitmap=null;
        attachpdf=null;
        name=null;
        remove_pdf.setOnClickListener(null);
        profile_image.setImageBitmap(null);
        removeImage.setOnClickListener(null);
        copy_txt.setOnClickListener(null);
        save.setOnClickListener(null);
        voice_text.setOnClickListener(null);
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}