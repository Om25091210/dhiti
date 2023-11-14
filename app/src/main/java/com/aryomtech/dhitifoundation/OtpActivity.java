package com.aryomtech.dhitifoundation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import www.sanju.motiontoast.MotionToast;

public class OtpActivity extends AppCompatActivity {

    PinView pinView;
    private String OTP;
    Button verify_code;
    String phone="",name="";
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String DeviceToken;
    DatabaseReference user_reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        Window window = OtpActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(OtpActivity.this, R.color.light_red));
        window.setNavigationBarColor(ContextCompat.getColor(OtpActivity.this, R.color.ui_red));

        pinView = findViewById(R.id.pin_view);
        verify_code = findViewById(R.id.verify_code);
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        OTP = getIntent().getStringExtra("auth");
        phone=getIntent().getStringExtra("phone_number");
        name=getIntent().getStringExtra("phone_name_246");
        getting_device_token();

        TextView phone_txt=findViewById(R.id.textView4);
        phone_txt.setText(phone);

        verify_code.setOnClickListener(v -> {
            String otp_text=Objects.requireNonNull(pinView.getText()).toString().trim();
            if(otp_text.length()==6){
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP ,otp_text);
                signIn(credential);
            }
            else{
                MotionToast.Companion.darkColorToast(OtpActivity.this,
                        "Info",
                        "Please enter 6 digit Otp number.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(OtpActivity.this,R.font.helvetica_regular));
            }
        });

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==6){
                    String otp_text=Objects.requireNonNull(pinView.getText()).toString().trim();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP ,otp_text);
                    signIn(credential);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void signIn(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                user=firebaseAuth.getCurrentUser();
                assert user != null;
                user_reference.child(user.getUid()).child("name").setValue(name);
                user_reference.child(user.getUid()).child("phone").setValue(user.getPhoneNumber());
                user_reference.child(user.getUid()).child("token").setValue(DeviceToken);
                user_reference.child(user.getUid()).child("uid").setValue(user.getUid());
                sendToMain();
            }else{
                MotionToast.Companion.darkColorToast(OtpActivity.this,
                        "Failed ☹️",
                        "Verification Failed!!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(OtpActivity.this,R.font.helvetica_regular));
            }
        });
    }
    private void getting_device_token() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {

        }).addOnFailureListener(e -> {
            //handle e
        }).addOnCanceledListener(() -> {
            //handle cancel
        }).addOnCompleteListener(task ->
                DeviceToken=task.getResult());

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser !=null){
            Home_gateway();
        }
    }
    private void Home_gateway() {
        Intent mainIntent = new Intent(OtpActivity.this , Home.class);
        startActivity(mainIntent);
        finish();
    }
    private void sendToMain(){
        getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE).edit()
                .putString("0_or_1_first_payment","").apply();
        startActivity(new Intent(OtpActivity.this , Locality.class));
        finish();
    }
}