package com.aryomtech.dhitifoundation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import www.sanju.motiontoast.MotionToast;

public class Splash extends AppCompatActivity {


    LinearLayout google;
    private EditText phoneNumberEdit;
    private FirebaseAuth auth;

    Dialog dialog;
    int downspeed;
    int upspeed;
    GoogleSignInClient agooglesigninclient;
    private static final int RC_SIGN_IN = 101;
    private FirebaseAuth mAuth;
    EditText phone_name;
    FirebaseUser user;
    DatabaseReference user_reference;
    String DeviceToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = Splash.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Splash.this, R.color.light_red));
        window.setNavigationBarColor(ContextCompat.getColor(Splash.this, R.color.ui_red));

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        getting_device_token();
        //TODO: Take user name while filling phone number(mandatory).
        //Hide the keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        //bindig views
        phoneNumberEdit=findViewById(R.id.editTextPhone2);
        google=findViewById(R.id.imageView);
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        google.setOnClickListener(view -> signIn_Google());
        phone_name=findViewById(R.id.name);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        agooglesigninclient = GoogleSignIn.getClient(this,gso);

        //implementing click listeners.
        findViewById(R.id.save).setOnClickListener(view ->
        {
            if(!phone_name.getText().toString().trim().equals("")) {
                RequestOtp();
                Toast.makeText(this, "Please wait while we process.", Toast.LENGTH_SHORT).show();
            }
            else{
                MotionToast.Companion.darkColorToast(Splash.this,
                        "Empty ☹️",
                        "Name required!!",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));
            }
        });

        mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                MotionToast.Companion.darkColorToast(Splash.this,
                        "Failed ☹️",
                        "Verification Failed!!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                //sometime the code is not detected automatically
                //so user has to manually enter the code
                MotionToast.Companion.darkColorToast(Splash.this,
                        "Sent",
                        "OTP sent successfully!!",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));

                new Handler(Looper.myLooper()).postDelayed(() -> {
                    String phone = phoneNumberEdit.getText().toString();
                    String phoneNumber = "+" + "91" + "" + phone;
                    Intent otpIntent = new Intent(Splash.this , OtpActivity.class);
                    otpIntent.putExtra("auth" , s);
                    otpIntent.putExtra("phone_number" , phoneNumber);
                    otpIntent.putExtra("phone_name_246" , phone_name.getText().toString());
                    startActivity(otpIntent);
                }, 2000);

            }
        };
    }

    private void signIn_Google() {
        Intent SignInIntent = agooglesigninclient.getSignInIntent();
        startActivityForResult(SignInIntent,RC_SIGN_IN);
        dialog = new Dialog(Splash.this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);


            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if(task.isSuccessful()){

                        dialog.dismiss();
                        user = mAuth.getCurrentUser();

                        assert user != null;
                        MotionToast.Companion.darkColorToast(Splash.this,"Login Successfull", Objects.requireNonNull(user.getEmail()),
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));

                        //String DeviceToken= FirebaseInstanceId.getInstance().getToken();
                      /*  String displayname;
                        String name = user.getDisplayName();

                        assert name != null;
                        displayname = name.replaceAll("[^a-zA-Z0-9]","");*/

                        user_reference.child(user.getUid()).child("email").setValue(user.getEmail());
                        user_reference.child(user.getUid()).child("uid").setValue(user.getUid());
                        user_reference.child(user.getUid()).child("name").setValue(user.getDisplayName());
                        user_reference.child(user.getUid()).child("token").setValue(DeviceToken);
                        updateUI();

                    }
                    else{

                        MotionToast.Companion.darkColorToast(Splash.this,"login Failed", Objects.requireNonNull(task.getException()).toString(),
                                MotionToast.TOAST_INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));

                        updateUI();
                    }
                });
    }

    private void updateUI() {

        getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE).edit()
                .putString("0_or_1_first_payment","").apply();

        getSharedPreferences("SignInGOOGLE201",MODE_PRIVATE).edit()
                .putString("203google_signin","google").apply();

        Intent intent = new Intent(Splash.this, Locality.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();

    }

    private void RequestOtp() {

        String phone = phoneNumberEdit.getText().toString();
        String phoneNumber = "+" + "91" + "" + phone;

        if (phone.length() == 10){
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L , TimeUnit.SECONDS)
                    .setActivity(Splash.this)
                    .setCallbacks(mCallBacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }else{
            MotionToast.Companion.darkColorToast(Splash.this,"Info","Please enter your Phone number of 10 digits!!",
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));
        }
    }
    private void getting_device_token() {


        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(nc!=null) {
            downspeed = nc.getLinkDownstreamBandwidthKbps()/1000;
            upspeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        }else{
            downspeed=0;
            upspeed=0;
        }

        if((upspeed!=0 && downspeed!=0) || getWifiLevel()!=0) {
            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                if (!TextUtils.isEmpty(token)) {
                    Log.d("token", "retrieve token successful : " + token);
                } else {
                    Log.w("token121", "token should not be null...");
                }
            }).addOnFailureListener(e -> {
                //handle e
            }).addOnCanceledListener(() -> {
                //handle cancel
            }).addOnCompleteListener(task ->
            {
                try {
                    DeviceToken = task.getResult();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public int getWifiLevel()
    {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        return WifiManager.calculateSignalLevel(linkSpeed, 5);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user !=null){
            sendToMain();
        }
    }

    private void sendToMain(){
        Intent mainIntent = new Intent(Splash.this , Home.class);
        startActivity(mainIntent);
        finish();
    }

    private void signIn(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                sendToMain();
            }else{
                MotionToast.Companion.darkColorToast(Splash.this,
                        "Failed ☹️",
                        "Sign In Failed!!",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(Splash.this,R.font.helvetica_regular));
            }
        });
    }

}