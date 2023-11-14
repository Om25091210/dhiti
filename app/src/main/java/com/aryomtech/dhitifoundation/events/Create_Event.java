package com.aryomtech.dhitifoundation.events;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.topic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import www.sanju.motiontoast.MotionToast;


public class Create_Event extends Fragment {

    View view;
    private TextView from,to;
    int from_or_to=-1;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    DatabaseReference event,Notifications_ref;
    EditText title,location,message;
    String selectedImagePath;
    Dialog dialog;
    String imagepath;
    int downspeed;
    CheckBox checkBox;
    int upspeed;
    ImageView image;
    final String OLD_FORMAT = "dd-MM-yyyy";
    String filelink;
    FirebaseUser user;
    String push = "";
    String DeviceToken;
    Bitmap imageBitmap;
    private Context contextNullSafe;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 101;
    public static final int REQUEST_CODE_SELECT_IMAGE = 102;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_create__event, container, false);
        checkBox=view.findViewById(R.id.checkBox);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.calendar_status));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.calendar_status));

        Notifications_ref= FirebaseDatabase.getInstance().getReference().child("Notifications");

        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        user= FirebaseAuth.getInstance().getCurrentUser();
        view.findViewById(R.id.imageBack).setOnClickListener(v->onback());
        view.findViewById(R.id.imageSave).setOnClickListener(v-> {
            try {
                save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.attach).setOnClickListener(v->{
            //Ask for permission
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
            else{
                selectImage();
            }
        });

        event= FirebaseDatabase.getInstance().getReference().child("Events");

        getting_device_token();
        title=view.findViewById(R.id.editTextTextMultiLine9);
        location=view.findViewById(R.id.location);
        message=view.findViewById(R.id.inputNote);
        image=view.findViewById(R.id.image);

        from=view.findViewById(R.id.from);
        from.setOnClickListener(v->{

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year,month,day);
            from_or_to=0;
            dialog.show();
        });

        to=view.findViewById(R.id.to);
        to.setOnClickListener(v->{

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    mDateSetListener,
                    year,month,day);

            from_or_to=1;
            dialog.show();

        });

        view.findViewById(R.id.imageremove).setOnClickListener(v -> {
            image.setImageBitmap(null);
            image.setVisibility(View.GONE);
            view.findViewById(R.id.imageremove).setVisibility(View.GONE);
        });


        mDateSetListener = (datePicker, year, month, day) -> {

            String d=String.valueOf(day);
            String m=String.valueOf(month+1);
            Log.e("month",m+"");
            month = month + 1;
            Log.e("month",month+"");
            if(String.valueOf(day).length()==1)
                d="0"+ day;
            if(String.valueOf(month).length()==1)
                m="0"+ month;
            String date = year + "-" + m + "-" + d;
            if(from_or_to==0)
                from.setText(date);
            else if(from_or_to==1)
                to.setText(date);

        };
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    private void onback() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }

        ft.commit();
    }

    private void getting_device_token() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getContextNullSafety().getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if(nc!=null) {
            downspeed = nc.getLinkDownstreamBandwidthKbps()/1000;
            upspeed = nc.getLinkUpstreamBandwidthKbps()/1000;
        }else{
            downspeed=0;
            upspeed=0;
        }
        Log.e("downspeed_check",downspeed+"");
        Log.e("upspeed_check",upspeed+"");
        Log.e("wifispeed_check",getWifiLevel()+"");
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
        WifiManager wifiManager = (WifiManager) getContextNullSafety().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int linkSpeed = wifiManager.getConnectionInfo().getRssi();
        return WifiManager.calculateSignalLevel(linkSpeed, 5);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
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
                    try {
                        addImageNote(selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContextNullSafety().getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        }
        else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }
    private void addImageNote(Uri imageUri) throws IOException {

        try {
            InputStream inputStream = getContextNullSafety().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            image.setImageBitmap(bitmap);
            image.setVisibility(View.VISIBLE);
            selectedImagePath = getPathFromUri(imageUri);
            imageBitmap = SiliCompressor.with(getContext()).getCompressBitmap(String.valueOf(imageUri));
            view.findViewById(R.id.imageremove).setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void save() throws ParseException {

        if(!title.getText().toString().trim().equals("")){
            if(!location.getText().toString().trim().equals("")){

                if(!from.getText().toString().trim().equals("Tap to select")) {

                    String date=new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
                    imagepath = "Events/" + date+title.getText().toString().trim() + ".png";

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(imagepath);
                    final String randomKey = UUID.randomUUID().toString();
                    String path = MediaStore.Images.Media.insertImage(getContextNullSafety().getApplicationContext().getContentResolver(), imageBitmap, "" + randomKey, null);

                    String s = from.getText().toString();
                    String start=s.substring(8)+"-"+s.substring(5,7)+"-"+s.substring(0,4);

                    String e = from.getText().toString();
                    if (!to.getText().toString().trim().equals("Tap to select"))
                        e=to.getText().toString();


                    SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, Locale.getDefault());
                    Date todaysDate = sdf.parse(start);

                    String end=e.substring(8)+"-"+e.substring(5,7)+"-"+e.substring(0,4);
                    SimpleDateFormat sdf1 = new SimpleDateFormat(OLD_FORMAT, Locale.getDefault());
                    Date anotherDate = sdf1.parse(end);

                    List<String> Dates = getDaysBetweenDates(todaysDate, anotherDate);

                    if (path != null) {

                        dialog = new Dialog(getContext());
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.loading_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                        lottieAnimationView.setAnimation("done.json");
                        dialog.show();

                        String noti_pushkey=Notifications_ref.push().getKey();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        cal.add(Calendar.DAY_OF_MONTH,2);

                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("title").setValue("New Event");
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("delete_on").setValue(simpleDateFormat.format(cal.getTime())+"");
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("creator").setValue(user.getUid());
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("body").setValue(title.getText().toString().trim());

                        topic topic=new topic();
                        topic.noti("New Event",title.getText().toString().trim());

                        storageReference.putFile(Uri.parse(path))
                                .addOnSuccessListener(taskSnapshot ->
                                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                                task ->{

                                                    filelink = Objects.requireNonNull(task.getResult()).toString();
                                                    if (Dates.size() > 1) {
                                                        for (int k = 0; k < Dates.size(); k++) {

                                                            push= event.push().getKey();
                                                            assert push != null;
                                                            event.child(Dates.get(k)).child(push).child("title").setValue(title.getText().toString().trim());
                                                            event.child(Dates.get(k)).child(push).child("createdon").setValue(date);
                                                            if(checkBox.isChecked())
                                                                event.child(Dates.get(k)).child(push).child("state").setValue("private");
                                                            event.child(Dates.get(k)).child(push).child("creator").setValue(user.getUid());
                                                            event.child(Dates.get(k)).child(push).child("location").setValue(location.getText().toString().trim());
                                                            event.child(Dates.get(k)).child(push).child("key").setValue(push);
                                                            if (!message.getText().toString().trim().equals(""))
                                                                event.child(Dates.get(k)).child(push).child("message").setValue(message.getText().toString().trim());

                                                            event.child(Dates.get(k)).child(push).child("image").setValue(filelink);

                                                        }

                                                    }
                                                    else {

                                                        push = event.push().getKey();
                                                        assert push != null;
                                                        event.child(Dates.get(0)).child(push).child("title").setValue(title.getText().toString().trim());
                                                        event.child(Dates.get(0)).child(push).child("createdon").setValue(date);
                                                        if(checkBox.isChecked())
                                                            event.child(Dates.get(0)).child(push).child("state").setValue("private");
                                                        event.child(Dates.get(0)).child(push).child("creator").setValue(user.getUid());
                                                        event.child(Dates.get(0)).child(push).child("location").setValue(location.getText().toString().trim());
                                                        event.child(Dates.get(0)).child(push).child("key").setValue(push);
                                                        if (!message.getText().toString().trim().equals(""))
                                                            event.child(Dates.get(0)).child(push).child("message").setValue(message.getText().toString().trim());


                                                        event.child(Dates.get(0)).child(push).child("image").setValue(filelink);

                                                    }

                                                    MotionToast.Companion.darkColorToast(getActivity(),
                                                            "Successfull",
                                                            "Created successfully check events.",
                                                            MotionToast.TOAST_SUCCESS,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));

                                                    long delayInMillis = 3000;
                                                    Timer timer = new Timer();
                                                    timer.schedule(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            dialog.dismiss();
                                                            onback();
                                                        }
                                                    }, delayInMillis);
                                                }));

                    }
                    else{

                        dialog = new Dialog(getContext());
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.loading_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animate);
                        lottieAnimationView.setAnimation("done.json");
                        dialog.show();

                        String noti_pushkey=Notifications_ref.push().getKey();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        cal.add(Calendar.DAY_OF_MONTH,2);

                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("title").setValue("New Event");
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("creator").setValue(user.getUid());
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("delete_on").setValue(simpleDateFormat.format(cal.getTime())+"");
                        Notifications_ref.child(Objects.requireNonNull(noti_pushkey)).child("body").setValue(title.getText().toString());

                        topic topic=new topic();
                        topic.noti("New Event",title.getText().toString());

                        if (Dates.size() > 1) {

                            for (int k = 0; k < Dates.size(); k++) {

                                String push = event.push().getKey();
                                assert push != null;
                                event.child(Dates.get(k)).child(push).child("title").setValue(title.getText().toString().trim());
                                event.child(Dates.get(k)).child(push).child("createdon").setValue(date);
                                if(checkBox.isChecked())
                                    event.child(Dates.get(k)).child(push).child("state").setValue("private");
                                event.child(Dates.get(k)).child(push).child("creator").setValue(user.getUid());
                                event.child(Dates.get(k)).child(push).child("location").setValue(location.getText().toString().trim());
                                event.child(Dates.get(k)).child(push).child("key").setValue(push);
                                if (!message.getText().toString().trim().equals(""))
                                    event.child(Dates.get(k)).child(push).child("message").setValue(message.getText().toString().trim());

                            }

                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Successfull",
                                    "Created successfully check events.",
                                    MotionToast.TOAST_SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));

                            long delayInMillis = 3000;
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    onback();
                                }
                            }, delayInMillis);
                        }
                        else {

                            String push = event.push().getKey();
                            assert push != null;
                            event.child(Dates.get(0)).child(push).child("title").setValue(title.getText().toString().trim());
                            event.child(Dates.get(0)).child(push).child("createdon").setValue(date);
                            if(checkBox.isChecked())
                                event.child(Dates.get(0)).child(push).child("state").setValue("private");
                            event.child(Dates.get(0)).child(push).child("creator").setValue(user.getUid());
                            event.child(Dates.get(0)).child(push).child("location").setValue(location.getText().toString().trim());
                            event.child(Dates.get(0)).child(push).child("key").setValue(push);
                            if (!message.getText().toString().trim().equals(""))
                                event.child(Dates.get(0)).child(push).child("message").setValue(message.getText().toString().trim());

                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Successfull",
                                    "Created successfully check events.",
                                    MotionToast.TOAST_SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));


                            long delayInMillis = 3000;
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    onback();
                                }
                            }, delayInMillis);
                        }

                    }

                }
                else{
                    MotionToast.Companion.darkColorToast(getActivity(),
                            "Missing",
                            "Start Date is required.",
                            MotionToast.TOAST_INFO,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                }
            }
            else{
                MotionToast.Companion.darkColorToast(getActivity(),
                        "Missing",
                        "Location is required.",
                        MotionToast.TOAST_INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
            }
        }
        else{
            MotionToast.Companion.darkColorToast(getActivity(),
                    "Missing",
                    "Title is required.",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
        }
    }

    public static List<String> getDaysBetweenDates(Date startdate, Date enddate)
    {
        List<String> date = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().compareTo(enddate)==0)
        {
            Date result = calendar.getTime();

            int ch=0;
            String store = null;
            int whiteend=0;
            String str=result+"";
            String slice=str.substring(0,10);
            for(char c:str.toCharArray()){
                ch++;
                if(Character.isWhitespace(c)){
                    whiteend++;
                }
                if(whiteend==5){
                    String slice2=str.substring(ch);
                    store=slice+" "+slice2;
                    break;
                }
            }

            date.add(store);

            calendar.add(Calendar.DATE, 1);
        }

        return date;
    }
}