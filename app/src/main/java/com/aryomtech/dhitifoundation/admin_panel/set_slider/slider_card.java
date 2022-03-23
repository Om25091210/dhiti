package com.aryomtech.dhitifoundation.admin_panel.set_slider;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;


public class slider_card extends Fragment {

    View view;
    Uri selectedImageUri;
    Bitmap bitmap;
    String selected_path="",head_text_args,des_text_args,pushkey_args;
    ImageView photo,imageSave;
    Dialog dialog;
    TextView head,des;
    ImageView del;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    public static final int REQUEST_CODE_SELECT_IMAGE = 200;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 110;
    private Context contextNullSafe;
    String image_link_args;
    EditText head_title,des_title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_slider_card, container, false);
        if (contextNullSafe == null) getContextNullSafety();

        reference= FirebaseDatabase.getInstance().getReference().child("slider");
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        try {
            head_text_args = getArguments().getString("head_text_101");
            des_text_args = getArguments().getString("des_text_102");
            image_link_args = getArguments().getString("image_url_103");
            pushkey_args = getArguments().getString("pushkey_10465");
        } catch (Exception e) {
            e.printStackTrace();
        }

        photo=view.findViewById(R.id.img_slider);
        del=view.findViewById(R.id.del);
        imageSave=view.findViewById(R.id.imageSave);
        view.findViewById(R.id.image).setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(getContextNullSafety().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });
        head_title=view.findViewById(R.id.editTextTextMultiLine);
        head=view.findViewById(R.id.textView110);
        head_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                head.setText(editable);
            }
        });
        des_title=view.findViewById(R.id.editTextTextMultiLine1);
        des=view.findViewById(R.id.textView113);
        des_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                des.setText(editable);
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
        if(head_text_args!=null) {
            head.setText(head_text_args);
            head_title.setText(head_text_args);
        }
        if (des_text_args!=null){
            des.setText(des_text_args);
            des_title.setText(des_text_args);
        }
        if(image_link_args!=null){
            try{
                Glide.with(getContextNullSafety()).asBitmap()
                        .load(image_link_args)
                        .placeholder(R.drawable.ic_image_holder)
                        .into(photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        imageSave.setOnClickListener(v-> {
            if(pushkey_args!=null)
                save(pushkey_args);
            else{
                String pushkey=reference.push().getKey();
                save(pushkey);
            }
        });
        if(pushkey_args!=null)
            del.setVisibility(View.VISIBLE);
        else
            del.setVisibility(View.GONE);
        del.setOnClickListener(v->{
            if(pushkey_args!=null)
                delete();
        });
        return view;
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
            reference.child(pushkey_args).removeValue();
            String path = "slider/" + pushkey_args + "_sliderimg" + ".png";
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
        });

    }
    private void save(String pushkey) {
        if(!head_title.getText().toString().equals("")){
            if(!des_title.getText().toString().equals("")){
                if (!selected_path.trim().equals("") || image_link_args != null) {
                    assert pushkey != null;
                    dialog = new Dialog(requireContext());
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.loading_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                    lottieAnimationView.setAnimation("uploaded.json");
                    dialog.show();

                    reference.child(pushkey).child("head_text").setValue(head_title.getText().toString());
                    reference.child(pushkey).child("des_text").setValue(des_title.getText().toString());
                    reference.child(pushkey).child("pushkey").setValue(pushkey);

                    if (image_link_args == null) {
                        String imagepath = "slider/" + pushkey + "_sliderimg" + ".png";

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
                                                        reference.child(pushkey).child("image_url").setValue(image_link);

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
                        reference.child(pushkey).child("image_url").setValue(image_link_args);
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
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Empty",
                        "please enter short description for event.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Empty",
                    "please enter event or cause.",
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
    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
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
            image_link_args=null;
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