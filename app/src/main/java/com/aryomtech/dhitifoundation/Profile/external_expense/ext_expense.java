package com.aryomtech.dhitifoundation.Profile.external_expense;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.OtpActivity;
import com.aryomtech.dhitifoundation.Profile.external_expense.Adapter.exp_ext_Adapter;
import com.aryomtech.dhitifoundation.Profile.external_expense.Model.expenseData;
import com.aryomtech.dhitifoundation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import www.sanju.motiontoast.MotionToast;


public class ext_expense extends Fragment {

    View view;
    EditText search;
    private Context contextNullSafe;
    LottieAnimationView fab;
    ImageView image;
    TextView msg;
    DatabaseReference expense_record,users_ref;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    String file_name,city;
    String str_txt;
    Dialog dialog,dialog1;
    ProgressBar progressBar;
    ArrayList<expenseData> list,mylist;
    EditText des;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 110;
    public static final int REQUEST_CODE_SELECT_IMAGE = 220;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_ext_expense, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        fab=view.findViewById(R.id.fab);
        recyclerView=view.findViewById(R.id.rv_content);
        image=view.findViewById(R.id.imageView13);
        msg=view.findViewById(R.id.textView112);
        search=view.findViewById(R.id.search);
        progressBar=view.findViewById(R.id.progressBar2);
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        list=new ArrayList<>();
        mylist=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        expense_record= FirebaseDatabase.getInstance().getReference().child("expense_record");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

        users_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("city").exists())
                    city=snapshot.child("city").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(layoutManager);

        fab.setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(getContextNullSafety(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        new Handler(Looper.myLooper()).postDelayed(this::get_data,1000);
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

        }
        return view;
    }

    private void search(String s) {
        mylist.clear();
        for(expenseData Objects:list){
            if (Objects.getFile_name().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if (Objects.getDescription().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
            else if(Objects.getCity().toLowerCase().contains(s.toLowerCase().trim())){
                mylist.add(Objects);
            }
        }
        exp_ext_Adapter exp_ext_adapter=new exp_ext_Adapter(getContext(),mylist,"user");
        exp_ext_adapter.notifyDataSetChanged();
        if (recyclerView!=null)
            recyclerView.setAdapter(exp_ext_adapter);
    }

    private void get_data() {
        expense_record.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    mylist.clear();
                    image.setVisibility(View.GONE);
                    msg.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds:snapshot.getChildren()){
                        list.add(ds.getValue(expenseData.class));
                    }
                    Collections.reverse(list);
                    exp_ext_Adapter exp_ext_adapter=new exp_ext_Adapter(getContext(),list,"user");
                    exp_ext_adapter.notifyDataSetChanged();
                    if (recyclerView!=null)
                        recyclerView.setAdapter(exp_ext_adapter);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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
    private void selectImage(){
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        if (intent.resolveActivity(getContextNullSafety().getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(getContextNullSafety(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    Cursor cursor = getContextNullSafety().getContentResolver()
                            .query(selectedImageUri, null, null, null, null);
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    cursor.moveToFirst();

                    String name = cursor.getString(nameIndex);
                    String size = Long.toString(cursor.getLong(sizeIndex));

                    dialog= new Dialog(getContext());
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_for_ext_exp);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    TextView cancel=dialog.findViewById(R.id.textView96);
                    TextView text=dialog.findViewById(R.id.textView94);
                    des=dialog.findViewById(R.id.field_goal);
                    TextView yes=dialog.findViewById(R.id.textView95);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                    cancel.setOnClickListener(vi-> dialog.dismiss());
                    yes.setOnClickListener(v->{
                        dialog1 = new Dialog(requireContext());
                        dialog1.setCancelable(false);
                        dialog1.setContentView(R.layout.loading_dialog);
                        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        LottieAnimationView lottieAnimationView = dialog1.findViewById(R.id.animate);
                        lottieAnimationView.setAnimation("uploaded.json");
                        dialog1.show();
                        send_doc(selectedImageUri);
                    });
                    file_name =name;
                    str_txt=name+"\n("+size+" bytes)";
                    text.setText(str_txt);
                }
            }
        }
    }

    private void send_doc(Uri selectedImageUri) {
        if (city!=null) {
            String push_key = expense_record.push().getKey();
            String pdfpath = "expense_record/" + user.getUid() + push_key;
            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(pdfpath);
            final StorageReference filepath = storageReference1.child(file_name);
            filepath.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot1 ->
                            taskSnapshot1.getStorage().getDownloadUrl().addOnCompleteListener(
                                    task1 -> {
                                        String pdf_link = Objects.requireNonNull(task1.getResult()).toString();
                                        assert push_key != null;
                                        Calendar cal = Calendar.getInstance();
                                        SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm a", Locale.getDefault());
                                        expense_record.child(user.getUid()).child(push_key).child("file_link").setValue(pdf_link);
                                        expense_record.child(user.getUid()).child(push_key).child("key").setValue(push_key);
                                        expense_record.child(user.getUid()).child(push_key).child("city").setValue(city+"");
                                        expense_record.child(user.getUid()).child(push_key).child("file_name").setValue(file_name);
                                        expense_record.child(user.getUid()).child(push_key).child("description").setValue(des.getText().toString() + "\nDate:" + simpleDateFormat_time.format(cal.getTime()));
                                        dialog1.dismiss();
                                        dialog.dismiss();
                                    }));
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Info",
                    "Please mention your city in your profile.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getContextNullSafety(),R.font.helvetica_regular));
        }
    }
}