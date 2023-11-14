package com.aryomtech.dhitifoundation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.exifinterface.media.ExifInterface;

import com.aryomtech.dhitifoundation.fcm.Specific;
import com.aryomtech.dhitifoundation.models.forms_data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.s.m.shahi.custombottomsheetdatepicker.DatePicker;
import com.s.m.shahi.custombottomsheetdatepicker.DatePickerListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import io.ghyeok.stickyswitch.widget.StickySwitch;
import soup.neumorphism.NeumorphButton;
import www.sanju.motiontoast.MotionToast;

public class Form_Fill extends AppCompatActivity implements DatePickerListener {

    private DatePicker datePicker;
    TextView datedit,select;
    NeumorphButton pre_member,chapter_head,guest;
    EditText name_field,blood_field_,city_field,contact_field,address_field
            ,qualification_field,profession_field,email_field,heard_from_where_field,
            dedicate_time_field_other,suggestion_field_,why_dhiti_field,nameOfSchoolOrCollege_field,
            dedicate_talent_field_other;
    public static final int REQUEST_CODE_SELECT_IMAGE = 2;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    RadioButton dt,start_dhiti_city,represent_dhiti_s_c,experience_comm;

    RadioGroup rg0,rg1,rg2,rg3;

    CheckBox c_w_c1,g_d_c2,e_m_c3,p_r_c4,s_amp_f_c5,c_amp_d_c6
            ,photo_c7,c_a_c8,other_c9;

    NeumorphButton submit;

