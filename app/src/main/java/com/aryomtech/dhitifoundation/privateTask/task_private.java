package com.aryomtech.dhitifoundation.privateTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.privateTask.Adapter.privatetaskAdapter;
import com.aryomtech.dhitifoundation.privateTask.Model.privateTaskData;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;


public class task_private extends Fragment {

    View view;
    private Context contextNullSafe;
    TextView badge_tag,previous,next,date;
    ImageView previous_img,next_img;
    FirebaseAuth auth;
    ArrayList<privateTaskData> privateTaskData_list;
    int counter=0;
    RecyclerView recyclerView;
    ArrayList<String> keys_task;
    CoordinatorLayout constraintLayout;
    DatabaseReference user_ref;
    ImageView profile_image,badge_image;
    FirebaseUser user;
    String current_week_last_date="";
    String dp="";
    ArrayList<Long> list_tasks;
    ArrayList<String> mon,tue,wed,thru,fri,sat,sun;
    TextView xp_msg,textview_xp,name,current_level,current_xp,current_level_plus_plus,next_xp,remaining_xp,green_level;
    ProgressBar progressBar;
    ImageView back_img;
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data.
    BarData barData;
    // variable for our bar data set.
    BarDataSet barDataSet;
    // array list for storing entries.
    ArrayList barEntriesArrayList;
    ProgressBar progressBar_rec;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_task_private, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        progressBar_rec=view.findViewById(R.id.progressBar2);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        keys_task=new ArrayList<>();
        mon=new ArrayList<>();
        tue=new ArrayList<>();
        wed=new ArrayList<>();
        thru=new ArrayList<>();
        fri=new ArrayList<>();
        sat=new ArrayList<>();
        sun=new ArrayList<>();
        privateTaskData_list=new ArrayList<>();
        list_tasks=new ArrayList<>();

        user_ref= FirebaseDatabase.getInstance().getReference().child("users");

