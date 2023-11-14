package com.aryomtech.dhitifoundation.events;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.events.adapter.EventAdapterClass;
import com.aryomtech.dhitifoundation.website.d_Website;
import com.aryomtech.mylibrary3.RobotoCalendarView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import www.sanju.motiontoast.MotionToast;

public class Events extends Fragment implements RobotoCalendarView.RobotoCalendarListener,LocationListener {

    String identity="";
    DatabaseReference event;
    RobotoCalendarView robotoCalendarView;
    LottieAnimationView fab;
    String clicked_date="";
    RecyclerView recyclerView;
    ArrayList<Eventmodel> list;
    ImageView event_preview_image;
    LocationManager locationManager;
    private final  int REQUEST_CHECK_CODE=8989;
    TextView event_preview_text;
    boolean once=true;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView loc_text;
    WebView webView;
    Dialog dialog;
    String longitude="",latitude="";
    String store_current=null;
    EditText description;
    View view;
    private Context contextNullSafe;
    DatabaseReference att_ref;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_events, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.calendar_status));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.calendar_status));

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        webView = view.findViewById(R.id.webview);
        webView.setVisibility(View.GONE);
        new Handler(Looper.myLooper()).postDelayed(() -> {
            webView.loadUrl("https://www.dhitifoundation.com/our-events/");
            webView.setWebViewClient(new MyBrowser());

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(false);

            webView.setWebViewClient(new WebViewClient(){

                @Override
                public void onPageCommitVisible (WebView view,
                                                 String url){
                    event_preview_image.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    event_preview_text.setVisibility(View.GONE);
                }
            });
        },1000);

        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        event_preview_image=view.findViewById(R.id.event_preview_image);
        event_preview_text=view.findViewById(R.id.event_preview_text);

        robotoCalendarView = view.findViewById(R.id.roboto);
        list=new ArrayList<>();
        fab = view.findViewById(R.id.fab);
        if(identity.equals("chapter-head") || identity.equals("admin"))
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(v-> ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new Create_Event())
                .addToBackStack(null)
                .commit());
        // Button clearSelectedDayButton = findViewById(R.id.clearSelectedDayButton);

        att_ref=FirebaseDatabase.getInstance().getReference().child("attendance");

        Calendar current_date=Calendar.getInstance();
        int ch_current=0;
        int whiteend_current=0;
        String str_current=current_date.getTime()+"";
        String slice_current=str_current.substring(0,10);
        for(char c:str_current.toCharArray()){
            ch_current++;
            if(Character.isWhitespace(c)){
                whiteend_current++;
            }
            if(whiteend_current==5){
                String slice2=str_current.substring(ch_current);
                store_current=slice_current+" "+slice2;
                break;
            }
        }

        recyclerView= view.findViewById(R.id.cal_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        event= FirebaseDatabase.getInstance().getReference().child("Events");

        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot date_As_keys:snapshot.getChildren()){

                    String sub_1 = Objects.requireNonNull(date_As_keys.getKey()).substring(4, 7);
                    String database_year=date_As_keys.getKey().substring(11).trim();

                    Calendar calendar = Calendar.getInstance();
                    String sub_text = calendar.get(Calendar.MONTH)+"";
                    String current_year=calendar.get(Calendar.YEAR)+"";

                    String month="";
                    if(sub_text.equals("0")){
                        month="Jan";
                    }
                    if(sub_text.equals("1")){
                        month="Feb";
                    }
                    if(sub_text.equals("2")){
                        month="Mar";
                    }
                    if(sub_text.equals("3")){
                        month="Apr";
                    }
                    if(sub_text.equals("4")){
                        month="May";
                    }
                    if(sub_text.equals("5")){
                        month="Jun";
                    }
                    if(sub_text.equals("6")){
                        month="Jul";
                    }
                    if(sub_text.equals("7")){
                        month="Aug";
                    }
                    if(sub_text.equals("8")){
                        month="Sep";
                    }
                    if(sub_text.equals("9")){
                        month="Oct";
                    }
                    if(sub_text.equals("10")){
                        month="Nov";
                    }
                    if(sub_text.equals("11")){
                        month="Dec";
                    }

                    if (sub_1.equals(month) && current_year.trim().equals(database_year)) {
                        Random random = new Random(System.currentTimeMillis());
                        int style = random.nextInt(2);
                        String str = date_As_keys.getKey();
                        assert str != null;
                        String slice = str.substring(8, 10);
                        if (slice.toCharArray()[0] == '0') {
                            slice = slice.toCharArray()[1] + "";
                        }

                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));


                        switch (style) {
                            case 0:
                                robotoCalendarView.markCircleImage1(calendar.getTime());
                                break;
                            case 1:
                                robotoCalendarView.markCircleImage2(calendar.getTime());
                                break;
                            default:
                                break;
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(Events.this);

        robotoCalendarView.setShortWeekDays(false);

        robotoCalendarView.showDateTitle(true);

        robotoCalendarView.setDate(new Date());

        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,new MainFragment())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        initMiscellaneous();
        return view;
    }
    public void initMiscellaneous(){
        final ConstraintLayout layoutMiscellaneous = view.findViewById(R.id.bottom_sheet_attandance);
        final BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
            else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        loc_text=layoutMiscellaneous.findViewById(R.id.text_loc);
        description=layoutMiscellaneous.findViewById(R.id.field_goal);

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            openGPs();
            dialog = new Dialog(requireContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.locating_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            getLocation();
        });

        layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (!loc_text.getText().toString().equals("Tap to get location")) {
                String pushkey = att_ref.push().getKey();
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("uid").setValue(user.getUid());
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("location").setValue(loc_text.getText().toString().trim());
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("latitude").setValue(latitude);
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("longitude").setValue(longitude);
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("reason").setValue(description.getText().toString().trim());
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat_time = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
                att_ref.child(store_current).child(Objects.requireNonNull(pushkey)).child("time").setValue(simpleDateFormat_time.format(cal.getTime()) + "");
                description.setText("");
                loc_text.setText(R.string.tap_to_get_location);
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Success",
                        "Attendance submitted.",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
            }
        });


    }// ends layoutMiscellaneous
    @Override
    public void onDayClick(Date date) {
        int ch=0;
        String store = null;
        int whiteend=0;
        String str=date+"";
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

        clicked_date=store;

        once=true;
        list.clear();
        String finalStore = store;
        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(finalStore).exists() && once) {
                    event_preview_image.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    event_preview_text.setVisibility(View.GONE);
                    for (DataSnapshot ds : snapshot.child(finalStore).getChildren()) {
                        list.add(ds.getValue(Eventmodel.class));

                        EventAdapterClass eventAdapterClass = new EventAdapterClass(list, getContext(),finalStore,identity);
                        try {
                            recyclerView.setAdapter(eventAdapterClass);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    once =false;
                }
                else{
                    event_preview_image.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    event_preview_text.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager)getContextNullSafety(). getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,this);

        }catch (Exception e){
            e.printStackTrace();
        }

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
    @Override
    public void onDayLongClick(Date date) {
        if(identity.equals("chapter-head") || identity.equals("admin")) {
            int ch = 0;
            String store = "";
            int whiteend = 0;
            String str = date + "";
            String slice = str.substring(0, 10);
            for (char c : str.toCharArray()) {
                ch++;
                if (Character.isWhitespace(c)) {
                    whiteend++;
                }
                if (whiteend == 5) {
                    String slice2 = str.substring(ch);
                    store = slice + " " + slice2;
                    break;
                }
            }

            //TODO:Admin opening;
            View_attendance view_attendance = new View_attendance();
            Bundle args = new Bundle();
            args.putString("date_key_25091210", store);
            view_attendance.setArguments(args);

            ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, view_attendance)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRightButtonClick() {
        TextView textView=robotoCalendarView.findViewById(R.id.monthText);

        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot date_As_keys:snapshot.getChildren()){

                    String sub_1 = Objects.requireNonNull(date_As_keys.getKey()).substring(4, 7);
                    String database_year=date_As_keys.getKey().substring(11).trim();
                    String[] text_year =textView.getText().toString().split(" ");
                    Calendar calendar = Calendar.getInstance();
                    String extracted_year;
                    if (text_year.length==2)
                        extracted_year = text_year[1];
                    else
                        extracted_year = calendar.get(Calendar.YEAR)+"";


                    String sub_text = textView.getText().toString().substring(0,3);
                    if (sub_1.equals(sub_text) && extracted_year.equals(database_year)) {

                        String str = date_As_keys.getKey();
                        assert str != null;
                        String slice = str.substring(8, 10);
                        String slice_year = str.substring(11);
                        if (slice.toCharArray()[0] == '0') {
                            slice = slice.toCharArray()[1] + "";
                        }

                        if(sub_text.equals("Jan")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,0);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Feb")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,1);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Mar")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,2);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Apr")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,3);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("May")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,4);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Jun")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,5);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Jul")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,6);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Aug")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,7);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Sep")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,8);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Oct")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,9);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Nov")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,10);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Dec")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,11);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        robotoCalendarView.markCircleImage2(calendar.getTime());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onLeftButtonClick() {
        TextView textView=robotoCalendarView.findViewById(R.id.monthText);

        event.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot date_As_keys:snapshot.getChildren()){

                    String sub_1 = Objects.requireNonNull(date_As_keys.getKey()).substring(4, 7);
                    String database_year=date_As_keys.getKey().substring(11).trim();
                    Calendar calendar = Calendar.getInstance();
                    String[] text_year =textView.getText().toString().split(" ");
                    String extracted_year;
                    if (text_year.length==2)
                        extracted_year = text_year[1];
                    else
                        extracted_year = calendar.get(Calendar.YEAR)+"";

                    String sub_text = textView.getText().toString().substring(0,3);

                    if (sub_1.equals(sub_text) && extracted_year.equals(database_year)) {

                        String str = date_As_keys.getKey();
                        assert str != null;
                        String slice = str.substring(8, 10);
                        String slice_year = str.substring(11);
                        if (slice.toCharArray()[0] == '0') {
                            slice = slice.toCharArray()[1] + "";
                        }

                        if(sub_text.equals("Jan")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,0);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Feb")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,1);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Mar")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,2);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Apr")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,3);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("May")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,4);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Jun")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,5);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Jul")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,6);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Aug")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,7);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Sep")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,8);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Oct")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,9);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Nov")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,10);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        if(sub_text.equals("Dec")){
                            calendar.set(Calendar.YEAR,Integer.parseInt(slice_year));
                            calendar.set(Calendar.MONTH,11);
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(slice));
                        }
                        robotoCalendarView.markCircleImage2(calendar.getTime());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            longitude=location.getLongitude()+"";
            latitude=location.getLatitude()+"";
            loc_text.setText(address);
            dialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    private void openGPs(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        LocationRequest request= LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result=
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()){
                    case LocationSettingsStatusCodes
                            .RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException= (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(getActivity(),REQUEST_CHECK_CODE);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }catch (ClassCastException ignored){

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    {
                        break;
                    }
                }
            }
        });
    }
    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}