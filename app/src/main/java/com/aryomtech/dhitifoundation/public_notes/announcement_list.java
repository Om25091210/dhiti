package com.aryomtech.dhitifoundation.public_notes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.public_notes.Adapter.AnnouncementAdapterClass;
import com.aryomtech.dhitifoundation.public_notes.model.note_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class announcement_list extends Fragment {

    View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context contextNullSafe;
    DatabaseReference ref;
    RecyclerView recyclerView;
    EditText search;
    AnnouncementAdapterClass announcementAdapterClass;
    ArrayList<note_model> list;
    ArrayList<note_model> mylist;
    ArrayList<note_model> pin_list_item;
    TextView message;
    ImageView image;
    View menu_;
    int check_key_one=0,check_key_two=0,check_key_three=0;
    String key_pin1,key_pin2,key_pin3;
    String layout="list";
    LottieAnimationView create;
    ValueEventListener listener;
    TextView all,category,writtenby,city,content,date,Title;
    String filter="all";
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_announcement_list, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        progressBar=view.findViewById(R.id.progressBar2);
        mSwipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.light_three));

        ref= FirebaseDatabase.getInstance().getReference().child("Announcement");
        recyclerView=view.findViewById(R.id.rv_content);
        message=view.findViewById(R.id.textView27);
        image=view.findViewById(R.id.imageView3);
        menu_=view.findViewById(R.id.imageView4);

        all=view.findViewById(R.id.all);
        category=view.findViewById(R.id.category);
        writtenby=view.findViewById(R.id.writtenby);
        city=view.findViewById(R.id.city);
        content=view.findViewById(R.id.content);
        date=view.findViewById(R.id.date);
        Title=view.findViewById(R.id.Title);

        new Handler(Looper.myLooper()).postDelayed(this::freeMemory,2500);
        key_pin1=getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE)
                .getString("pin_1_announcement","");
        key_pin2=getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE)
                .getString("pin_2_announcement","");
        key_pin3=getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE)
                .getString("pin_3_announcement","");

        all.setOnClickListener(v->{
            all.setBackgroundResource(R.drawable.search_selector);
            category.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="all";
        });
        category.setOnClickListener(v->{
            category.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="category";
        });
        writtenby.setOnClickListener(v->{
            writtenby.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            category.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="writtenby";
        });
        city.setOnClickListener(v->{
            city.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            category.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="city";
        });
        content.setOnClickListener(v->{
            content.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            category.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="content";
        });
        date.setOnClickListener(v->{
            date.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            category.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            Title.setBackgroundResource(R.drawable.draw_three);
            filter="date";
        });
        Title.setOnClickListener(v->{
            Title.setBackgroundResource(R.drawable.search_selector);
            all.setBackgroundResource(R.drawable.draw_three);
            category.setBackgroundResource(R.drawable.draw_three);
            writtenby.setBackgroundResource(R.drawable.draw_three);
            city.setBackgroundResource(R.drawable.draw_three);
            content.setBackgroundResource(R.drawable.draw_three);
            date.setBackgroundResource(R.drawable.draw_three);
            filter="Title";
        });
        String layout_str=getContextNullSafety().getSharedPreferences("layout_preference_2509", Context.MODE_PRIVATE)
                .getString("layout_change_0529","list");
        if(layout_str.equals("list")) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(layoutManager);
            layout="list";
        }
        else if(layout_str.equals("grid")){
            StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setLayoutManager(gridLayoutManager);
            layout="grid";
        }
        final PopupMenu dropDownMenu = new PopupMenu(getContext(), menu_);

        final Menu menu = dropDownMenu.getMenu();
        // add your items:
        menu.add(0, 0, 0, "grid");
        menu.add(0, 1, 0, "list");
        // OR inflate your menu from an XML:
        dropDownMenu.getMenuInflater().inflate(R.menu.layout_menu, menu);
        dropDownMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    // item ID 0 was clicked
                    StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    getContextNullSafety().getSharedPreferences("layout_preference_2509", Context.MODE_PRIVATE).edit()
                            .putString("layout_change_0529","grid").apply();
                    layout="grid";
                    return true;
                case 1:
                    // item ID 1 was clicked
                    LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);
                    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recyclerView.setLayoutManager(layoutManager1);
                    getContextNullSafety().getSharedPreferences("layout_preference_2509", Context.MODE_PRIVATE).edit()
                            .putString("layout_change_0529","list").apply();
                    layout="list";
                    return true;
            }
            return false;
        });

        menu_.setOnClickListener(v-> dropDownMenu.show());

        search=view.findViewById(R.id.search);
        list=new ArrayList<>();
        mylist=new ArrayList<>();
        pin_list_item=new ArrayList<>();
        //Hide the keyboard
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        create=view.findViewById(R.id.create);
        create.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new create_announcement())
                .addToBackStack(null)
                .commit());

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

        mSwipeRefreshLayout.setColorScheme(R.color.blue,
                R.color.green, R.color.orange, R.color.purple_300);
        mSwipeRefreshLayout.setOnRefreshListener(this::onstart);
        new Handler(Looper.myLooper()).postDelayed(this::onstart,800);
        search.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    search(s+"");
                }
        });
        return view;
    }

    public void onstart() {
        if(ref!=null){

          ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mSwipeRefreshLayout.setRefreshing(false);

                    list.clear();
                    if(snapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        for(DataSnapshot ds:snapshot.getChildren()){
                            list.add(ds.getValue(note_model.class));
                            try {
                                message.setVisibility(View.GONE);
                                image.setVisibility(View.GONE);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        pin_list_item.clear();
                        Collections.reverse(list);
                        if(!key_pin1.equals("")){
                            for(note_model object1:list) {
                                if(object1.getAn_push_key().equals(key_pin1)){
                                    pin_list_item.add(0,object1);
                                    check_key_one=1;
                                    break;
                                }
                            }
                            if(check_key_one==0){
                                getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE).edit()
                                        .putString("pin_1_announcement","").apply();
                            }
                        }
                        if(!key_pin2.equals("")){
                            for(note_model object1:list) {
                                if(object1.getAn_push_key().equals(key_pin2)){
                                    pin_list_item.add(0,object1);
                                    check_key_two=1;
                                    break;
                                }
                            }
                            if(check_key_two==0){
                                getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE).edit()
                                        .putString("pin_2_announcement","").apply();
                            }
                        }
                        if(!key_pin3.equals("")){
                            for(note_model object1:list) {
                                if(object1.getAn_push_key().equals(key_pin3)){
                                    pin_list_item.add(0,object1);
                                    check_key_three=1;
                                    break;
                                }
                            }
                            if(check_key_three==0){
                                getContextNullSafety().getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE).edit()
                                        .putString("pin_3_announcement","").apply();
                            }
                        }
                        if (pin_list_item.size()>0){
                            list.removeAll(pin_list_item);
                            for (note_model each_object:pin_list_item)
                                list.add(0,each_object);
                        }
                        if(layout.equals("grid")) {
                            StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                            if(recyclerView!=null) {
                                recyclerView.setItemViewCacheSize(20);
                                recyclerView.setLayoutManager(gridLayoutManager);
                            }
                        }
                        announcementAdapterClass=new AnnouncementAdapterClass(list, getContext());
                        announcementAdapterClass.notifyDataSetChanged();
                        if(recyclerView!=null) {
                            recyclerView.setAdapter(announcementAdapterClass);
                        }
                    }
                    else{
                        try {
                            progressBar.setVisibility(View.GONE);
                            message.setVisibility(View.VISIBLE);
                            image.setVisibility(View.VISIBLE);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
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
    private void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void search(String str) {
        mylist.clear();
        for(note_model object:list) {
            switch (filter) {
                case "all":

                    if (object.getCategory().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    } else if (object.getCity().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    } else if (object.getWrittenby().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    } else if (object.getDate().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    } else if (object.getContent().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    } else if (object.getHead().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "category":
                    if (object.getCategory().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "writtenby":
                    if (object.getWrittenby().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "city":
                    if (object.getCity().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "content":
                    if (object.getContent().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "date":
                    if (object.getDate().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
                case "Title":
                    if (object.getHead().toLowerCase().contains(str.toLowerCase().trim())) {
                        mylist.add(object);
                    }
                    break;
            }
        }
        if(layout.equals("grid")){
            StaggeredGridLayoutManager gridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            if(recyclerView!=null) {
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setLayoutManager(gridLayoutManager);
                AnnouncementAdapterClass announcementAdapterClass = new AnnouncementAdapterClass(mylist, getContext());
                recyclerView.setAdapter(announcementAdapterClass);
            }
        }
        else if(layout.equals("list")){
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
            if(recyclerView!=null) {
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setLayoutManager(layoutManager1);
                AnnouncementAdapterClass announcementAdapterClass = new AnnouncementAdapterClass(mylist, getContext());
                recyclerView.setAdapter(announcementAdapterClass);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (listener != null) {
            ref.removeEventListener(listener);
        }
        recyclerView=null;
        search=null;
        list.clear();
        mylist.clear();
        message=null;
        image.setImageBitmap(null);
        menu_.setOnClickListener(null);
        create.setOnClickListener(null);
        all.setOnClickListener(null);
        category.setOnClickListener(null);
        writtenby.setOnClickListener(null);
        city.setOnClickListener(null);
        content.setOnClickListener(null);
        date.setOnClickListener(null);
        Title.setOnClickListener(null);
    }
}