        name=view.findViewById(R.id.textView37);
        profile_image=view.findViewById(R.id.profile_image);
        progressBar=view.findViewById(R.id.progressBar);
        back_img=view.findViewById(R.id.imageBack);
        previous=view.findViewById(R.id.textView67);
        next=view.findViewById(R.id.textView69);
        previous_img=view.findViewById(R.id.previous);
        next_img=view.findViewById(R.id.next);
        constraintLayout=view.findViewById(R.id.constraint);
        date=view.findViewById(R.id.date);
        recyclerView=view.findViewById(R.id.task_private_rv);
        current_level=view.findViewById(R.id.current_level);
        current_level_plus_plus=view.findViewById(R.id.current_level_plus_plus);
        current_xp=view.findViewById(R.id.current_xp);
        next_xp=view.findViewById(R.id.next_xp);
        remaining_xp=view.findViewById(R.id.remaining_xp);
        green_level=view.findViewById(R.id.textView64);
        textview_xp=view.findViewById(R.id.textView68);
        xp_msg=view.findViewById(R.id.xp_msg);
        badge_image=view.findViewById(R.id.badge_image);
        badge_tag=view.findViewById(R.id.textView47);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        date.setText(new SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        try {
            getWeeksRangesDates(calendar.get(Calendar.WEEK_OF_YEAR));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        current_week_last_date=next.getText().toString();
        previous_img.setOnClickListener(v->{
            counter--;
            try {
                getWeeksRangesDates(calendar.get(Calendar.WEEK_OF_YEAR)-Math.abs(counter));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        next_img.setOnClickListener(v->{
            if(!current_week_last_date.equals(next.getText().toString())) {
                counter++;
                try {
                    getWeeksRangesDates( calendar.get(Calendar.WEEK_OF_YEAR) - Math.abs(counter));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else{
                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "End of week.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        user_ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Long xp=snapshot.child("progress").child("xp").getValue(Long.class);
                if(xp!=null) {
                    if (xp >= 100) {
                        current_level.setVisibility(View.VISIBLE);
                        green_level.setVisibility(View.VISIBLE);
                        next_xp.setVisibility(View.VISIBLE);
                        current_xp.setVisibility(View.VISIBLE);
                        remaining_xp.setVisibility(View.VISIBLE);
                        current_level_plus_plus.setVisibility(View.VISIBLE);
                        textview_xp.setVisibility(View.VISIBLE);
                        xp_msg.setVisibility(View.VISIBLE);

                        String xp_str=getDecimalPlaces(xp/100f);
                        String xp_level=getLevel(xp/100f);
                        getBadge(xp_level);
                        current_level.setText(xp_level);
                        String green_text="Lvl."+xp_level;
                        green_level.setText(green_text);
                        String next_level=Integer.parseInt(xp_level)+1+"";
                        current_level_plus_plus.setText(next_level);
                        String current_xp_str="  "+xp;
                        current_xp.setText(current_xp_str);
                        String next_xp_str="/"+(Integer.parseInt(xp_level)+1)*100;
                        next_xp.setText(next_xp_str);
                        String remaining_xp_str=((Integer.parseInt(xp_level)+1)* 100L)-xp+"";
                        remaining_xp.setText(remaining_xp_str);
                        if(xp_str.length()==1)
                            xp_str=xp_str+"0";
                        float xp_float=Float.parseFloat(xp_str);
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, xp_float);
                        anim.setDuration(1000);
                        progressBar.startAnimation(anim);
                    }
                    else if(xp>0){
                        current_level.setVisibility(View.VISIBLE);
                        green_level.setVisibility(View.VISIBLE);
                        next_xp.setVisibility(View.VISIBLE);
                        current_xp.setVisibility(View.VISIBLE);
                        remaining_xp.setVisibility(View.VISIBLE);
                        current_level_plus_plus.setVisibility(View.VISIBLE);
                        textview_xp.setVisibility(View.VISIBLE);
                        xp_msg.setVisibility(View.VISIBLE);

                        badge_image.setImageResource(R.drawable.ic_badge_initial);
                        badge_tag.setText(R.string.philanthropic);

                        String xp_level=getLevel(xp/100f);
                        current_level.setText(xp_level);
                        String green_text="Lvl."+xp_level;
                        green_level.setText(green_text);
                        String next_level=Integer.parseInt(xp_level)+1+"";
                        current_level_plus_plus.setText(next_level);
                        String current_xp_str="  "+xp;
                        current_xp.setText(current_xp_str);
                        String next_xp_str="/"+(Integer.parseInt(xp_level)+1)*100;
                        next_xp.setText(next_xp_str);
                        String remaining_xp_str=((Integer.parseInt(xp_level)+1)*100L)-xp+"";
                        remaining_xp.setText(remaining_xp_str);
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, Float.parseFloat(xp+""));
                        anim.setDuration(1000);
                        progressBar.startAnimation(anim);
                    }
                    else{
                        ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0f, Float.parseFloat(0+""));
                        anim.setDuration(1000);
                        progressBar.startAnimation(anim);
                        current_level.setVisibility(View.GONE);
                        green_level.setVisibility(View.GONE);
                        next_xp.setVisibility(View.GONE);
                        current_xp.setVisibility(View.GONE);
                        remaining_xp.setVisibility(View.GONE);
                        current_level_plus_plus.setVisibility(View.GONE);
                        textview_xp.setVisibility(View.GONE);
                        xp_msg.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // initializing variable for bar chart.
        barChart = view.findViewById(R.id.bar_chart);
        // calling method to get bar entries.
        getBarEntries();
        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList,"");
        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);
        // below line is to set data
        // to our bar chart.
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setData(barData);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        // setting text color.
        barDataSet.setColors(Color.parseColor("#827CF0"));
        barDataSet.setValueTextColor(Color.TRANSPARENT);
        // setting text size
        barDataSet.setValueTextSize(16f);
        barData.setBarWidth(0.2f);
        barChart.getDescription().setEnabled(false);
        CustomBarChartRender barChartRender = new CustomBarChartRender(barChart,barChart.getAnimator(), barChart.getViewPortHandler());
        barChartRender.setRadius(20);
        barChart.setRenderer(barChartRender);
        YAxis yl = barChart.getAxisLeft();
        yl.setAxisMinimum(0f);
        yl.setAxisMaximum(30f);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        xAxisLabel.add("S");
        xAxisLabel.add("M");
        xAxisLabel.add("T");
        xAxisLabel.add("W");
        xAxisLabel.add("T");
        xAxisLabel.add("F");
        xAxisLabel.add("S");


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        ValueFormatter formatter = new ValueFormatter() {


            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setLabelCount(8);
        xAxis.setValueFormatter(formatter);

        user_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name_str=snapshot.child("name").getValue(String.class);
                if(name_str!=null) {
                    name_str = "Hello, " + name_str;
                    name.setText(name_str);
                }
                if(snapshot.child("dplink").exists()){
                    dp=snapshot.child("dplink").getValue(String.class);
                    try{
                        Glide.with(getContextNullSafety()).asBitmap()
                                .load(dp)
                                .thumbnail(0.1f)
                                .override(100,100)
                                .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                                .into(profile_image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        back_img.setOnClickListener(v->back());
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
        getTasks();
        return view;
    }

    private void getBadge(String xp_level) {
        switch (xp_level) {
            case "1":
                badge_image.setImageResource(R.drawable.ic_badge_one);
                badge_tag.setText(R.string.emerging);
                break;
            case "2":
                badge_image.setImageResource(R.drawable.ic_badge_two);
                badge_tag.setText(R.string.eagre);
                break;
            case "3":
                badge_image.setImageResource(R.drawable.ic_badge_three);
                badge_tag.setText(R.string.ecofreak);
                break;
            case "4":
                badge_image.setImageResource(R.drawable.ic_badge_four);
                badge_tag.setText(R.string.expeditious);
                break;
            case "5":
                badge_image.setImageResource(R.drawable.ic_badge_five);
                badge_tag.setText(R.string.efficacy);
                break;
            case "6":
                badge_image.setImageResource(R.drawable.ic_badge_six);
                badge_tag.setText(R.string.expedite);
                break;
            case "7":
                badge_image.setImageResource(R.drawable.ic_badge_seven);
                badge_tag.setText(R.string.benevolent);
                break;
            case "8":
                badge_image.setImageResource(R.drawable.ic_badge_eight);
                badge_tag.setText(R.string.fiery);
                break;
            case "9":
                badge_image.setImageResource(R.drawable.ic_badge_nine);
                badge_tag.setText(R.string.gladiator);
                break;
            case "10":
                badge_image.setImageResource(R.drawable.ic_badge_ten);
                badge_tag.setText(R.string.trailblazer);
                break;
            case "11":
                badge_image.setImageResource(R.drawable.ic_badge_eleven);
                badge_tag.setText(R.string.sergeant);
                break;
            case "12":
                badge_image.setImageResource(R.drawable.ic_badge_twelve);
                badge_tag.setText(R.string.lieutenant);
                break;
            case "13":
                badge_image.setImageResource(R.drawable.ic_badge_thirteen);
                badge_tag.setText(R.string.captain);
                break;
            case "14":
                badge_image.setImageResource(R.drawable.ic_badge_fourteen);
                badge_tag.setText(R.string.major);
                break;
            case "15":
                badge_image.setImageResource(R.drawable.ic_badge_fifteen);
                badge_tag.setText(R.string.lieutenantcolonel);
                break;
            case "16":
                badge_image.setImageResource(R.drawable.ic_badge_sixteen);
                badge_tag.setText(R.string.colonel);
                break;
            case "17":
                badge_image.setImageResource(R.drawable.ic_badge_seventeen);
                badge_tag.setText(R.string.brigadiergeneral);
                break;
            case "18":
                badge_image.setImageResource(R.drawable.ic_badge_eighteen);
                badge_tag.setText(R.string.majorgeneral);
                break;
            case "19":
                badge_image.setImageResource(R.drawable.ic_badge_nineteen);
                badge_tag.setText(R.string.lieutenantgeneral);
                break;
            case "20":
                badge_image.setImageResource(R.drawable.ic_badge_twenty);
                badge_tag.setText(R.string.general);
                break;
            case "21":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_one);
                badge_tag.setText(R.string.honor1);
                break;
            case "22":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_two);
                badge_tag.setText(R.string.honor2);
                break;
            case "23":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_three);
                badge_tag.setText(R.string.honor3);
                break;
            case "24":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_four);
                badge_tag.setText(R.string.honor4);
                break;
            case "25":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_five);
                badge_tag.setText(R.string.honor5);
                break;
            case "26":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_six);
                badge_tag.setText(R.string.honor6);
                break;
            case "27":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_seven);
                badge_tag.setText(R.string.honor7);
                break;
            case "28":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_eight);
                badge_tag.setText(R.string.honor8);
                break;
            case "29":
                badge_image.setImageResource(R.drawable.ic_badge_twenty_nine);
                badge_tag.setText(R.string.honor9);
                break;
            case "30":
                badge_image.setImageResource(R.drawable.ic_badge_thirty);
                badge_tag.setText(R.string.honor10);
                break;
            case "31":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_one);
                badge_tag.setText(R.string.ganin);
                break;
            case "32":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_two);
                badge_tag.setText(R.string.zetsunin);
                break;
            case "33":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_three);
                badge_tag.setText(R.string.gounin);
                break;
            case "34":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_four);
                badge_tag.setText(R.string.kyonin);
                break;
            case "35":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_five);
                badge_tag.setText(R.string.kagenin);
                break;
            case "36":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_six);
                badge_tag.setText(R.string.sennin);
                break;
            case "37":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_seven);
                badge_tag.setText(R.string.manjinin);
                break;
            case "38":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_eight);
                badge_tag.setText(R.string.oboronin);
                break;
            case "39":
                badge_image.setImageResource(R.drawable.ic_badge_thirty_nine);
                badge_tag.setText(R.string.hinin);
                break;
            case "40":
                badge_image.setImageResource(R.drawable.ic_badge_fourty);
                badge_tag.setText(R.string.kagura);
                break;
            case "41":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_one);
                badge_tag.setText(R.string.falcon1);
                break;
            case "42":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_two);
                badge_tag.setText(R.string.falcon2);
                break;
            case "43":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_three);
                badge_tag.setText(R.string.falcon3);
                break;
            case "44":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_four);
                badge_tag.setText(R.string.falcon4);
                break;
            case "45":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_five);
                badge_tag.setText(R.string.falcon5);
                break;
            case "46":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_six);
                badge_tag.setText(R.string.falcon6);
                break;
            case "47":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_seven);
                badge_tag.setText(R.string.falcon7);
                break;
            case "48":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_eight);
                badge_tag.setText(R.string.falcon8);
                break;
            case "49":
                badge_image.setImageResource(R.drawable.ic_badge_fourty_nine);
                badge_tag.setText(R.string.falcon9);
                break;
            case "50":
                badge_image.setImageResource(R.drawable.ic_badge_fifty);
                badge_tag.setText(R.string.greyhound);
                break;
            case "51":
                badge_image.setImageResource(R.drawable.ic_badge_fifty_three);
                badge_tag.setText(R.string.vanquish);
                break;
            case "52":
                badge_image.setImageResource(R.drawable.ic_badge_fifty_two);
                badge_tag.setText(R.string.mortal);
                break;
            case "53":
                badge_image.setImageResource(R.drawable.ic_badge_fifty_one);
                badge_tag.setText(R.string.hawkeye);
                break;
        }

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
    private void getTasks() {
        user_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                privateTaskData_list.clear();
                list_tasks.clear();
                if(snapshot.child("task").exists()){
                    progressBar_rec.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds:snapshot.child("task").getChildren()){
                        if(!Objects.equals(ds.getKey(), "added_task")){
                            privateTaskData_list.add(ds.getValue(privateTaskData.class));
                        }
                        if(snapshot.child("task").child(ds.getKey()).child("added_task").exists())
                            list_tasks.add(snapshot.child("task").child(ds.getKey()).child("added_task").getChildrenCount());
                        else
                            list_tasks.add(0L);

                    }
                    Collections.reverse(list_tasks);
                    Collections.reverse(privateTaskData_list);
                    privatetaskAdapter privatetaskAdapter=new privatetaskAdapter(getContext(),privateTaskData_list,list_tasks);
                    privatetaskAdapter.notifyDataSetChanged();
                    try {
                        if (recyclerView != null)
                            recyclerView.setAdapter(privatetaskAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else{
                    progressBar_rec.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void back() {
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
    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, 0));
        barEntriesArrayList.add(new BarEntry(2f, 0));
        barEntriesArrayList.add(new BarEntry(3f, 0));
        barEntriesArrayList.add(new BarEntry(4f, 0));
        barEntriesArrayList.add(new BarEntry(5f, 0));
        barEntriesArrayList.add(new BarEntry(6f, 0));
        barEntriesArrayList.add(new BarEntry(7f, 0));
    }
    public void getWeeksRangesDates(int w) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.WEEK_OF_YEAR,w);
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date todaysDate = formatter1.parse(formatter1.format(cal.getTime()));
        previous.setText(formatter1.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date anotherDate = formatter1.parse(formatter1.format(cal.getTime()));
        next.setText(formatter1.format(cal.getTime()));

        ArrayList<String> Dates = getDaysBetweenDates(todaysDate, anotherDate);
        Log.e("datesss",Dates+"");
        mon.clear();tue.clear();wed.clear();thru.clear();fri.clear();sat.clear();sun.clear();

        user_ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child("task").exists()){
                    for (DataSnapshot ds:snapshot.child("task").getChildren()){
                        String given_on=snapshot.child("task")
                                .child(Objects.requireNonNull(ds.getKey())).child("given_on").getValue(String.class);
                        if(given_on!=null) {
                            if (Dates.size() > 0) {
                                if (Dates.get(0).equals(given_on) && !sun.contains(ds.getKey()))
                                    sun.add(ds.getKey());
                                if (Dates.get(1).equals(given_on) && !mon.contains(ds.getKey()))
                                    mon.add(ds.getKey());
                                if (Dates.get(2).equals(given_on) && !tue.contains(ds.getKey()))
                                    tue.add(ds.getKey());
                                if (Dates.get(3).equals(given_on) && !wed.contains(ds.getKey()))
                                    wed.add(ds.getKey());
                                if (Dates.get(4).equals(given_on) && !thru.contains(ds.getKey()))
                                    thru.add(ds.getKey());
                                if (Dates.get(5).equals(given_on) && !fri.contains(ds.getKey()))
                                    fri.add(ds.getKey());
                                if (Dates.get(6).equals(given_on) && !sat.contains(ds.getKey()))
                                    sat.add(ds.getKey());
                            }
                        }
                    }
                     setting_barchart();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void setting_barchart() {
        barEntriesArrayList = new ArrayList<>();
        barEntriesArrayList.clear();
        barEntriesArrayList.add(new BarEntry(1f, sun.size()*10));
        barEntriesArrayList.add(new BarEntry(2f, mon.size()*10));
        barEntriesArrayList.add(new BarEntry(3f, tue.size()*10));
        barEntriesArrayList.add(new BarEntry(4f, wed.size()*10));
        barEntriesArrayList.add(new BarEntry(5f, thru.size()*10));
        barEntriesArrayList.add(new BarEntry(6f, fri.size()*10));
        barEntriesArrayList.add(new BarEntry(7f, sat.size()*10));

        barDataSet = new BarDataSet(barEntriesArrayList,"");
        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);
        // below line is to set data
        // to our bar chart.
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setData(barData);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(500);
        // setting text color.
        barDataSet.setColors(Color.parseColor("#827CF0"));
        barDataSet.setValueTextColor(Color.TRANSPARENT);
        // setting text size
        barDataSet.setValueTextSize(16f);
        barData.setBarWidth(0.2f);
        barChart.getDescription().setEnabled(false);
        YAxis yl = barChart.getAxisLeft();
        yl.setAxisMinimum(0f);
        yl.setAxisMaximum(60f);

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        xAxisLabel.add("S");
        xAxisLabel.add("M");
        xAxisLabel.add("T");
        xAxisLabel.add("W");
        xAxisLabel.add("T");
        xAxisLabel.add("F");
        xAxisLabel.add("S");


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);
        ValueFormatter formatter = new ValueFormatter() {


            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        };

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setLabelCount(8);
        xAxis.setValueFormatter(formatter);
    }
    public String getDecimalPlaces(Float f) {
        String txt = Float.toString(f);
        for(int k=0;k<txt.length();k++){
            if(txt.charAt(k)=='.'){
                txt=txt.substring(k+1);
            }
        }
        return txt;
    }
    public String getLevel(Float f){
        String txt = Float.toString(f);
        for(int k=0;k<txt.length();k++){
            if(txt.charAt(k)=='.'){
                txt=txt.substring(0,k);
            }
        }
        return txt;
    }
    public static ArrayList<String> getDaysBetweenDates(Date startdate, Date enddate)
    {
        ArrayList<String> date = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().compareTo(enddate)==0)
        {
            Date result = calendar.getTime();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
            date.add(simpleDateFormat.format(result));
            calendar.add(Calendar.DATE, 1);
        }

        return date;
    }
}