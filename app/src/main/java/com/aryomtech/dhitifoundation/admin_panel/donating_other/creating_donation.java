package com.aryomtech.dhitifoundation.admin_panel.donating_other;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;


public class creating_donation extends Fragment {

    View view;
    public static final int REQUEST_CODE_SELECT_IMAGE = 200;
    private Context contextNullSafe;
    Uri selectedImageUri;
    Bitmap bitmap;
    String identity_admin="";
    RoundedImageView photo;
    String category;
    EditText address_edit,contact_edit;
    LottieAnimationView voice_text;
    String selected_path="",key_for_donation;
    Dialog dialog;
    Button button3;
    TextView thought_text;
    String image_url_args,category_args,address_args,contact_args,what_is_args,pick_up_args,key_args,uid_args;
    DatabaseReference donation_ref,fluid_ref;
    FirebaseAuth auth;
    FirebaseUser user;
    EditText inputNote;
    TextView food,clothes,education,other;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 110;
    ConstraintLayout donate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_creating_donation, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        view.findViewById(R.id.imageNote).setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(getContextNullSafety().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });
        identity_admin=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        key_for_donation=getArguments().getString("keys_for_creating_donation");

        donation_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards").child(key_for_donation).child("cause_donations");
        fluid_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards").child(key_for_donation);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        photo=view.findViewById(R.id.imageNote);
        food=view.findViewById(R.id.food);
        clothes=view.findViewById(R.id.cloth);
        education=view.findViewById(R.id.education);
        other=view.findViewById(R.id.Other);
        inputNote=view.findViewById(R.id.inputNote);
        donate=view.findViewById(R.id.donate);
        address_edit=view.findViewById(R.id.editTextTextMultiLine13);
        contact_edit=view.findViewById(R.id.edit_);
        button3=view.findViewById(R.id.button3);
        thought_text=view.findViewById(R.id.textView126);

        image_url_args=getArguments().getString("image_url_key_donation");
        category_args=getArguments().getString("category_key_donation");
        address_args=getArguments().getString("address_key_donation");
        contact_args=getArguments().getString("contact_key_donation");
        what_is_args=getArguments().getString("what_is_the_donation");
        pick_up_args=getArguments().getString("pick_up_donation");
        key_args=getArguments().getString("key_of_donation");
        uid_args=getArguments().getString("uid_of_donation");

        donate.setOnClickListener(v-> {
            if(key_args!=null)
                save_data(key_args);
            else
                save_data(donation_ref.push().getKey());
        });
        button3.setOnClickListener(v->{
            if(key_args!=null)
                save_data(key_args);
            else
                save_data(donation_ref.push().getKey());
        });

        if(image_url_args !=null){
            try{
                Glide.with(getContextNullSafety()).asBitmap()
                        .load(image_url_args)
                        .placeholder(R.drawable.ic_image_holder)
                        .into(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(category_args!=null) {
            switch (category_args) {
                case "Food":
                    category = "Food";

                    food.setTextColor(Color.parseColor("#FF2371FA"));
                    clothes.setTextColor(Color.parseColor("#3F4056"));
                    education.setTextColor(Color.parseColor("#3F4056"));
                    other.setTextColor(Color.parseColor("#3F4056"));

                    food.setBackgroundResource(R.drawable.bg_of_add_donations);
                    clothes.setBackgroundResource(R.drawable.border_amount_bg);
                    education.setBackgroundResource(R.drawable.border_amount_bg);
                    other.setBackgroundResource(R.drawable.border_amount_bg);
                    break;
                case "Clothes":
                    category = "Clothes";

                    clothes.setTextColor(Color.parseColor("#FF2371FA"));
                    food.setTextColor(Color.parseColor("#3F4056"));
                    education.setTextColor(Color.parseColor("#3F4056"));
                    other.setTextColor(Color.parseColor("#3F4056"));

                    clothes.setBackgroundResource(R.drawable.bg_of_add_donations);
                    food.setBackgroundResource(R.drawable.border_amount_bg);
                    education.setBackgroundResource(R.drawable.border_amount_bg);
                    other.setBackgroundResource(R.drawable.border_amount_bg);
                    break;
                case "Education":
                    category = "Education";

                    education.setTextColor(Color.parseColor("#FF2371FA"));
                    clothes.setTextColor(Color.parseColor("#3F4056"));
                    food.setTextColor(Color.parseColor("#3F4056"));
                    other.setTextColor(Color.parseColor("#3F4056"));

                    education.setBackgroundResource(R.drawable.bg_of_add_donations);
                    food.setBackgroundResource(R.drawable.border_amount_bg);
                    clothes.setBackgroundResource(R.drawable.border_amount_bg);
                    other.setBackgroundResource(R.drawable.border_amount_bg);
                    break;
                case "Other":
                    category = "Other";

                    other.setTextColor(Color.parseColor("#FF2371FA"));
                    clothes.setTextColor(Color.parseColor("#3F4056"));
                    education.setTextColor(Color.parseColor("#3F4056"));
                    food.setTextColor(Color.parseColor("#3F4056"));

                    other.setBackgroundResource(R.drawable.bg_of_add_donations);
                    food.setBackgroundResource(R.drawable.border_amount_bg);
                    clothes.setBackgroundResource(R.drawable.border_amount_bg);
                    education.setBackgroundResource(R.drawable.border_amount_bg);
                    break;
            }
        }
        if(address_args!=null)
            address_edit.setText(address_args);
        if(contact_args!=null)
            contact_edit.setText(contact_args);
        if(what_is_args!=null)
            inputNote.setText(what_is_args);
        if(pick_up_args!=null) {
            String str="Our associate will reach out to you for this donation.";
            thought_text.setText(str);
            thought_text.setTextColor(Color.parseColor("#66BB6A"));
        }

        food.setOnClickListener(v->{
            category="Food";

            food.setTextColor(Color.parseColor("#FF2371FA"));
            clothes.setTextColor(Color.parseColor("#3F4056"));
            education.setTextColor(Color.parseColor("#3F4056"));
            other.setTextColor(Color.parseColor("#3F4056"));

            food.setBackgroundResource(R.drawable.bg_of_add_donations);
            clothes.setBackgroundResource(R.drawable.border_amount_bg);
            education.setBackgroundResource(R.drawable.border_amount_bg);
            other.setBackgroundResource(R.drawable.border_amount_bg);
        });

        clothes.setOnClickListener(v->{
            category="Clothes";

            clothes.setTextColor(Color.parseColor("#FF2371FA"));
            food.setTextColor(Color.parseColor("#3F4056"));
            education.setTextColor(Color.parseColor("#3F4056"));
            other.setTextColor(Color.parseColor("#3F4056"));

            clothes.setBackgroundResource(R.drawable.bg_of_add_donations);
            food.setBackgroundResource(R.drawable.border_amount_bg);
            education.setBackgroundResource(R.drawable.border_amount_bg);
            other.setBackgroundResource(R.drawable.border_amount_bg);
        });
        education.setOnClickListener(v->{
            category="Education";

            education.setTextColor(Color.parseColor("#FF2371FA"));
            clothes.setTextColor(Color.parseColor("#3F4056"));
            food.setTextColor(Color.parseColor("#3F4056"));
            other.setTextColor(Color.parseColor("#3F4056"));

            education.setBackgroundResource(R.drawable.bg_of_add_donations);
            food.setBackgroundResource(R.drawable.border_amount_bg);
            clothes.setBackgroundResource(R.drawable.border_amount_bg);
            other.setBackgroundResource(R.drawable.border_amount_bg);
        });
        other.setOnClickListener(v->{
            category="Other";

            other.setTextColor(Color.parseColor("#FF2371FA"));
            clothes.setTextColor(Color.parseColor("#3F4056"));
            education.setTextColor(Color.parseColor("#3F4056"));
            food.setTextColor(Color.parseColor("#3F4056"));

            other.setBackgroundResource(R.drawable.bg_of_add_donations);
            food.setBackgroundResource(R.drawable.border_amount_bg);
            clothes.setBackgroundResource(R.drawable.border_amount_bg);
            education.setBackgroundResource(R.drawable.border_amount_bg);
        });
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        ArrayList<String> result_voice = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String voice_text_Str=inputNote.getText().toString().trim()+" "+result_voice.get(0)+"";
                        inputNote.setText(voice_text_Str);
                    }
                }
        );
        voice_text = view.findViewById(R.id.voice_text);
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
        return view;
    }

    private void save_data(String key) {
        if(category!=null){
            if(!address_edit.getText().toString().equals("")){
                if(!contact_edit.getText().toString().equals("")){
                    if(!inputNote.getText().toString().equals("")){
                        if (!selected_path.trim().equals("") || image_url_args != null) {

                            donation_ref.child(key).child("category").setValue(category);
                            donation_ref.child(key).child("address").setValue(address_edit.getText().toString());
                            donation_ref.child(key).child("contact").setValue(contact_edit.getText().toString());
                            donation_ref.child(key).child("description").setValue(inputNote.getText().toString());
                            donation_ref.child(key).child("uid").setValue(user.getUid());
                            donation_ref.child(key).child("key").setValue(key);
                            dialog = new Dialog(requireContext());
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                            lottieAnimationView.setAnimation("uploaded.json");
                            dialog.show();

                            if (image_url_args==null) {
                                String imagepath = "donations/" + key + "_donation_img" + ".png";

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
                                                                donation_ref.child(key).child("image_url").setValue(image_link);

                                                                dialog.dismiss();
                                                                Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
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
                            } else {
                                donation_ref.child(key).child("image_url").setValue(image_url_args);
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
                        else{
                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Attach Image",
                                    "Please attach image for your donation.",
                                    MotionToast.TOAST_WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                        }
                    }
                    else{
                        MotionToast.Companion.darkColorToast(getActivity(),
                                "Empty",
                                "please tell us what are you donating?.",
                                MotionToast.TOAST_WARNING,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                    }
                }
                else{
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Empty",
                            "please add your number.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                }
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Empty",
                        "please add your address.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Empty",
                    "please add a category.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        }
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
            image_url_args=null;
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