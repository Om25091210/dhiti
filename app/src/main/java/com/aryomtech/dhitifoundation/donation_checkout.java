package com.aryomtech.dhitifoundation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import soup.neumorphism.NeumorphCardView;
import www.sanju.motiontoast.MotionToast;


public class donation_checkout extends Fragment {

    View view;
    String key,deep_sending_link_uid_value,name_str;
    TextView goal,thousand,five_hundred,three_hundred,name;
    EditText input_price;
    NeumorphCardView proceed;
    //RoundedImageView image;
    FirebaseAuth auth;
    CheckBox checkbox;
    FirebaseUser user;
    String amount_contributing;
    String sum;
    SimpleDraweeView draweeView;
    String image_link_args
            ,goal_args;
    private Context contextNullSafe;
    DatabaseReference fluid_ref,users_ref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_donation_checkout, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        //image=view.findViewById(R.id.imageNote);
        goal=view.findViewById(R.id.textView124);
        input_price=view.findViewById(R.id.editTextTextMultiLine);
        proceed=view.findViewById(R.id.proceed);
        name=view.findViewById(R.id.textView128);
        checkbox=view.findViewById(R.id.checkBox3);
        draweeView  = view.findViewById(R.id.imageNote);
        thousand=view.findViewById(R.id.thousand);
        five_hundred=view.findViewById(R.id.five_hundred);
        three_hundred=view.findViewById(R.id.three_hundred);
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        fluid_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        try {
            key = getArguments().getString("sending_key")+"";
            sum = getArguments().getString("deep_sending_link_sum")+"";
            deep_sending_link_uid_value = getArguments().getString("deep_sending_link_uid_value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (Integer.parseInt(sum)!=0) {
                amount_contributing=sum;
                input_price.setText(sum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fluid_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                image_link_args = snapshot.child("image_link_original").getValue(String.class);
                goal_args = snapshot.child("goal").getValue(String.class);
                try {
                    goal.setText(goal_args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (image_link_args != null) {
                    Uri uri = Uri.parse(image_link_args);
                    ImageRequest request = ImageRequest.fromUri(uri);

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(draweeView.getController()).build();

                    draweeView.setController(controller);
                    /*try {
                        Glide.with(getContextNullSafety())
                                .asBitmap()
                                .load(image_link_args)
                                .placeholder(R.drawable.ic_image_holder)
                                .thumbnail(0.1f)
                                .override(150, 150)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        new Handler(Looper.myLooper()).postDelayed(() -> {
                                            try {
                                                Glide.with(getContextNullSafety())
                                                        .asBitmap()
                                                        .load(image_link_args)
                                                        .into(image);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }, 2000);
                                        return false;
                                    }
                                })
                                .into(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        users_ref=FirebaseDatabase.getInstance().getReference().child("users");
        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_str=snapshot.child("name").getValue(String.class);
                try {
                    name.setText(name_str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        checkbox.setOnClickListener(v->{
            if (checkbox.isChecked()){
                String str="Well wisher";
                name.setText(str);
            }
            else{
                name.setText(name_str);//
            }
        });

        thousand.setOnClickListener(v->{
            amount_contributing="1000";
            input_price.setText(amount_contributing);
            thousand.setBackgroundResource(R.drawable.bg_of_add_donations);
            five_hundred.setBackgroundResource(R.drawable.border_amount_bg);
            three_hundred.setBackgroundResource(R.drawable.border_amount_bg);
        });
        five_hundred.setOnClickListener(v->{
            amount_contributing="500";
            input_price.setText(amount_contributing);
            thousand.setBackgroundResource(R.drawable.border_amount_bg);
            five_hundred.setBackgroundResource(R.drawable.bg_of_add_donations);
            three_hundred.setBackgroundResource(R.drawable.border_amount_bg);
        });
        three_hundred.setOnClickListener(v->{
            amount_contributing="300";
            input_price.setText(amount_contributing);
            thousand.setBackgroundResource(R.drawable.border_amount_bg);
            five_hundred.setBackgroundResource(R.drawable.border_amount_bg);
            three_hundred.setBackgroundResource(R.drawable.bg_of_add_donations);
        });


        input_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()<=7) {
                    amount_contributing = editable.toString();
                    thousand.setBackgroundResource(R.drawable.border_amount_bg);
                    five_hundred.setBackgroundResource(R.drawable.border_amount_bg);
                    three_hundred.setBackgroundResource(R.drawable.border_amount_bg);
                }
                else {
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Sorry \uD83D\uDE36",
                            "That's too much amount. \uD83D\uDE2C.",
                            MotionToast.TOAST_INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
                    input_price.setText("");
                }
            }
        });
        proceed.setOnClickListener(v->Proceed());

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    private void Proceed() {
        if(amount_contributing!=null) {
            if (Integer.parseInt(amount_contributing)>0) {
                Intent passing_value = new Intent(getContext(), donation_activity_connecting_razorpay.class);
                passing_value.putExtra("amount_contibuting7894568", amount_contributing);
                passing_value.putExtra("key_parse_7568", key);
                passing_value.putExtra("deep_sending_link_uid_value_parse_7568", deep_sending_link_uid_value);
                passing_value.putExtra("name_identity_3453", name.getText().toString());
                startActivity(passing_value);
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Add an amount greater than zero",
                        "Zero looks good with a digit.",
                        MotionToast.TOAST_INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Add an amount",
                    "To proceed, we request you to add an amount.",
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(), R.font.helvetica_regular));
        }
    }

    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}