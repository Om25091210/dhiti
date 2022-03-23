package com.aryomtech.dhitifoundation;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;

public class donation_activity_connecting_razorpay extends AppCompatActivity implements PaymentResultListener {

    String amount_contributing,key,deep_link_uid,identity;
    ImageView logo_img;
    DatabaseReference fluid_Cards,users_ref;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView hang_txt,connecting_txt;
    LottieAnimationView payment_anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_connecting_razorpay);

        Checkout.preload(donation_activity_connecting_razorpay.this);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.veryLightGrey));

        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        fluid_Cards= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

        amount_contributing=getIntent().getStringExtra("amount_contibuting7894568");
        key =getIntent().getStringExtra("key_parse_7568");
        deep_link_uid =getIntent().getStringExtra("deep_sending_link_uid_value_parse_7568");
        identity =getIntent().getStringExtra("name_identity_3453");

        int amount=Integer.parseInt(amount_contributing)*100;

        logo_img=findViewById(R.id.imageView21);
        hang_txt=findViewById(R.id.textView127);
        connecting_txt=findViewById(R.id.textView123);
        payment_anim=findViewById(R.id.payment_anim);
        payment_anim.setVisibility(View.GONE);
        start_payment_gate(amount);
    }

    private void start_payment_gate(int amount) {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.splash);
        //Pass your payment options to the Razorpay Checkout as a JSONObject
        if (amount != 0) {
            try {
                JSONObject options = new JSONObject();

                options.put("name", "Contributing");
                options.put("description", "Dhiti foundation");
                options.put("theme.color", "#FFE1E3");
                options.put("currency", "INR");
                options.put("amount", amount + "");//pass amount in currency subunits
                JSONObject retryObj = new JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                checkout.open(donation_activity_connecting_razorpay.this, options);

            } catch (Exception e) {
                Log.e("puya", "Error in starting Razorpay Checkout", e);
            }
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        logo_img.setVisibility(View.GONE);
        hang_txt.setVisibility(View.GONE);
        connecting_txt.setVisibility(View.GONE);
        payment_anim.setVisibility(View.VISIBLE);
        payment_anim.setAnimation("payment_successfull.json");
        Calendar cal = Calendar.getInstance();
        String pushkey=fluid_Cards.push().getKey();
        assert pushkey != null;
        SimpleDateFormat simpleDateFormat_time=new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm a", Locale.getDefault());

        //fluid_Cards.child(key).child("transactions").child(pushkey).child("key").setValue(pushkey);
        fluid_Cards.child(key).child("transactions").child(pushkey).child("paid_on").setValue(simpleDateFormat_time.format(cal.getTime())+"");
        fluid_Cards.child(key).child("transactions").child(pushkey).child("amount_paid").setValue(amount_contributing);
        fluid_Cards.child(key).child("transactions").child(pushkey).child("name").setValue(identity);
        fluid_Cards.child(key).child("transactions").child(pushkey).child("uid").setValue(user.getUid());

        if (deep_link_uid!=null){
            //fluid_Cards.child(key).child("raised_by_share").child(deep_link_uid).child(pushkey).child("key").setValue(pushkey);
            fluid_Cards.child(key).child("raised_by_share").child(deep_link_uid).child(pushkey).child("paid_on").setValue(simpleDateFormat_time.format(cal.getTime())+"");
            fluid_Cards.child(key).child("raised_by_share").child(deep_link_uid).child(pushkey).child("amount_paid").setValue(amount_contributing);
            fluid_Cards.child(key).child("raised_by_share").child(deep_link_uid).child(pushkey).child("name").setValue(identity);
            fluid_Cards.child(key).child("raised_by_share").child(deep_link_uid).child(pushkey).child("uid").setValue(user.getUid());

            users_ref.child(deep_link_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("token").exists()) {
                        String token = snapshot.child("token").getValue(String.class)+"";
                        Specific specific=new Specific();
                        specific.noti("Referral Successfull","We got a confirmed donation from "+identity+" through your referral link.We are adding some points to your profile.",token);
                    }
                    if (snapshot.child("progress").exists()){
                        try {
                            long xp = snapshot.child("progress").child("xp").getValue(Long.class);
                            xp = xp + 15;
                            users_ref.child(deep_link_uid).child("progress").child("xp").setValue(xp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        users_ref.child(deep_link_uid).child("progress").child("xp").setValue(15);
                    }
                    MotionToast.Companion.darkColorToast(donation_activity_connecting_razorpay.this,
                            "Successfull",
                            "Payment Successfull.",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(donation_activity_connecting_razorpay.this, R.font.helvetica_regular));
                    new Handler(Looper.myLooper()).postDelayed(donation_activity_connecting_razorpay.this::finish,1500);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }else {
            MotionToast.Companion.darkColorToast(this,
                    "Successfull",
                    "Payment Successfull.",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));
            new Handler(Looper.myLooper()).postDelayed(this::finish, 1500);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        logo_img.setVisibility(View.GONE);
        hang_txt.setVisibility(View.GONE);
        connecting_txt.setVisibility(View.GONE);
        payment_anim.setVisibility(View.VISIBLE);
        payment_anim.setAnimation("payment_failed.json");
        MotionToast.Companion.darkColorToast(this,
                "Failed",
                "Payment failed.",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.helvetica_regular));
        new Handler(Looper.myLooper()).postDelayed(this::finish,1500);

    }
}