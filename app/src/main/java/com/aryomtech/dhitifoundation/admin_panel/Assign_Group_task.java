package com.aryomtech.dhitifoundation.admin_panel;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.group_dp_Adapter;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.taskAdapter;
import com.aryomtech.dhitifoundation.cities.onClickInterface;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import www.sanju.motiontoast.MotionToast;

import static android.app.Activity.RESULT_OK;


public class Assign_Group_task extends Fragment {

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    View view;
    ArrayList<String> mem_list,device_token_list;
    RecyclerView recyclerView,recyclerView2;
    DatabaseReference users_ref,group_ref;
    ArrayList<String> dp_list;
    ArrayList<String> name_list;
    String head_dp,head_name,head="";
    ArrayList<String> task_list;
    TextView deadline,save,head_txt,head_name_txt;
    LinearLayout linearLayout;
    CheckBox checkBox;
    SimpleDraweeView head_pic;
    EditText location;
    int day, month, year;
    private ImageView imageNote;
    ImageView add_img,delete_head,removeImage,imageBack;
    LinearLayout add_task;
    RadioButton radioButton;
    taskAdapter taskAdapter;
    String deleted_task,city;
    EditText edtNoteContent,title;
    String selectedImagePath="",image_link;
    ImageView copy_txt;
    Dialog dialog;
    FirebaseAuth auth;
    FirebaseUser user;
    private onClickInterface onclickInterface;
    LottieAnimationView voice_text;
    ActivityResultLauncher<Intent> startActivityForImage;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Context contextNullSafe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_assign__group_task, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        try {
            mem_list = getArguments().getStringArrayList("uids_array_list");
            city = getArguments().getString("location");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dp_list=new ArrayList<>();
        name_list=new ArrayList<>();
        task_list=new ArrayList<>();
        device_token_list=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        taskAdapter=new taskAdapter(getContext(),task_list);
        imageBack=view.findViewById(R.id.imageBack);
        voice_text = view.findViewById(R.id.voice_text);
        imageNote = view.findViewById(R.id.imageNote);
        recyclerView=view.findViewById(R.id.recyclerView2);
        recyclerView2=view.findViewById(R.id.task_rv);
        edtNoteContent= view.findViewById(R.id.inputNote);
        add_img = view.findViewById(R.id.imageView9);
        copy_txt = view.findViewById(R.id.imageView7);
        deadline=view.findViewById(R.id.date_start);
        radioButton=view.findViewById(R.id.radioButton);
        save=view.findViewById(R.id.textView60);
        title=view.findViewById(R.id.editTextTextPersonName);
        removeImage=view.findViewById(R.id.imageRemoveImage);
        location=view.findViewById(R.id.editTextTextMultiLine15);
        add_task=view.findViewById(R.id.add_task);
        checkBox=view.findViewById(R.id.checkBox);
        head_txt=view.findViewById(R.id.head_txt);
        head_pic=view.findViewById(R.id.circleImageView);
        head_name_txt=view.findViewById(R.id.textView82);
        linearLayout=view.findViewById(R.id.linearLayout9);
        delete_head=view.findViewById(R.id.imageView16);
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        group_ref= FirebaseDatabase.getInstance().getReference().child("group_task");

        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
        head_txt.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        delete_head.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);


        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        recyclerView2.setVisibility(View.GONE);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        recyclerView2.setItemViewCacheSize(20);
        recyclerView2.setDrawingCacheEnabled(true);
        recyclerView2.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView2.setLayoutManager(layoutManager2);

        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int device_token=0;device_token<mem_list.size();device_token++) {
                    if (mem_list.get(device_token) != null) {
                        if (snapshot.child(mem_list.get(device_token)).child("token").exists())
                            device_token_list.add(snapshot.child(mem_list.get(device_token)).child("token").getValue(String.class));
                        else
                            device_token_list.add("");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        radioButton.setOnClickListener(v -> {
            if(radioButton.isChecked()){
                String text="Tap to select";
                deadline.setText(text);
            }
        });

        deadline.setOnClickListener(v->{
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), mDateSetListener,year, month,day);
            datePickerDialog.show();
        });

        mDateSetListener = (datePicker, year, month, day) -> {

            String d=String.valueOf(day);
            String m=String.valueOf(month);

            month = month + 1;
            if(String.valueOf(day).length()==1)
                d="0"+ day;
            if(String.valueOf(month).length()==1)
                m="0"+ month;
            String date = d + "-" + m + "-" + year;
            deadline.setText(date);
            radioButton.setChecked(false);
        };
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

        startActivityForImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        Uri selectedImageUri = result.getData().getData();
                        addImageNote(selectedImageUri);
                    }
                }
        );
        copy_txt.setOnClickListener(v->{
            ClipboardManager clipboardManager= (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData=ClipData.newPlainText("content",edtNoteContent.getText().toString().trim());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        imageBack.setOnClickListener(v -> onBackPressed());
        add_task.setOnClickListener(v-> addTask());
        removeImage.setOnClickListener(v -> removeImage());
        add_img.setOnClickListener(v-> selectImage());
        save.setOnClickListener(v->submit());
        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position=viewHolder.getAdapterPosition();

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        deleted_task=task_list.get(position);
                        task_list.remove(position);
                        taskAdapter.notifyItemRemoved(position);
                        Snackbar.make(recyclerView2,deleted_task+" deleted.",Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    task_list.add(position,deleted_task);
                                    taskAdapter.notifyItemInserted(position);
                                })
                                .setActionTextColor(Color.parseColor("#ea4a1f"))
                                .setTextColor(Color.parseColor("#000000"))
                                .setBackgroundTint(Color.parseColor("#D9F5F8"))
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        Dialog dialog = new Dialog(getContext());
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.get_task_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView cancel=dialog.findViewById(R.id.textCancel);
                        TextView add=dialog.findViewById(R.id.textAdd);
                        EditText task_content=dialog.findViewById(R.id.inputURL);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show();

                        task_content.setText(task_list.get(position));
                        cancel.setOnClickListener(v->{
                            task_list.remove(position);
                            task_list.add(position,task_content.getText().toString().trim());
                            taskAdapter.notifyItemChanged(position);
                            dialog.dismiss();
                        });
                        add.setOnClickListener(v->{
                            if(!task_content.getText().toString().trim().equals("")) {
                                task_list.remove(position);
                                task_list.add(position,task_content.getText().toString().trim());
                                taskAdapter.notifyItemChanged(position);
                                dialog.dismiss();
                            }
                            else
                                MotionToast.Companion.darkColorToast(getActivity(),
                                        "Info",
                                        "task cannot be empty.",
                                        MotionToast.TOAST_WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                        });
                        break;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView2);

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

        onclickInterface = position -> {
                head=mem_list.get(position);
                head_dp=dp_list.get(position);
                head_name=name_list.get(position);
                delete_head.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                head_txt.setVisibility(View.VISIBLE);
                head_name_txt.setText(head_name);

                Uri uri = Uri.parse(dp_list.get(position));
                ImageRequest request = ImageRequest.fromUri(uri);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(head_pic.getController()).build();

                head_pic.setController(controller);
           /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try{
                        Glide.with(this).asBitmap()
                                .load(dp_list.get(position))
                                .thumbnail(0.1f)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .override(80,80)
                                .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                .into(head_image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
            else{
                    try{
                        Glide.with(this).asBitmap()
                                .load(dp_list.get(position))
                                .override(40,40)
                                .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                .into(head_image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
        };
        delete_head.setOnClickListener(v->hide_layout());
        new Handler(Looper.myLooper()).postDelayed(this::get_pictures,1500);

        return view;
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
            Save();
            dialog.dismiss();
        });
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

    private void hide_layout() {
        head_txt.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        delete_head.setVisibility(View.GONE);
        head_dp="";
        head_name="";
        head="";
    }

    private void Save() {
        String pushkey=group_ref.push().getKey();
        if(!title.getText().toString().trim().equals("")){
            if(!deadline.getText().toString().trim().equals("Tap to select") || radioButton.isChecked()){

                dialog = new Dialog(requireContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("uploaded.json");
                for (int token=0;token<device_token_list.size();token++){
                    Specific specific=new Specific();
                    specific.noti("New group task",title.getText().toString().trim(),device_token_list.get(token));
                }

                if (!selectedImagePath.equals("")){
                    dialog.show();
                    assert pushkey != null;
                    group_ref.child(pushkey).child("task_title").setValue(title.getText().toString().trim());
                    if(radioButton.isChecked())
                        group_ref.child(pushkey).child("task_deadline").setValue(radioButton.getText().toString());
                    else
                        group_ref.child(pushkey).child("task_deadline").setValue(deadline.getText().toString());
                    if(task_list.size()!=0)
                        group_ref.child(pushkey).child("added_task").setValue(task_list);
                    if(checkBox.isChecked())
                        group_ref.child(pushkey).child("state").setValue("private");
                    else
                        group_ref.child(pushkey).child("state").setValue("public");
                    if(!head.equals(""))
                        group_ref.child(pushkey).child("head").setValue(head);
                    group_ref.child(pushkey).child("members").setValue(mem_list);
                    group_ref.child(pushkey).child("description").setValue(edtNoteContent.getText().toString().trim());
                    group_ref.child(pushkey).child("key").setValue(pushkey);
                    group_ref.child(pushkey).child("status").setValue("ongoing");
                    group_ref.child(pushkey).child("city").setValue(city);
                    group_ref.child(pushkey).child("creator").setValue(user.getUid());
                    group_ref.child(pushkey).child("location").setValue(location.getText().toString().trim());
                    group_ref.child(pushkey).child("submitted").setValue("no");
                    group_ref.child(pushkey).child("given_on").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    String imagepath = "GroupTask/" + pushkey + ".png";
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                    /*BitmapDrawable drawable = (BitmapDrawable) imageNote.getDrawable();
                    Bitmap bitmap_up = drawable.getBitmap();
                    final String randomKey = UUID.randomUUID().toString();
                    String path = MediaStore.Images.Media.insertImage(requireContext().getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

                    try {
                        InputStream stream = new FileInputStream(new File(selectedImagePath));

                        storageReference.putStream(stream)
                                .addOnSuccessListener(taskSnapshot ->
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                task -> {
                                                    image_link = Objects.requireNonNull(task.getResult()).toString();
                                                    group_ref.child(pushkey).child("image_link").setValue(image_link);
                                                    dialog.dismiss();
                                                    Toast.makeText(getContextNullSafety(), "Task assigned successfully", Toast.LENGTH_SHORT).show();
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
                }
                else{
                    assert pushkey != null;
                    group_ref.child(pushkey).child("task_title").setValue(title.getText().toString().trim());
                    if(radioButton.isChecked())
                        group_ref.child(pushkey).child("task_deadline").setValue(radioButton.getText().toString());
                    else
                        group_ref.child(pushkey).child("task_deadline").setValue(deadline.getText().toString());
                    if(task_list.size()!=0)
                        group_ref.child(pushkey).child("added_task").setValue(task_list);
                    if(checkBox.isChecked())
                        group_ref.child(pushkey).child("state").setValue("private");
                    else
                        group_ref.child(pushkey).child("state").setValue("public");
                    if(!head.equals(""))
                        group_ref.child(pushkey).child("head").setValue(head);
                    group_ref.child(pushkey).child("members").setValue(mem_list);
                    group_ref.child(pushkey).child("description").setValue(edtNoteContent.getText().toString().trim());
                    group_ref.child(pushkey).child("key").setValue(pushkey);
                    group_ref.child(pushkey).child("city").setValue(city);
                    group_ref.child(pushkey).child("creator").setValue(user.getUid());
                    group_ref.child(pushkey).child("status").setValue("ongoing");
                    group_ref.child(pushkey).child("location").setValue(location.getText().toString().trim());
                    group_ref.child(pushkey).child("submitted").setValue("no");
                    group_ref.child(pushkey).child("given_on").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

                    Toast.makeText(getContextNullSafety(), "Task assigned successfully", Toast.LENGTH_SHORT).show();
                    FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    if(fm.getBackStackEntryCount()>0) {
                        fm.popBackStack();
                    }
                    ft.commit();
                }


            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Empty",
                        "Set deadline.",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Error",
                    "Title is empty.",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
        }
    }
    void removeImage(){
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(View.GONE);
        view.findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        selectedImagePath = "";
    }
    private void onBackPressed() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
    private void addTask() {

        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.get_task_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.textCancel);
        TextView add=dialog.findViewById(R.id.textAdd);
        EditText task_content=dialog.findViewById(R.id.inputURL);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        cancel.setOnClickListener(v->dialog.dismiss());
        add.setOnClickListener(v->{
            if(!task_content.getText().toString().trim().equals("")) {
                recyclerView2.setVisibility(View.VISIBLE);
                task_list.add(task_content.getText().toString().trim());
                Collections.reverse(task_list);
                taskAdapter=new taskAdapter(getContext(),task_list);
                taskAdapter.notifyDataSetChanged();
                try {
                    recyclerView2.setAdapter(taskAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
            else
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Info",
                        "task cannot be empty.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
        });

    }

    private void get_pictures() {
        for(int i=0;i<mem_list.size();i++) {
            if (mem_list.get(i) != null) {
                users_ref.child(mem_list.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String dp = snapshot.child("dplink").getValue(String.class);
                        String name = snapshot.child("name").getValue(String.class);
                        name_list.add(name);
                        if (dp != null)
                            dp_list.add(dp);
                        else
                            dp_list.add("");
                        group_dp_Adapter group_dp_adapter = new group_dp_Adapter(dp_list, name_list, getContext(), onclickInterface);
                        try {
                            recyclerView.setAdapter(group_dp_adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        group_dp_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
            }
        }
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
    private void selectImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForImage.launch(intent);
    }
    private void addImageNote(Uri imageUri) {

        imageNote.setVisibility(View.VISIBLE);
        selectedImagePath = compressImage(imageUri+"");
        imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        view.findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

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
        File file = new File(requireContext().getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = requireContext().getContentResolver().query(contentUri, null, null, null, null);
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