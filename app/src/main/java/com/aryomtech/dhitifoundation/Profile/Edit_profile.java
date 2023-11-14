package com.aryomtech.dhitifoundation.Profile;

import android.Manifest;
import android.app.Activity;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.exifinterface.media.ExifInterface;

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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import www.sanju.motiontoast.MotionToast;

public class Edit_profile extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 110;
    public static final int REQUEST_CODE_SELECT_IMAGE = 220;
    CircleImageView profile_image;
    Bitmap bitmap;
    EditText name_ed,prt_in_dhiti_ed,phone_ed
            ,address_ed,email_ed,aadhar_no,facebook_ed,twitter_ed,linkedin_ed,whatsapp_ed,
            insta_ed, add_city_other;
    String name_str,part_str,email_str,phone_str,aadhar_str,city_str,address_str
            ,facebook_str,twitter_str,linkedin_str,whatsapp_str,instagram_str,imagelink_str
            ,city_ed;
    LottieAnimationView upload;
    Dialog dialog;
    boolean once=true;
    FirebaseAuth auth;
    FirebaseUser user;
    String selected_path="";
    List<String> phone_number=new ArrayList<>();
    ArrayList<String> cityNames;
    ArrayList<Integer> imagesint;
    DatabaseReference user_reference,announcement_ref,private_task_ref;
    Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Window window = Edit_profile.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Edit_profile.this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(Edit_profile.this, R.color.red_200));

        cityNames=new ArrayList<>();
        cityNames.add("Bilaspur");
        cityNames.add("Bhilai");
        cityNames.add("Korba");
        cityNames.add("Janjgir-Champa");
        cityNames.add("other");

        imagesint = new ArrayList<>();
        imagesint.add(R.drawable.ic_bilaspur);
        imagesint.add(R.drawable.ic_bhilai);
        imagesint.add(R.drawable.ic_korba);
        imagesint.add(R.drawable.ic_champa);
        imagesint.add(R.drawable.ic_other_city);
        //Hide the keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        upload=findViewById(R.id.upload);
        user_reference=FirebaseDatabase.getInstance().getReference().child("users");
        private_task_ref= FirebaseDatabase.getInstance().getReference().child("private_approval");
        announcement_ref=FirebaseDatabase.getInstance().getReference().child("Announcement");
        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());

        profile_image=findViewById(R.id.profile_image);
        name_ed=findViewById(R.id.editTextTextMultiLine);
        prt_in_dhiti_ed=findViewById(R.id.editTextTextMultiLine1);
        phone_ed=findViewById(R.id.editTextTextMultiLine3);
        address_ed=findViewById(R.id.editTextTextMultiLine4);
        email_ed=findViewById(R.id.editTextTextMultiLine5);
        aadhar_no=findViewById(R.id.editTextTextMultiLine6);
        facebook_ed=findViewById(R.id.editTextTextMultiLine7);
        twitter_ed=findViewById(R.id.editTextTextMultiLine8);
        linkedin_ed=findViewById(R.id.editTextTextMultiLine9);
        whatsapp_ed=findViewById(R.id.editTextTextMultiLine10);
        insta_ed=findViewById(R.id.editTextTextMultiLine11);
        add_city_other=findViewById(R.id.editTextTextMultiLine2);


        name_str=getIntent().getStringExtra("name_passing_301");
        part_str=getIntent().getStringExtra("part_passing_302");
        email_str=getIntent().getStringExtra("email_passing_303");
        phone_str=getIntent().getStringExtra("phone_passing_304");
        aadhar_str=getIntent().getStringExtra("aadhar_passing_305");
        city_str=getIntent().getStringExtra("city_passing_306");
        address_str=getIntent().getStringExtra("address_passing_307");
        imagelink_str=getIntent().getStringExtra("imagelink_passing_308");
        facebook_str=getIntent().getStringExtra("facebook_passing_309");
        twitter_str=getIntent().getStringExtra("twitter_passing_310");
        linkedin_str=getIntent().getStringExtra("linkedin_passing_311");
        whatsapp_str=getIntent().getStringExtra("whatsapp_passing_312");
        instagram_str=getIntent().getStringExtra("instagram_passing_313");

        spin= findViewById(R.id.simpleSpinner);
        add_city_other.setVisibility(View.GONE);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city_ed=cityNames.get(position);
                if(city_ed.equalsIgnoreCase("other"))
                    add_city_other.setVisibility(View.VISIBLE);
                else
                    add_city_other.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),imagesint,cityNames);
        spin.setAdapter(customAdapter);

        name_ed.setText(name_str);
        if(email_str!=null)
            email_ed.setText(email_str);
        if (phone_str!=null)
            phone_ed.setText(phone_str);
        if(part_str!=null)
            prt_in_dhiti_ed.setText(part_str);
        if(aadhar_str!=null)
            aadhar_no.setText(aadhar_str);
        if (city_str!=null) {
            if (!cityNames.contains(city_str)) {
                cityNames.add(0, city_str);
                imagesint.add(0,R.drawable.ic_city_home);
                spin.setSelection(0);
                city_ed = cityNames.get(0);
            }
            else{
                spin.setSelection(cityNames.indexOf(city_str));
            }
        }
        if(address_str!=null)
            address_ed.setText(address_str);
        if (facebook_str!=null)
            facebook_ed.setText(facebook_str);
        if(twitter_str!=null)
            twitter_ed.setText(twitter_str);
        if(linkedin_str!=null)
            linkedin_ed.setText(linkedin_str);
        if (whatsapp_str!=null)
            whatsapp_ed.setText(whatsapp_str);
        if (insta_ed!=null)
            insta_ed.setText(instagram_str);
        if (imagelink_str!=null)
            try {
                Glide.with(this).asBitmap().load(imagelink_str)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t).into(profile_image);
            } catch (Exception e) {
                e.printStackTrace();
            }

        upload.setOnClickListener(v->{
            upload();
        });
        findViewById(R.id.neumorphCardView2).setOnClickListener(v->{
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST_CODE_STORAGE_PERMISSION );
            } else {
                //do here
                selectImage();
            }
        });
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void upload() {
         if(!facebook_ed.getText().toString().trim().equals("")){
                 if(!Patterns.WEB_URL.matcher(facebook_ed.getText().toString()).matches()){
                     facebook_ed.setError("Invalid Link");
                     MotionToast.Companion.darkColorToast(Edit_profile.this,
                             "Invalid Link",
                             "Please enter a valid link.",
                             MotionToast.TOAST_WARNING,
                             MotionToast.GRAVITY_BOTTOM,
                             MotionToast.LONG_DURATION,
                             ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
                     facebook_ed.setText("");
                 }
             }
             if(!twitter_ed.getText().toString().trim().equals("")){
                 if(!Patterns.WEB_URL.matcher(twitter_ed.getText().toString()).matches()){
                     twitter_ed.setError("Invalid Link");
                     MotionToast.Companion.darkColorToast(Edit_profile.this,
                             "Invalid Link",
                             "Please enter a valid link.",
                             MotionToast.TOAST_WARNING,
                             MotionToast.GRAVITY_BOTTOM,
                             MotionToast.LONG_DURATION,
                             ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
                     twitter_ed.setText("");
                 }
             }
            if(!linkedin_ed.getText().toString().trim().equals("")){
                if(!Patterns.WEB_URL.matcher(linkedin_ed.getText().toString()).matches()){
                    linkedin_ed.setError("Invalid Link");
                    MotionToast.Companion.darkColorToast(Edit_profile.this,
                            "Invalid Link",
                            "Please enter a valid link.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
                    linkedin_ed.setText("");
                }
            }
        if(!name_ed.getText().toString().trim().equals("")){
            phone_number.clear();
            for (int j=0;j<phone_ed.getText().toString().trim().length();j++){
                phone_number.add(String.valueOf(phone_ed.getText().toString().trim().charAt(j)));
            }
            if(!phone_ed.getText().toString().trim().equals("")
                    && (phone_ed.getText().toString().trim().length()==10 || phone_ed.getText().toString().trim().length()==12 || phone_ed.getText().toString().trim().length()==13)
                    && Collections.frequency(phone_number,phone_number.get(0))!=phone_number.size()){
                if(!email_ed.getText().toString().trim().equals("")){
                    if((!city_ed.equals("other") && !city_ed.equals("")) || !add_city_other.getText().toString().trim().equals("")){

                        if(!add_city_other.getText().toString().trim().equals("") && city_ed.equals("other"))
                            city_ed=add_city_other.getText().toString().trim();
                        dialog = new Dialog(Edit_profile.this);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.loading_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                        lottieAnimationView.setAnimation("uploading.json");
                        dialog.show();
                        if(bitmap!=null){
                            String  imagepath = "profile/" + user.getUid() + ".png";
                            once=true;
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                            /*final String randomKey = UUID.randomUUID().toString();
                            BitmapDrawable drawable = (BitmapDrawable) profile_image.getDrawable();
                            Bitmap bitmap_up = drawable.getBitmap();
                            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap_up, "" + randomKey, null);*/

                            try {
                                InputStream stream = new FileInputStream(new File(selected_path));

                                storageReference.putStream(stream)
                                        .addOnSuccessListener(taskSnapshot ->
                                                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                        task -> {
                                                            String image_link = Objects.requireNonNull(task.getResult()).toString();

                                                            user_reference.child(user.getUid()).child("name").setValue(name_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("part").setValue(prt_in_dhiti_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("city").setValue(city_ed);
                                                            user_reference.child(user.getUid()).child("phone").setValue(phone_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("address").setValue(address_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("email").setValue(email_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("aadhar").setValue(aadhar_no.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("facebook").setValue(facebook_ed.getText().toString());
                                                            user_reference.child(user.getUid()).child("twitter").setValue(twitter_ed.getText().toString());
                                                            user_reference.child(user.getUid()).child("linkedin").setValue(linkedin_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("whatsapp").setValue(whatsapp_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("instagram").setValue(insta_ed.getText().toString().trim());
                                                            user_reference.child(user.getUid()).child("dplink").setValue(image_link);

                                                            announcement_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                                                            if (Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("uid").getValue(String.class), user.getUid())) {
                                                                                announcement_ref.child(ds.getKey()).child("dplink").setValue(image_link);
                                                                                announcement_ref.child(ds.getKey()).child("writtenby").setValue(name_ed.getText().toString().trim());
                                                                            }
                                                                            if (snapshot.child(ds.getKey()).child("seenby").child(user.getUid()).exists()
                                                                                    && snapshot.child(ds.getKey()).child("uid").exists()) {
                                                                                announcement_ref.child(ds.getKey()).child("seenby").child(user.getUid()).child("dp").setValue(image_link);
                                                                            }

                                                                        }
                                                                        private_task_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                                                        if (Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("uid").getValue(String.class), user.getUid())) {

                                                                                            private_task_ref.child(ds.getKey()).child("dp").setValue(image_link);

                                                                                        }
                                                                                    }
                                                                                    dialog.dismiss();
                                                                                    runOnUiThread(() -> {
                                                                                        //  add_city_other.setText("");
                                                                                        add_city_other.setVisibility(View.GONE);
                                                                                        // city_ed="";

                                                                                    });
                                                                                } else {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                                            }
                                                                        });
                                                                    } else {
                                                                        dialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                            MotionToast.Companion.darkColorToast(Edit_profile.this,
                                                                    "Uploaded",
                                                                    "successfully updated!!",
                                                                    MotionToast.TOAST_SUCCESS,
                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                    MotionToast.SHORT_DURATION,
                                                                    ResourcesCompat.getFont(Edit_profile.this, R.font.helvetica_regular));
                                                            dialog.dismiss();
                                                        }));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            user_reference.child(user.getUid()).child("name").setValue(name_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("part").setValue(prt_in_dhiti_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("city").setValue(city_ed);
                            user_reference.child(user.getUid()).child("phone").setValue(phone_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("address").setValue(address_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("email").setValue(email_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("aadhar").setValue(aadhar_no.getText().toString().trim());
                            user_reference.child(user.getUid()).child("facebook").setValue(facebook_ed.getText().toString());
                            user_reference.child(user.getUid()).child("twitter").setValue(twitter_ed.getText().toString());
                            user_reference.child(user.getUid()).child("linkedin").setValue(linkedin_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("whatsapp").setValue(whatsapp_ed.getText().toString().trim());
                            user_reference.child(user.getUid()).child("instagram").setValue(insta_ed.getText().toString().trim());
                            announcement_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                if (Objects.equals(snapshot.child(Objects.requireNonNull(ds.getKey())).child("uid").getValue(String.class), user.getUid())) {
                                                    announcement_ref.child(ds.getKey()).child("writtenby").setValue(name_ed.getText().toString().trim());
                                                }
                                            }
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            long delayInMillis = 3000;
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    runOnUiThread(() -> {

                                        //add_city_other.setText("");
                                       // city_ed="";
                                       add_city_other.setVisibility(View.GONE);

                                    });
                                }
                            }, delayInMillis);
                            MotionToast.Companion.darkColorToast(Edit_profile.this,
                                    "Uploaded",
                                    "successfully updated!!",
                                    MotionToast.TOAST_SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
                        }
                    }
                    else{
                        if(add_city_other.getText().toString().trim().equals("")) {
                            MotionToast.Companion.darkColorToast(Edit_profile.this,
                                    "Info",
                                    "Please mention your city",
                                    MotionToast.TOAST_WARNING,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(Edit_profile.this, R.font.helvetica_regular));
                        }
                    }
                }
                else{
                    email_ed.setError("Empty");
                    MotionToast.Companion.darkColorToast(Edit_profile.this,
                            "Info",
                            "Please give your E-mail.",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
                }
            }
            else{
                MotionToast.Companion.darkColorToast(Edit_profile.this,
                        "Info",
                        "Please enter correct digit phone number.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
            }
        }
        else{
            name_ed.setError("Empty");
            MotionToast.Companion.darkColorToast(Edit_profile.this,
                    "Info",
                    "Please give your name.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(Edit_profile.this,R.font.helvetica_regular));
        }
    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
       /* if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }*/
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    addImageNote(selectedImageUri);
                }
            }
        }
    }
    private void addImageNote(Uri imageUri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
            selected_path=compressImage(imageUri+"");
            profile_image.setImageBitmap(BitmapFactory.decodeFile(selected_path));
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
        File file = new File(getExternalFilesDir(null).getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
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