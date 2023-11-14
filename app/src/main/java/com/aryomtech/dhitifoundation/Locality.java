package com.aryomtech.dhitifoundation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import soup.neumorphism.NeumorphButton;

public class Locality extends AppCompatActivity {

    //We are taking two shared preferences here
    //1. for getting the locality and
    //2. for checking that user has successfully passed this activity or not.

    LinearLayout animation_view,animation_view1,animation_view2,animation_view3;
    DatabaseReference user_reference;
    private FirebaseAuth mAuth;
    DatabaseReference users_ref;
    NeumorphButton noneoftheabove;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locality);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        user_reference= FirebaseDatabase.getInstance().getReference().child("users");
        findViewById(R.id.noneoftheabove).setOnClickListener(view->send_to_form());

        Window window = Locality.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Locality.this, R.color.light_red));
        window.setNavigationBarColor(ContextCompat.getColor(Locality.this, R.color.ui_red));

        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        noneoftheabove=findViewById(R.id.noneoftheabove);
        noneoftheabove.setOnClickListener(v->{

            user_reference.child(user.getUid()).child("city").setValue("Not provided");

            send_to_form();
        });
        animation_view=findViewById(R.id.animation_view);
        animation_view.setOnClickListener(v -> {

            user_reference.child(user.getUid()).child("city").setValue("Bilaspur");

            send_to_form();
        });

        animation_view1=findViewById(R.id.animation_view1);
        animation_view1.setOnClickListener(v -> {

            user_reference.child(user.getUid()).child("city").setValue("Korba");

            send_to_form();
        });

        animation_view2=findViewById(R.id.animation_view2);
        animation_view2.setOnClickListener(v -> {

            user_reference.child(user.getUid()).child("city").setValue("Bhilai");

            send_to_form();
        });

        animation_view3=findViewById(R.id.animation_view3);
        animation_view3.setOnClickListener(v -> {

            user_reference.child(user.getUid()).child("city").setValue("Janjgir-Champa");

            send_to_form();
        });
    }

    private void send_to_form() {

        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.child("identity").exists()){//if guest or member , chapter-head.
                       if (snapshot.child("request").exists()){//member or chapter-head.
                           if (Objects.equals(snapshot.child("request").getValue(String.class), "approved")){//member or chapter-head
                               Intent mainIntent = new Intent(Locality.this , Home.class);
                               startActivity(mainIntent);
                               finish();
                           }
                           else {// 1st guest and then requested.
                               Intent mainIntent = new Intent(Locality.this, Approval.class);
                               mainIntent.putExtra("sending_for_4568","Locality");
                               startActivity(mainIntent);
                               finish();
                           }
                       }
                       else{ // guest mode.
                           if (Objects.equals(snapshot.child("identity").getValue(String.class), "guest")){
                               Intent mainIntent = new Intent(Locality.this , Form_Fill.class);
                               startActivity(mainIntent);
                               finish();
                           }
                       }
                   }
                   else{//requested for approval
                       if (snapshot.child("request").exists()){
                           Intent mainIntent = new Intent(Locality.this , Approval.class);
                           mainIntent.putExtra("sending_for_4568","Locality");
                           startActivity(mainIntent);
                           finish();
                       }
                       else{//new user
                           Intent mainIntent = new Intent(Locality.this , Form_Fill.class);
                           startActivity(mainIntent);
                           finish();
                       }
                   }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(Locality.this , Splash.class));
            finish();
        }
    }
}