package com.aryomtech.dhitifoundation.GroupTask;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.Form_Fill;
import com.aryomtech.dhitifoundation.GroupTask.Adapter.grouptaskAdapter;
import com.aryomtech.dhitifoundation.GroupTask.Model.groupTaskData;
import com.aryomtech.dhitifoundation.GroupTask.Renderer.RoundedSlicesPieChartRenderer;
import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.R;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class group_task_list extends Fragment {

    View view;
    RecyclerView recyclerView;
    PieChart pieChart;
    EditText search;
    ImageView image_msg,img_back;
    TextView msg,finished_txt,ongoing_txt
            ,all,bsp,bhilai,korba,champa,other;
    PieData pieData;
    ArrayList<groupTaskData> mylist;
    DatabaseReference group_ref,users_ref;
    PieDataSet pieDataSet;
    CircleImageView profile_image;
    FirebaseAuth auth;
    FirebaseUser user;
    List<ArrayList<String>> keys_list;
    ArrayList<String> names_list;
    ArrayList<groupTaskData> groupTaskData_list;
    ArrayList<Long> list_tasks;
    ArrayList<String> names;
    Query query;
    String dp,city;
    String identity="";
    ProgressBar progressBar;
    ArrayList<Long> membership_list;
    ArrayList<PieEntry> pieEntries;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_group_task_list, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        pieChart=view.findViewById(R.id.pie_chart);
        recyclerView=view.findViewById(R.id.rv_content);
        msg=view.findViewById(R.id.textView27);
        image_msg=view.findViewById(R.id.imageView3);
        finished_txt=view.findViewById(R.id.finished_txt);
        ongoing_txt=view.findViewById(R.id.ongoing_txt);
        img_back=view.findViewById(R.id.imageBack);
        search=view.findViewById(R.id.search);
        profile_image=view.findViewById(R.id.profile_image);
        progressBar=view.findViewById(R.id.progressBar2);

        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        all=view.findViewById(R.id.all);
        bsp=view.findViewById(R.id.bsp);
        bhilai=view.findViewById(R.id.bhilai);
        korba=view.findViewById(R.id.korba);
        champa=view.findViewById(R.id.champa);
        other=view.findViewById(R.id.other);

        if (identity.equals("admin")) {
            all.setVisibility(View.VISIBLE);
            bsp.setVisibility(View.VISIBLE);
            bhilai.setVisibility(View.VISIBLE);
            korba.setVisibility(View.VISIBLE);
            champa.setVisibility(View.VISIBLE);
            other.setVisibility(View.VISIBLE);
        }
        else{
            all.setVisibility(View.GONE);
            bsp.setVisibility(View.GONE);
            bhilai.setVisibility(View.GONE);
            korba.setVisibility(View.GONE);
            champa.setVisibility(View.GONE);
            other.setVisibility(View.GONE);
        }


        groupTaskData_list=new ArrayList<>();
        list_tasks=new ArrayList<>();
        names_list= new ArrayList<>();
        membership_list=new ArrayList<>();
        keys_list=new ArrayList<>();
        names=new ArrayList<>();
        mylist=new ArrayList<>();

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.yellow_100));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.yellow_100));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(layoutManager);

        group_ref= FirebaseDatabase.getInstance().getReference().child("group_task");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");

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
        img_back.setOnClickListener(v->back());
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getUid()).child("dplink").exists()){
                    dp=snapshot.child(user.getUid()).child("dplink").getValue(String.class);
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
                if(snapshot.child(user.getUid()).child("city").exists()){
                    city=snapshot.child(user.getUid()).child("city").getValue(String.class);
                    switch (Objects.requireNonNull(city)) {
                        case "Bilaspur":
                            bsp.setVisibility(View.VISIBLE);
                            query=group_ref.orderByChild("city").equalTo("bilaspur");
                            search.setText("");
                            getTasks();
                            break;
                        case "Bhilai":
                            bhilai.setVisibility(View.VISIBLE);
                            query=group_ref.orderByChild("city").equalTo("bhilai");
                            search.setText("");
                            getTasks();
                            break;
                        case "Korba":
                            korba.setVisibility(View.VISIBLE);
                            query=group_ref.orderByChild("city").equalTo("korba");
                            search.setText("");
                            getTasks();
                            break;
                        case "Champa":
                            champa.setVisibility(View.VISIBLE);
                            query=group_ref.orderByChild("city").equalTo("champa");
                            search.setText("");
                            getTasks();
                            break;
                        default:
                            other.setVisibility(View.VISIBLE);
                            query=group_ref.orderByChild("city").equalTo("other");
                            search.setText("");
                            getTasks();
                            break;
                    }
                }
                else{
                    other.setVisibility(View.VISIBLE);
                    query=group_ref.orderByChild("city").equalTo("other");
                    search.setText("");
                    getTasks();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        all.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.bg_grp_search_selector);
            bsp.setBackgroundResource(R.drawable.draw_four);
            bhilai.setBackgroundResource(R.drawable.draw_four);
            korba.setBackgroundResource(R.drawable.draw_four);
            champa.setBackgroundResource(R.drawable.draw_four);
            other.setBackgroundResource(R.drawable.draw_four);
            query=group_ref;
            search.setText("");
            getTasks();
        });

        bsp.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.draw_four);
            bsp.setBackgroundResource(R.drawable.bg_grp_search_selector);
            bhilai.setBackgroundResource(R.drawable.draw_four);
            korba.setBackgroundResource(R.drawable.draw_four);
            champa.setBackgroundResource(R.drawable.draw_four);
            other.setBackgroundResource(R.drawable.draw_four);
            query=group_ref.orderByChild("city").equalTo("bilaspur");
            search.setText("");
            getTasks();
        });
        bhilai.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.draw_four);
            bsp.setBackgroundResource(R.drawable.draw_four);
            bhilai.setBackgroundResource(R.drawable.bg_grp_search_selector);
            korba.setBackgroundResource(R.drawable.draw_four);
            champa.setBackgroundResource(R.drawable.draw_four);
            other.setBackgroundResource(R.drawable.draw_four);
            query=group_ref.orderByChild("city").equalTo("bhilai");
            search.setText("");
            getTasks();
        });
        korba.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.draw_four);
            bsp.setBackgroundResource(R.drawable.draw_four);
            bhilai.setBackgroundResource(R.drawable.draw_four);
            korba.setBackgroundResource(R.drawable.bg_grp_search_selector);
            champa.setBackgroundResource(R.drawable.draw_four);
            other.setBackgroundResource(R.drawable.draw_four);
            query=group_ref.orderByChild("city").equalTo("korba");
            search.setText("");
            getTasks();
        });
        champa.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.draw_four);
            bsp.setBackgroundResource(R.drawable.draw_four);
            bhilai.setBackgroundResource(R.drawable.draw_four);
            korba.setBackgroundResource(R.drawable.draw_four);
            champa.setBackgroundResource(R.drawable.bg_grp_search_selector);
            other.setBackgroundResource(R.drawable.draw_four);
            query=group_ref.orderByChild("city").equalTo("champa");
            search.setText("");
            getTasks();
        });
        other.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.draw_four);
            bsp.setBackgroundResource(R.drawable.draw_four);
            bhilai.setBackgroundResource(R.drawable.draw_four);
            korba.setBackgroundResource(R.drawable.draw_four);
            champa.setBackgroundResource(R.drawable.draw_four);
            other.setBackgroundResource(R.drawable.bg_grp_search_selector);
            query=group_ref.orderByChild("city").equalTo("other");
            search.setText("");
            getTasks();
        });

        pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ResourcesCompat.getColor(getContextNullSafety().getResources(), R.color.card_personal_bg, null),
                ResourcesCompat.getColor(getContextNullSafety().getResources(), R.color.blue_A200, null));
        pieData = new PieData(pieDataSet);
        pieChart.setCenterText("");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(Color.BLUE);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(80);
        //Rendering
        pieChart.setRenderer(new RoundedSlicesPieChartRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler()));
        pieDataSet.setValueTextColor(Color.TRANSPARENT);
        pieDataSet.setSliceSpace(2f);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(true);
        pieChart.setRotationEnabled(true);
        pieChart.animateY(1400);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        if (identity.equals("admin")) {
            query = group_ref;
            getTasks();
        }
        return view;
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

    private void getTasks() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupTaskData_list.clear();
                list_tasks.clear();
                names_list.clear();
                membership_list.clear();
                keys_list.clear();
                names.clear();
                mylist.clear();
                int count_for_finished_task=0;
                int count_for_ongoing_task=0;
                if(snapshot.exists()){
                    recyclerView.setVisibility(View.VISIBLE);
                    for(DataSnapshot ds:snapshot.getChildren()){
                        if(!Objects.equals(ds.getKey(), "added_task")){
                            if (!Objects.equals(ds.getKey(),"members")) {
                                groupTaskData_list.add(ds.getValue(groupTaskData.class));
                            }
                        }
                        if(Objects.equals(snapshot.child(ds.getKey()).child("status").getValue(String.class), "ongoing"))
                            count_for_ongoing_task++;
                        else if (Objects.equals(snapshot.child(ds.getKey()).child("status").getValue(String.class), "finished"))
                            count_for_finished_task++;
                        if (snapshot.child(ds.getKey()).child("members").exists())
                            membership_list.add(snapshot.child(ds.getKey()).child("members").getChildrenCount());
                        else
                            membership_list.add(0L);
                        keys_list.add((ArrayList<String>) snapshot.child(ds.getKey()).child("members").getValue());
                    }
                    Collections.reverse(membership_list);
                    Collections.reverse(list_tasks);
                    Collections.reverse(groupTaskData_list);
                    int total_1=count_for_ongoing_task+count_for_finished_task;
                    float per_finished = 0;
                    if(total_1!=0) {
                        per_finished = (float) ((count_for_finished_task * 100) / total_1);
                    }
                    String txt1=per_finished+"%";
                    finished_txt.setText(txt1);
                    float per_ongoing=(float)100-per_finished;
                    String txt2=per_ongoing+"%";
                    ongoing_txt.setText(txt2);

                    pieEntries = new ArrayList<>();
                    pieEntries.add(new PieEntry((float) count_for_ongoing_task, 0));
                    pieEntries.add(new PieEntry((float) count_for_finished_task, 1));

                    pieDataSet = new PieDataSet(pieEntries, "");
                    try {
                        pieDataSet.setColors(ResourcesCompat.getColor(getContextNullSafety().getResources(), R.color.card_personal_bg, null),
                                ResourcesCompat.getColor(getContextNullSafety().getResources(), R.color.blue_A200, null));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    pieData = new PieData(pieDataSet);
                    pieChart.setCenterText("Total Task\n"+snapshot.getChildrenCount());
                    pieChart.setCenterTextSize(14f);
                    pieChart.setCenterTextColor(Color.parseColor("#E65100"));
                    pieChart.setData(pieData);
                    pieChart.setHoleRadius(80);

                    pieDataSet.setValueTextColor(Color.TRANSPARENT);
                    pieDataSet.setSliceSpace(2f);
                    pieChart.getLegend().setEnabled(false);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setTouchEnabled(true);
                    pieChart.setRotationEnabled(true);
                    pieChart.animateY(1400);
                    pieChart.setDragDecelerationFrictionCoef(0.95f);
                    retrieve();

                }
                else{
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    image_msg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        if(search!=null){
            search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
            });

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
    private void search(String s) {
        mylist.clear();
        String searched="yes";
        for(groupTaskData object:groupTaskData_list){
            if (object.getGiven_on().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getTask_deadline().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getLocation().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getTask_title().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            } else if (object.getStatus().toLowerCase().contains(s.toLowerCase().trim())) {
                mylist.add(object);
            }
        }
        if(s.equals(""))
            searched="no";
        grouptaskAdapter grouptaskAdapter=new grouptaskAdapter(getContext(),mylist,list_tasks,membership_list,names_list,searched);
        grouptaskAdapter.notifyDataSetChanged();
        try {
            recyclerView.setAdapter(grouptaskAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void retrieve() {
        users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(int i=0;i<keys_list.size();i++) {
                    ArrayList<String> keys_data = keys_list.get(i);
                    names.clear();
                    for (int j = 0; j < keys_data.size(); j++) {
                        String name = snapshot.child(keys_data.get(j)).child("name").getValue(String.class);
                        names.add(name);
                    }
                    names_list.add(names+"");

                }
                progressBar.setVisibility(View.GONE);
                msg.setVisibility(View.GONE);
                image_msg.setVisibility(View.GONE);
                Collections.reverse(names_list);
                grouptaskAdapter grouptaskAdapter=new grouptaskAdapter(getContext(),groupTaskData_list,list_tasks,membership_list,names_list,"no");
                grouptaskAdapter.notifyDataSetChanged();
                try {
                    recyclerView.setAdapter(grouptaskAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                }
            });
    }

}