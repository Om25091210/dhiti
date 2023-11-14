package com.aryomtech.dhitifoundation.Profile;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;

public class Mem_payment extends AppCompatActivity implements PaymentResultListener {

    LottieAnimationView payment_anim,payment_indication;
    DatabaseReference users_ref,membership_ref;
    String first_membership="";
    FirebaseAuth auth;
    ImageView image_logo;
    FirebaseUser user;
    EditText amount_edit;
    TextView txt_ins_first,txt_val_first,save,text_note,wallet,wallet_amt;
    int amount=0;
    Long due,xp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_payment);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        due=getIntent().getLongExtra("member due 32345",0L);
        payment_anim=findViewById(R.id.payment_anim);
        txt_ins_first=findViewById(R.id.textView58);
        image_logo=findViewById(R.id.imageView24);
        text_note=findViewById(R.id.textView63);
        txt_val_first=findViewById(R.id.textView62);
        amount_edit=findViewById(R.id.editTextNumber);

        save=findViewById(R.id.save);
        wallet=findViewById(R.id.textView60);
        wallet_amt=findViewById(R.id.textView64);
        payment_indication=findViewById(R.id.payment_indication);

        Checkout.preload(Mem_payment.this);
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        membership_ref=FirebaseDatabase.getInstance().getReference().child("membership_payment");
        first_membership=getSharedPreferences("is_first_membership_payment_01", Context.MODE_PRIVATE)
                .getString("0_or_1_first_payment","");

        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child("progress").exists()) {
                     xp = snapshot.child("progress").child("xp").getValue(Long.class);
                     if(xp!=null)
                        xp=xp+20L;
                }
                else
                    xp=20L;
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        /*if(due<200) {
            due_amt = due;
            String due_txt = due_amt + "";
            txt_val_sec.setText(due_txt);
        }
        else {
            due_amt=200L;
            String due_txt = due_amt + "";
            txt_val_sec.setText(due_txt);
        }*/
        /*if(due==0L) {
            txt_ins_first.setVisibility(View.GONE);
            txt_val_first.setVisibility(View.GONE);
            txt_ins_second.setVisibility(View.GONE);
            txt_val_sec.setVisibility(View.GONE);
            amount_edit.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            payment_anim.setVisibility(View.VISIBLE);
            payment_indication.setVisibility(View.GONE);
            Toast.makeText(this, "connecting securely", Toast.LENGTH_SHORT).show();
            new Handler(Looper.myLooper()).postDelayed(this::startPayment, 1000);
        }
        else{
            txt_ins_first.setVisibility(View.VISIBLE);
            txt_val_first.setVisibility(View.VISIBLE);
            txt_ins_second.setVisibility(View.VISIBLE);
            txt_val_sec.setVisibility(View.VISIBLE);
            amount_edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            String txt_due=due+"";
            txt_val_first.setText(txt_due);
            payment_indication.setVisibility(View.VISIBLE);
            payment_anim.setVisibility(View.GONE);
        }*/
        txt_ins_first.setVisibility(View.VISIBLE);
        txt_val_first.setVisibility(View.VISIBLE);
        amount_edit.setVisibility(View.VISIBLE);
        image_logo.setVisibility(View.VISIBLE);
        text_note.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        if(due>0){
            String txt_due=due+"";
            txt_val_first.setText(txt_due);
            String txt_d="0";
            wallet_amt.setText(txt_d);
        }
        else{
            String txt_due=-(due)+"";
            wallet_amt.setText(txt_due);
            String txt_d="0";
            txt_val_first.setText(txt_d);
        }
        payment_indication.setVisibility(View.VISIBLE);
       // payment_anim.setVisibility(View.GONE);
        save.setOnClickListener(v->proceed());

    }

    private void proceed() {

        if(!amount_edit.getText().toString().trim().equals("")){
            Toast.makeText(this, "connecting securely", Toast.LENGTH_SHORT).show();
            amount = Integer.parseInt(amount_edit.getText().toString().trim()) * 100;
            new Handler(Looper.myLooper()).postDelayed(this::startPayment, 2000);
        }
        else{
            MotionToast.Companion.darkColorToast(Mem_payment.this,
                    "Invalid ☹️",
                    "Please enter an amount.1",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(Mem_payment.this,R.font.helvetica_regular));
        }
        /*if(due!=0L) {
            if (!amount_edit.getText().toString().trim().equals("")
                    && Integer.parseInt(amount_edit.getText().toString().trim()) >= due_amt
                    ){

                if (Integer.parseInt(amount_edit.getText().toString().trim())<=due) {
                    Toast.makeText(this, "connecting securely", Toast.LENGTH_SHORT).show();
                    amount = Integer.parseInt(amount_edit.getText().toString().trim()) * 100;
                    new Handler(Looper.myLooper()).postDelayed(this::startPayment, 2000);
                }
                else{
                    MotionToast.Companion.darkColorToast(Mem_payment.this,
                            "Invalid ☹️",
                            "amount should be less or equal to due amount",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(Mem_payment.this,R.font.helvetica_regular));
                }
            }
            else{
                MotionToast.Companion.darkColorToast(Mem_payment.this,
                        "Invalid ☹️",
                        "amount should be less or equal to minimum amount",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(Mem_payment.this,R.font.helvetica_regular));
            }
        }*/
    }

    public void startPayment() {
        if (first_membership.equals("true") && amount==0)
            amount = 200 * 100;
        else if (first_membership.equals("false") && amount==0)
            amount = 350 * 100;
        if (amount != 0) {
            Checkout checkout = new Checkout();
            checkout.setImage(R.drawable.splash);
            //Pass your payment options to the Razorpay Checkout as a JSONObject
            try {
                JSONObject options = new JSONObject();

                options.put("name", "Membership Fee");
                options.put("description", "Dhiti foundation");
                options.put("theme.color", "#FFE1E3");
                options.put("currency", "INR");
                options.put("amount", amount+"");//pass amount in currency subunits
                JSONObject retryObj = new JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                checkout.open(Mem_payment.this, options);

            } catch (Exception e) {
                Log.e("puya", "Error in starting Razorpay Checkout", e);
            }
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Checkout.clearUserData(Mem_payment.this);
        txt_ins_first.setVisibility(View.GONE);
        txt_val_first.setVisibility(View.GONE);
        image_logo.setVisibility(View.GONE);
        text_note.setVisibility(View.GONE);
        wallet.setVisibility(View.GONE);
        amount_edit.setVisibility(View.GONE);
        wallet_amt.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        payment_anim.setVisibility(View.VISIBLE);
        payment_indication.setVisibility(View.GONE);
        payment_anim.setAnimation("payment_successfull.json");

        String pushkey=users_ref.push().getKey();
        assert pushkey != null;

        users_ref.child(user.getUid()).child("progress").child("xp").setValue(xp);

        membership_ref.child(user.getUid()).child(pushkey).child("key").setValue(pushkey);
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("key").setValue(pushkey);
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("payment").setValue("successfull");


        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat simpleDateFormat_time=new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());

        users_ref.child(user.getUid()).child("membership_info").child("paid").setValue("yes");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("paid").setValue("yes");

        membership_ref.child(user.getUid()).child(pushkey).child("paid_on").setValue(simpleDateFormat_time.format(cal.getTime())+"");
        users_ref.child(user.getUid()).child("membership_info").child("paid_on").setValue(simpleDateFormat.format(cal.getTime())+"");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("paid_on").setValue(simpleDateFormat_time.format(cal.getTime())+"");

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH,3);

        users_ref.child(user.getUid()).child("membership_info").child("next_date").setValue(simpleDateFormat.format(cal.getTime())+"");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("next_date").setValue(simpleDateFormat_time.format(cal.getTime())+"");

        //using cloud function if the member fails to pay the amount on given date then the amount will be added to due.
        membership_ref.child(user.getUid()).child(pushkey).child("amount_paid").setValue(amount/100+"");
        users_ref.child(user.getUid()).child("membership_info").child("amount_paid").setValue(amount/100);
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("amount_paid").setValue(amount/100+"");

        int amt=amount/100;
        Long final_amt=due-amt;
        users_ref.child(user.getUid()).child("membership_info").child("due").setValue(final_amt);
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("due").setValue(final_amt+"");
        membership_ref.child(user.getUid()).child(pushkey).child("payment_id").setValue(s+"");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("payment_id").setValue(s+"");

        /*if(due!=0L){
            if(due-amt>=0){
                Long final_amt=due-amt;
                users_ref.child(user.getUid()).child("membership_info").child("due").setValue(final_amt);
                users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("due").setValue(final_amt+"");
            }
        }
        else
            users_ref.child(user.getUid()).child("membership_info").child("due").setValue(0L);
            users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("due").setValue(0L+"");*/

        new Handler(Looper.myLooper()).postDelayed(this::finish,2000);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Checkout.clearUserData(Mem_payment.this);
        txt_ins_first.setVisibility(View.GONE);
        txt_val_first.setVisibility(View.GONE);
        text_note.setVisibility(View.GONE);
        image_logo.setVisibility(View.GONE);
        wallet_amt.setVisibility(View.GONE);
        wallet.setVisibility(View.GONE);
        amount_edit.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        payment_anim.setVisibility(View.VISIBLE);
        payment_indication.setVisibility(View.GONE);
        payment_anim.setAnimation("payment_failed.json");
        Calendar cal = Calendar.getInstance();
        String pushkey=users_ref.push().getKey();
        assert pushkey != null;
        SimpleDateFormat simpleDateFormat_time=new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());

        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("key").setValue(pushkey);
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("paid_on").setValue(simpleDateFormat_time.format(cal.getTime())+"");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("amount_paid").setValue(amount/100+"");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("payment").setValue("failed");
        users_ref.child(user.getUid()).child("membership_info").child(pushkey).child("payment_id").setValue(s+"");

        new Handler(Looper.myLooper()).postDelayed(this::finish,1500);
    }
}