    DatabaseReference forms,members_ref,users_ref,chapter_ref;
    String pushkey="";
    String profile_identity_value;
    forms_data forms_data;
    Dialog dialog;
    private ImageView imageNote,imageRemoveImage;
    StickySwitch stickySwitch;
    String which_switch="";
    int check_box_check=0;
    NeumorphButton register;
    String selectedImagePath="";
    NestedScrollView nestedscroll,nestedscroll0;
    WebView webview;
    private FirebaseAuth mAuth;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_fill);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        profile_identity_value=getIntent().getStringExtra("sending_from_profile_identity");

        datePicker = new DatePicker();

        mAuth=FirebaseAuth.getInstance();

        forms_data=new forms_data();

        //binding views
        stickySwitch=findViewById(R.id.sticky_switch);
        submit=findViewById(R.id.submit);
        webview=findViewById(R.id.webview);
        select=findViewById(R.id.select);
        imageNote = findViewById(R.id.imageNote);
        imageRemoveImage = findViewById(R.id.imageRemoveImage);
        nestedscroll=findViewById(R.id.nestedscroll);
        nestedscroll0=findViewById(R.id.nestedscroll0);
        nestedscroll.setVisibility(View.GONE);
        register=findViewById(R.id.register);
        webview.loadUrl("https://www.dhitifoundation.com");
        webview.setWebViewClient(new MyBrowser());

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageCommitVisible (WebView view,
                                             String url){

        }
        });

        register.setOnClickListener(v->{
            nestedscroll.setVisibility(View.VISIBLE);
            nestedscroll0.setVisibility(View.GONE);
        });
        name_field=findViewById(R.id.editTextTextMultiLine);
        blood_field_=findViewById(R.id.editTextTextMultiLine1);
        city_field=findViewById(R.id.editTextTextMultiLine2);
        contact_field=findViewById(R.id.editTextTextMultiLine3);
        address_field=findViewById(R.id.editTextTextMultiLine4);
        qualification_field=findViewById(R.id.editTextTextMultiLine5);
        profession_field=findViewById(R.id.editTextTextMultiLine6);
        email_field=findViewById(R.id.editTextTextMultiLine7);
        heard_from_where_field=findViewById(R.id.editTextTextMultiLine10);
        dedicate_time_field_other=findViewById(R.id.editTextTextMultiLine11);
        suggestion_field_=findViewById(R.id.editTextTextMultiLine12);
        why_dhiti_field=findViewById(R.id.editTextTextMultiLine16);
        nameOfSchoolOrCollege_field=findViewById(R.id.editTextTextMultiLine17);
        dedicate_talent_field_other=findViewById(R.id.editTextTextMultiLine18);
        pre_member=findViewById(R.id.pre_member);
        chapter_head=findViewById(R.id.chapter_head);
        guest=findViewById(R.id.guest);

        select.setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(Form_Fill.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Form_Fill.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });
        if(profile_identity_value!=null){
            guest.setVisibility(View.GONE);
            switch (profile_identity_value) {
                case "chapter-head":
                    pre_member.setVisibility(View.GONE);
                    chapter_head.setVisibility(View.GONE);
                    break;
                case "member":
                    pre_member.setVisibility(View.GONE);
                    chapter_head.setVisibility(View.VISIBLE);
                    break;
                case "guest":
                    pre_member.setVisibility(View.VISIBLE);
                    chapter_head.setVisibility(View.VISIBLE);
                    break;
            }
        }
        rg0=findViewById(R.id.rg0);
        rg1=findViewById(R.id.rg1);
        rg2=findViewById(R.id.rg2);
        rg3=findViewById(R.id.rg3);

        c_w_c1=findViewById(R.id.c1);
        g_d_c2=findViewById(R.id.c2);
        e_m_c3=findViewById(R.id.c3);
        p_r_c4=findViewById(R.id.c4);
        s_amp_f_c5=findViewById(R.id.c5);
        c_amp_d_c6=findViewById(R.id.c6);
        photo_c7=findViewById(R.id.c7);
        c_a_c8=findViewById(R.id.c8);
        other_c9=findViewById(R.id.c9);

        datedit =findViewById(R.id.editTextTextMultiLine8);
        datedit.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), datePicker.getTag()));

        stickySwitch.setOnSelectedChangeListener((direction, text) -> {
            if(direction.name().equals("LEFT")){
                which_switch="Male";
            }
            else{
                which_switch="Female";
            }
        });
        imageRemoveImage.setOnClickListener(v-> removeImage());

        Window window = Form_Fill.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Form_Fill.this, R.color.light_red));
        window.setNavigationBarColor(ContextCompat.getColor(Form_Fill.this, R.color.ui_red));


        rg0.setOnCheckedChangeListener((group,checkedId)->{

            dt=findViewById(checkedId);
            check_box_check=1;
            //TODO: when dt is other then we will go for  visibility and then "dedicate_time_field_other".getText() and it should not be empty.
            if(dt.getText().toString().equalsIgnoreCase("other")){
                dedicate_time_field_other.setVisibility(View.VISIBLE);
            }
            else{
                dedicate_time_field_other.setVisibility(View.GONE);
            }

        });
        rg1.setOnCheckedChangeListener((group,checkedId)-> start_dhiti_city =findViewById(checkedId));

        rg2.setOnCheckedChangeListener((group,checkedId)-> represent_dhiti_s_c=findViewById(checkedId));

        rg3.setOnCheckedChangeListener((group,checkedId)-> experience_comm=findViewById(checkedId));

        pre_member.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView text=dialog.findViewById(R.id.textView94);
            String txt="Send request to Join.";
            String yes_txt="Send";
            text.setText(txt);
            TextView yes=dialog.findViewById(R.id.textView95);
            yes.setText(yes_txt);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                dialog.dismiss();
                send_for_member_request();
            });
        });

        chapter_head.setOnClickListener(v->{
            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView text=dialog.findViewById(R.id.textView94);
            String txt="Send request to Join.";
            String yes_txt="Send";
            text.setText(txt);
            TextView yes=dialog.findViewById(R.id.textView95);
            yes.setText(yes_txt);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                dialog.dismiss();
                send_for_chapter_head_request();
            });
        });

        guest.setOnClickListener(v->{
            users_ref=FirebaseDatabase.getInstance().getReference().child("users");
            FirebaseUser user=mAuth.getCurrentUser();
            users_ref.child(Objects.requireNonNull(user).getUid()).child("identity").setValue("guest");
            Toast.makeText(Form_Fill.this, "Entering as guest.", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(Form_Fill.this, Home.class);
            startActivity(mainIntent);
            finish();
        });
        other_c9.setOnClickListener(v->{

            if(other_c9.isChecked()){
                dedicate_talent_field_other.setVisibility(View.VISIBLE);
            }
            else{
                dedicate_talent_field_other.setVisibility(View.GONE);
            }
        });

        submit.setOnClickListener(v->{

                if(!name_field.getText().toString().trim().isEmpty()){
                    if(!city_field.getText().toString().trim().isEmpty()){
                        if(!contact_field.getText().toString().trim().isEmpty() && contact_field.getText().toString().length()==10){
                            if(!address_field.getText().toString().trim().isEmpty()){
                                if(!qualification_field.getText().toString().trim().isEmpty()){
                                    if (!profession_field.getText().toString().trim().isEmpty()){
                                        if (!email_field.getText().toString().trim().isEmpty()){
                                            if(!heard_from_where_field.getText().toString().trim().isEmpty()){
                                                if(!why_dhiti_field.getText().toString().trim().isEmpty()){
                                                    if(!nameOfSchoolOrCollege_field.getText().toString().trim().isEmpty()){
                                                        if(check_box_check==1){
                                                            if(!datedit.getText().toString().trim().isEmpty() && !datedit.getText().toString().contains("null")){
                                                                if(c_w_c1.isChecked() || g_d_c2.isChecked() || e_m_c3.isChecked()
                                                                   || p_r_c4.isChecked() || s_amp_f_c5.isChecked()
                                                                   || c_amp_d_c6.isChecked() || photo_c7.isChecked()
                                                                   || c_a_c8.isChecked() || other_c9.isChecked()) {
                                                                    if (!selectedImagePath.equals("")) {


                                                                        forms = FirebaseDatabase.getInstance().getReference().child("forms");
                                                                        pushkey = forms.push().getKey();

                                                                        assert pushkey != null;

                                                                        forms_data.setName(name_field.getText().toString().trim());
                                                                        forms_data.setBlood("" + blood_field_.getText().toString().trim());
                                                                        forms_data.setCity(city_field.getText().toString().trim());
                                                                        forms_data.setContact(contact_field.getText().toString().trim());
                                                                        forms_data.setAddress(address_field.getText().toString().trim());
                                                                        forms_data.setQualification(qualification_field.getText().toString().trim());
                                                                        forms_data.setProfession(profession_field.getText().toString().trim());
                                                                        forms_data.setEmail(email_field.getText().toString().trim());
                                                                        forms_data.setAge(datedit.getText().toString().trim());
                                                                        if (which_switch.equals("")) {
                                                                            which_switch = "Male";
                                                                        }
                                                                        forms_data.setGender(which_switch);
                                                                        forms_data.setHeard_about_dhiti(heard_from_where_field.getText().toString().trim());

                                                                        if (dt.getText().toString().equalsIgnoreCase("other")) {
                                                                            forms_data.setDedicate_time(dedicate_time_field_other.getText().toString().trim());
                                                                        } else {
                                                                            forms_data.setDedicate_time(dt.getText().toString());
                                                                        }

                                                                        forms_data.setSuggestion("" + suggestion_field_.getText().toString().trim());
                                                                        forms_data.setStart_city_dhiti(start_dhiti_city.getText().toString());
                                                                        forms_data.setRepresent_dhiti(represent_dhiti_s_c.getText().toString());
                                                                        forms_data.setExperience_comm(experience_comm.getText().toString());
                                                                        forms_data.setWhy_dhiti(why_dhiti_field.getText().toString().trim());
                                                                        forms_data.setName_of_school_college(nameOfSchoolOrCollege_field.getText().toString().trim());

                                                                        ArrayList<String> arrayList = new ArrayList<>();

                                                                        if (c_w_c1.isChecked()) {
                                                                            arrayList.add("CONTENT WRITTING");
                                                                        }
                                                                        if (g_d_c2.isChecked()) {
                                                                            arrayList.add("GRAPHIC DESIGNING");
                                                                        }
                                                                        if (e_m_c3.isChecked()) {
                                                                            arrayList.add("EVENT MANAGEMENT");
                                                                        }
                                                                        if (p_r_c4.isChecked()) {
                                                                            arrayList.add("PUBLIC RELATION");
                                                                        }
                                                                        if (s_amp_f_c5.isChecked()) {
                                                                            arrayList.add("SPONSERSHIP AND FINANCE");
                                                                        }
                                                                        if (c_amp_d_c6.isChecked()) {
                                                                            arrayList.add("CREATIVES &DECORATION WORK");
                                                                        }
                                                                        if (photo_c7.isChecked()) {
                                                                            arrayList.add("PHOTOGRAPHY");
                                                                        }
                                                                        if (c_a_c8.isChecked()) {
                                                                            arrayList.add("CULTURAL ACTIVITY");
                                                                        }
                                                                        if (other_c9.isChecked()) {
                                                                            arrayList.add(dedicate_talent_field_other.getText().toString().trim());
                                                                        }

                                                                        forms_data.setDedicate_talent(arrayList.toString());
                                                                        forms_data.setPushkey(pushkey);
                                                                        FirebaseUser form_user = mAuth.getCurrentUser();
                                                                        forms_data.setUid(Objects.requireNonNull(form_user).getUid());

                                                                        dialog = new Dialog(Form_Fill.this);
                                                                        dialog.setCancelable(true);
                                                                        dialog.setContentView(R.layout.loading_dialog);
                                                                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                                        dialog.show();
                                                                        //for image storing
                                                                        String imagepath = "forms/" + name_field.getText().toString().trim() + "__" + pushkey + ".png";

                                                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);

                                                                        try {
                                                                            InputStream stream = new FileInputStream(new File(selectedImagePath));

                                                                            storageReference.putStream(stream)
                                                                                    .addOnSuccessListener(taskSnapshot ->
                                                                                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                                                                    task -> {
                                                                                                        String image_link = Objects.requireNonNull(task.getResult()).toString();
                                                                                                        forms_data.setImage_link(image_link);
                                                                                                        //sending data pack
                                                                                                        forms.child(pushkey).setValue(forms_data);
                                                                                                        if (!(Form_Fill.this).isFinishing() && !((Form_Fill.this).isDestroyed())) {
                                                                                                            dialog.dismiss();
                                                                                                        }

                                                                                                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                                                                "Success",
                                                                                                                "Submitted successfully",
                                                                                                                MotionToast.TOAST_SUCCESS,
                                                                                                                MotionToast.GRAVITY_BOTTOM,
                                                                                                                MotionToast.LONG_DURATION,
                                                                                                                ResourcesCompat.getFont(Form_Fill.this, R.font.helvetica_regular));


                                                                                                        getSharedPreferences("Form_fill_Activity_done_or_not", MODE_PRIVATE).edit()
                                                                                                                .putBoolean("Done_or_not_form_fill", true).apply();


                                                                                                        if (profile_identity_value == null) {
                                                                                                            Intent intent = new Intent(Form_Fill.this, Approval.class);
                                                                                                            intent.putExtra("sending_for_4568", "guest");
                                                                                                            startActivity(intent);
                                                                                                            finish();
                                                                                                        } else {
                                                                                                            MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                                                                    "Success",
                                                                                                                    "Given for verification",
                                                                                                                    MotionToast.TOAST_SUCCESS,
                                                                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                                                                    MotionToast.LONG_DURATION,
                                                                                                                    ResourcesCompat.getFont(Form_Fill.this, R.font.helvetica_regular));
                                                                                                            finish();
                                                                                                        }
                                                                                                    }));
                                                                        } catch (FileNotFoundException e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                    }
                                                                    else{
                                                                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                                "Photo not found!",
                                                                                "Selection is required.",
                                                                                MotionToast.TOAST_INFO,
                                                                                MotionToast.GRAVITY_BOTTOM,
                                                                                MotionToast.LONG_DURATION,
                                                                                ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                                    }
                                                                }
                                                                else{
                                                                    MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                            "Info",
                                                                            "Selection is required.",
                                                                            MotionToast.TOAST_INFO,
                                                                            MotionToast.GRAVITY_BOTTOM,
                                                                            MotionToast.LONG_DURATION,
                                                                            ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                                }
                                                            }
                                                            else{
                                                                MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                        "Info",
                                                                        "Age selection is required.",
                                                                        MotionToast.TOAST_INFO,
                                                                        MotionToast.GRAVITY_BOTTOM,
                                                                        MotionToast.LONG_DURATION,
                                                                        ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                            }
                                                        }
                                                        else{
                                                            MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                    "Info",
                                                                    "How would you like to...! selection is required.",
                                                                    MotionToast.TOAST_INFO,
                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                    MotionToast.LONG_DURATION,
                                                                    ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                        }
                                                    }
                                                    else{
                                                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                                "Info",
                                                                "Name of school..! cannot be Empty",
                                                                MotionToast.TOAST_INFO,
                                                                MotionToast.GRAVITY_BOTTOM,
                                                                MotionToast.LONG_DURATION,
                                                                ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                    }
                                                }
                                                else{
                                                    MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                            "Info",
                                                            "Why would you like to...!  cannot be Empty",
                                                            MotionToast.TOAST_INFO,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                                }
                                            }
                                            else{
                                                MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                        "Info",
                                                        "From where you got to know..! cannot be Empty",
                                                        MotionToast.TOAST_INFO,
                                                        MotionToast.GRAVITY_BOTTOM,
                                                        MotionToast.LONG_DURATION,
                                                        ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                            }
                                        }
                                        else{
                                            MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                    "Info",
                                                    "Email cannot be Empty",
                                                    MotionToast.TOAST_INFO,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                        }
                                    }
                                    else{
                                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                                "Info",
                                                "Profession cannot be Empty",
                                                MotionToast.TOAST_INFO,
                                                MotionToast.GRAVITY_BOTTOM,
                                                MotionToast.LONG_DURATION,
                                                ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                    }
                                }
                                else{
                                    MotionToast.Companion.darkColorToast(Form_Fill.this,
                                            "Info",
                                            "Qualification cannot be Empty",
                                            MotionToast.TOAST_INFO,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                                }
                            }
                            else{
                                MotionToast.Companion.darkColorToast(Form_Fill.this,
                                        "Info",
                                        "Address cannot be Empty",
                                        MotionToast.TOAST_INFO,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                            }
                        }
                        else{
                            MotionToast.Companion.darkColorToast(Form_Fill.this,
                                    "Info",
                                    "Contact cannot be Empty and must be of 10 digits",
                                    MotionToast.TOAST_INFO,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                        }
                    }
                    else{
                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                "Info",
                                "City cannot be Empty",
                                MotionToast.TOAST_INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                    }
                }
                else{
                    MotionToast.Companion.darkColorToast(Form_Fill.this,
                            "Info",
                            "Name cannot be Empty",
                            MotionToast.TOAST_INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                }
        });
    }
    void removeImage(){
        imageNote.setImageBitmap(null);
        imageNote.setVisibility(View.GONE);
        findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
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
                Toast.makeText(Form_Fill.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
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

    }
    private void addImageNote(Uri imageUri) throws IOException {

        imageNote.setVisibility(View.VISIBLE);
        selectedImagePath = compressImage(imageUri+"");
        imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

    }
    private void send_for_chapter_head_request() {
        chapter_ref=FirebaseDatabase.getInstance().getReference().child("chapter_head_Request");
        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        String pushkey=chapter_ref.push().getKey();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        users_ref.child(Objects.requireNonNull(firebaseUser).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name=snapshot.child("name").getValue(String.class);
                    if(snapshot.child("email").exists()) {
                        String email=snapshot.child("email").getValue(String.class);
                        chapter_ref.child(Objects.requireNonNull(pushkey)).child("email").setValue(email);
                    }
                    else if(snapshot.child("phone").exists()){
                        String phone=snapshot.child("phone").getValue(String.class);
                        chapter_ref.child(Objects.requireNonNull(pushkey)).child("phone").setValue(phone);
                    }
                    chapter_ref.child(Objects.requireNonNull(pushkey)).child("name").setValue(name);
                    chapter_ref.child(Objects.requireNonNull(pushkey)).child("key").setValue(pushkey);
                    chapter_ref.child(Objects.requireNonNull(pushkey)).child("uid").setValue(firebaseUser.getUid());
                    users_ref.child(firebaseUser.getUid()).child("request").setValue("given");
                    send_notification_to_admin(name,"Chapter-head");
                    Toast.makeText(Form_Fill.this, "Sending...", Toast.LENGTH_SHORT).show();
                    if (profile_identity_value==null) {
                        Intent approval = new Intent(Form_Fill.this, Approval.class);
                        approval.putExtra("sending_for_4568", "chapter_head");
                        startActivity(approval);
                        finish();
                    }
                    else {
                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                "Success",
                                "Given for verification",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Form_Fill.this,R.font.helvetica_regular));
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void send_for_member_request() {
        members_ref=FirebaseDatabase.getInstance().getReference().child("member_Request");
        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        String pushkey=members_ref.push().getKey();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        users_ref.child(Objects.requireNonNull(firebaseUser).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name=snapshot.child("name").getValue(String.class);
                    if(snapshot.child("email").exists()) {
                        String email=snapshot.child("email").getValue(String.class);
                        members_ref.child(Objects.requireNonNull(pushkey)).child("email").setValue(email);
                    }
                    else if(snapshot.child("phone").exists()){
                        String phone=snapshot.child("phone").getValue(String.class);
                        members_ref.child(Objects.requireNonNull(pushkey)).child("phone").setValue(phone);
                    }
                    members_ref.child(Objects.requireNonNull(pushkey)).child("name").setValue(name);
                    members_ref.child(Objects.requireNonNull(pushkey)).child("key").setValue(pushkey);
                    members_ref.child(Objects.requireNonNull(pushkey)).child("uid").setValue(firebaseUser.getUid());
                    users_ref.child(firebaseUser.getUid()).child("request").setValue("given");
                    send_notification_to_admin(name,"member");
                    Toast.makeText(Form_Fill.this, "Sending...", Toast.LENGTH_SHORT).show();
                    if (profile_identity_value==null) {
                        Intent approval = new Intent(Form_Fill.this, Approval.class);
                        approval.putExtra("sending_for_4568", "member");
                        startActivity(approval);
                        finish();
                    }
                    else {
                        MotionToast.Companion.darkColorToast(Form_Fill.this,
                                "Success",
                                "Given for verification",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Form_Fill.this, R.font.helvetica_regular));
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void send_notification_to_admin(String name,String which_role) {
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds_keys:snapshot.getChildren()){
                    if(Objects.equals(snapshot.child(ds_keys.getKey()).child("identity").getValue(String.class), "admin")){
                        if(snapshot.child(ds_keys.getKey()).child("token").exists()){
                            String token=snapshot.child(ds_keys.getKey()).child("token").getValue(String.class);
                            Specific specific=new Specific();
                            if (token != null) {
                                specific.noti("New Approval",name+" wants approval to continue as a "+which_role,token);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onPickupDate(String value) {
        datedit.setText(value);
    }

    @Override
    public void onDay(String day) {

    }

    @Override
    public void onMonth(String month) {

    }

    @Override
    public void onYear(String onYear) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(Form_Fill.this , Splash.class));
            finish();
        }

    }
    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
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

        int actualHeight = options.outHeight+1;
        int actualWidth = options.outWidth+1;

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