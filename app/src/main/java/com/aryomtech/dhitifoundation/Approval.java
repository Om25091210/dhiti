package com.aryomtech.dhitifoundation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

public class Approval extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String for_which_thing;
    TextView message;
    DatabaseReference users_ref;
    NeumorphButton guest;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        mAuth=FirebaseAuth.getInstance();
        for_which_thing=getIntent().getStringExtra("sending_for_4568");
        message=findViewById(R.id.textView14);
        guest=findViewById(R.id.guest);
        switch (for_which_thing) {
            case "member": {
                String str_msg = "Your request to join as a member has been received. Please wait for the verification. Thank you.";
                message.setText(str_msg);
                break;
            }
            case "chapter_head": {
                String str_msg = "Your request to join as a Chapter head has been received. Please wait for the verification. Thank you.";
                message.setText(str_msg);
                break;
            }
            case "Locality": {
                String str_msg = "You can enter as a guest till you get verified.";
                message.setText(str_msg);
                break;
            }
            default:
                String str_msg="You can enter as a guest till you get verified";
                message.setText(str_msg);
                break;
        }
        //Need Testing
        //Adding EVENT Listener for taking user directly to home if he gets approved.
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseUser c_user=mAuth.getCurrentUser();
        valueEventListener=users_ref.child(c_user.getUid()).child("identity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!Objects.equals(snapshot.getValue(String.class), "guest")){
                    Intent mainIntent = new Intent(Approval.this , Home.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        guest.setOnClickListener(v->{
            users_ref= FirebaseDatabase.getInstance().getReference().child("users");
            FirebaseUser user=mAuth.getCurrentUser();
            users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(Objects.requireNonNull(user).getUid()).child("identity").exists()){
                        Intent mainIntent = new Intent(Approval.this , Home.class);
                        startActivity(mainIntent);
                    }
                    else{
                        users_ref.child(user.getUid()).child("identity").setValue("guest");
                        Intent mainIntent = new Intent(Approval.this , Home.class);
                        startActivity(mainIntent);
                    }
                    finish();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        Window window = Approval.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Approval.this, R.color.light_red));
        window.setNavigationBarColor(ContextCompat.getColor(Approval.this, R.color.ui_red));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(Approval.this , Splash.class));
            finish();
        }

        //TODO:create a dialog box to show wait for approval in the already a member button.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        users_ref.removeEventListener(valueEventListener);
    }

}