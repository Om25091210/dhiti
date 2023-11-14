package com.aryomtech.dhitifoundation.admin_panel;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import androidx.exifinterface.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.taskAdapter;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.aryomtech.dhitifoundation.public_notes.announcement_list;
import com.bumptech.glide.Glide;
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


public class Assign_task extends Fragment {

    DatabaseReference users_ref;
    View view;
    LinearLayout deadline;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    TextView date_start,name,save;
    String selectedImagePath="",image_link,uid,device_token="";
    ImageView add_img,removeImage,imageBack;
    EditText edtNoteContent,Title;
    private ImageView imageNote;
    FirebaseAuth auth;
    FirebaseUser user;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    LottieAnimationView voice_text;
    ImageView copy_txt;
    CircleImageView profile_image;
    RadioButton radioButton;
    int day, month, year;
    //https://dribbble.com/shots/7933705-Project-Management-Mobile-App
    LinearLayout add_task;
    ArrayList<String> task_list;
    RecyclerView recyclerView;
    ActivityResultLauncher<Intent> startActivityForImage;
    taskAdapter taskAdapter;
    String deleted_task;Dialog dialog;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_assign_task, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        deadline=view.findViewById(R.id.deadline);
        date_start=view.findViewById(R.id.date_start);
        voice_text = view.findViewById(R.id.voice_text);
        edtNoteContent= view.findViewById(R.id.inputNote);
        copy_txt = view.findViewById(R.id.imageView7);
        add_img = view.findViewById(R.id.imageView9);
        imageNote = view.findViewById(R.id.imageNote);
        removeImage=view.findViewById(R.id.imageRemoveImage);
        profile_image=view.findViewById(R.id.profile_image);
        name=view.findViewById(R.id.name);
        imageBack=view.findViewById(R.id.imageBack);
        add_task=view.findViewById(R.id.add_task);
        recyclerView=view.findViewById(R.id.task_rv);
        radioButton=view.findViewById(R.id.radioButton);
        save=view.findViewById(R.id.textView60);
        Title=view.findViewById(R.id.editTextTextMultiLine14);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        assert getArguments() != null;
        uid=getArguments().getString("uid unique key");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String dplink=snapshot.child(uid).child("dplink").getValue(String.class);
                String name_data=snapshot.child(uid).child("name").getValue(String.class);
                name.setText(name_data);
                try{
                    Glide.with(getContextNullSafety()).asBitmap()
                            .load(dplink)
                            .thumbnail(0.1f)
                            .override(80,80)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(profile_image);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).child("token").exists()){
                    device_token= snapshot.child(uid).child("token").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        radioButton.setOnClickListener(v -> {
            if(radioButton.isChecked()){
                String text="Tap to select";
               date_start.setText(text);
            }
        });
        task_list=new ArrayList<>();
        taskAdapter=new taskAdapter(getContext(),task_list);
        add_task.setOnClickListener(v-> addTask());
        imageBack.setOnClickListener(v -> onBackPressed());
        removeImage.setOnClickListener(v -> removeImage());


        recyclerView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);


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
                        Snackbar.make(recyclerView,deleted_task+" deleted.",Snackbar.LENGTH_LONG)
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
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        }
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
            date_start.setText(date);
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
        copy_txt.setOnClickListener(v->{
            ClipboardManager clipboardManager= (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData=ClipData.newPlainText("content",edtNoteContent.getText().toString().trim());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext(), "copied to clipboard", Toast.LENGTH_SHORT).show();
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
        add_img.setOnClickListener(v-> selectImage());
        save.setOnClickListener(v->submit());

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

        return  view;
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
    private void Save() {
        String pushkey=users_ref.push().getKey();
        if(!Title.getText().toString().trim().equals("")){
            if(!date_start.getText().toString().trim().equals("Tap to select") || radioButton.isChecked()){

                dialog = new Dialog(requireContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                lottieAnimationView.setAnimation("uploaded.json");

                Specific specific=new Specific();
                specific.noti("New task for you",Title.getText().toString().trim(),device_token);

                if (!selectedImagePath.equals("")){
                    dialog.show();
                    assert pushkey != null;
                    users_ref.child(uid).child("task").child(pushkey).child("task_title").setValue(Title.getText().toString().trim());
                    if(radioButton.isChecked())
                        users_ref.child(uid).child("task").child(pushkey).child("task_deadline").setValue(radioButton.getText().toString());
                    else
                        users_ref.child(uid).child("task").child(pushkey).child("task_deadline").setValue(date_start.getText().toString());
                    if(task_list.size()!=0)
                        users_ref.child(uid).child("task").child(pushkey).child("added_task").setValue(task_list);
                    users_ref.child(uid).child("task").child(pushkey).child("description").setValue(edtNoteContent.getText().toString().trim());
                    users_ref.child(uid).child("task").child(pushkey).child("key").setValue(pushkey);
                    users_ref.child(uid).child("task").child(pushkey).child("creator").setValue(user.getUid());
                    users_ref.child(uid).child("task").child(pushkey).child("status").setValue("ongoing");
                    users_ref.child(uid).child("task").child(pushkey).child("submitted").child("no");
                    users_ref.child(uid).child("task").child(pushkey).child("given_on").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    String imagepath = "PrivateTask/" + uid + pushkey + ".png";
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
                                                    users_ref.child(uid).child("task").child(pushkey).child("image_link").setValue(image_link);
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
                    users_ref.child(uid).child("task").child(pushkey).child("task_title").setValue(Title.getText().toString().trim());
                    if(radioButton.isChecked())
                        users_ref.child(uid).child("task").child(pushkey).child("task_deadline").setValue(radioButton.getText().toString());
                    else
                        users_ref.child(uid).child("task").child(pushkey).child("task_deadline").setValue(date_start.getText().toString());
                    if(task_list.size()!=0)
                        users_ref.child(uid).child("task").child(pushkey).child("added_task").setValue(task_list);
                    users_ref.child(uid).child("task").child(pushkey).child("description").setValue(edtNoteContent.getText().toString().trim());
                    users_ref.child(uid).child("task").child(pushkey).child("key").setValue(pushkey);
                    users_ref.child(uid).child("task").child(pushkey).child("creator").setValue(user.getUid());
                    users_ref.child(uid).child("task").child(pushkey).child("status").setValue("ongoing");
                    users_ref.child(uid).child("task").child(pushkey).child("submitted").child("no");
                    users_ref.child(uid).child("task").child(pushkey).child("given_on").setValue(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

                    Toast.makeText(getContextNullSafety(), "Task assigned successfully", Toast.LENGTH_SHORT).show();
                    FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                    FragmentTransaction ft=fm.beginTransaction();
                    if(fm.getBackStackEntryCount()>0) {
                        fm.popBackStack();
                    }
                    ft.commit();
                }


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
                recyclerView.setVisibility(View.VISIBLE);
                task_list.add(task_content.getText().toString().trim());
                Collections.reverse(task_list);
                taskAdapter=new taskAdapter(getContext(),task_list);
                taskAdapter.notifyDataSetChanged();
                try {
                    recyclerView.setAdapter(taskAdapter);
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
    private void onBackPressed() {
            FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            if(fm.getBackStackEntryCount()>0) {
                fm.popBackStack();
            }
            ft.commit();
    }
    void removeImage(){
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(View.GONE);
        view.findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        selectedImagePath = "";
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