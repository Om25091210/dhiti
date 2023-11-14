package com.aryomtech.dhitifoundation;

import static android.content.Context.MODE_PRIVATE;
import static com.av.smoothviewpager.utils.Smoolider_Utils.autoplay_viewpager;
import static com.av.smoothviewpager.utils.Smoolider_Utils.stop_autoplay_ViewPager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.Adapter.CardAdapterClass;
import com.aryomtech.dhitifoundation.GroupTask.group_task_list;
import com.aryomtech.dhitifoundation.Updates.updates;
import com.aryomtech.dhitifoundation.admin_panel.fluid_Overview;
import com.aryomtech.dhitifoundation.admin_panel.model.fluid_Cards_Data;
import com.aryomtech.dhitifoundation.events.Events;
import com.aryomtech.dhitifoundation.privateTask.task_private;
import com.aryomtech.dhitifoundation.public_notes.announcement_list;
import com.aryomtech.dhitifoundation.slider.Model.ModelSmoolider;
import com.aryomtech.dhitifoundation.slider.Model.SmooliderAdapter;
import com.aryomtech.mylibrary2.HorizontalCalendarView;
import com.astritveliu.boom.Boom;
import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.av.smoothviewpager.utils.Txt_Factory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;
import www.sanju.motiontoast.MotionToast;


public class MainFragment extends Fragment {

    String identity="";
    private FirebaseAuth mAuth;
    FirebaseUser user;
    View view;
    boolean first_run=true;
    LottieAnimationView website,toolbar,private_task;
    TextView pub_notes,events,name,groupTask,updates;
    DatabaseReference events_ref,user_reference,dates_ref,card_ref,noti_ref;
    ValueEventListener listener,valueListener;
    ArrayList<String> imageArray=new ArrayList<>();
    ArrayList<String> total_amount_list=new ArrayList<>();
    String deep_link_value,deep_link_uid_value;
    ImageView alert;
    CardAdapterClass cardAdapterClass;
    ArrayList<fluid_Cards_Data> list;
    int counter=0;
    final String OLD_FORMAT = "dd-MM-yyyy";
    String m,d,y;
    Long amount=0L;
    String DeviceToken;
    ArrayList<String> formatted_dates;
    private Context contextNullSafe;
    RecyclerView recyclerView;
    SmoothBottomBar smoothBottomBar;
    private ShimmerFrameLayout shimmerFrameLayout,shimmerfluid,shimmer_slider;
    //slider
    List<String> position=new ArrayList<>();
    List<String> titles=new ArrayList<>();
    //private TextSwitcher txt_swithcher_position;
    //private TextView txt_title;
    private boolean is_autoplay = false;
    private int currentPosition;
    private SmoothViewpager viewPager;
    private List<ModelSmoolider> feedItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        viewPager = view.findViewById(R.id.smoolider);
        //txt_swithcher_position = view.findViewById(R.id.txt_swithcher_position);
        //txt_title = view.findViewById(R.id.txt_title);


        generate_items();
        getting_device_token();
        try {
            deep_link_value = getArguments().getString("deep_link_value");//deep link value
            deep_link_uid_value = getArguments().getString("deep_link_uid_value");//deep link uid value
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(deep_link_value!=null){
            fluid_Overview fluid_overview=new fluid_Overview();
            Bundle args=new Bundle();
            args.putString("sending_key",deep_link_value);
            args.putString("sending_deep_uid_value",deep_link_uid_value);
            fluid_overview.setArguments(args);

            ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,fluid_overview)
                    .addToBackStack(null)
                    .commit();
        }

        //use this method if you want to interact with other widgets
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(final int position) {
                // handle operations with current page
                manage_widgets_on_swipe(position);

            }
        });

        pub_notes=view.findViewById(R.id.textView17);
        View layout_member = view.findViewById(R.id.guest_mode);
        View layout_guest = view.findViewById(R.id.slider);
        //setting visibility
        layout_member.setVisibility(View.GONE);
        layout_guest.setVisibility(View.GONE);
        events=view.findViewById(R.id.textView19);
        groupTask=view.findViewById(R.id.textView18);
        name=view.findViewById(R.id.textView20);
        shimmerFrameLayout = view.findViewById(R.id.shimmerEffect);
        shimmerfluid = view.findViewById(R.id.shimmerfluid);
        shimmer_slider = view.findViewById(R.id.shimmer_slider);
        private_task = view.findViewById(R.id.view5);
        updates = view.findViewById(R.id.textView16);
        alert = view.findViewById(R.id.imageView22);

        alert.setVisibility(View.GONE);
        recyclerView=view.findViewById(R.id.rv);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        user_reference=FirebaseDatabase.getInstance().getReference().child("users");
        noti_ref=FirebaseDatabase.getInstance().getReference().child("Notifications");

        noti_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    try {
                        long total_count = snapshot.getChildrenCount();
                        long stored_value = getContextNullSafety().getSharedPreferences("notification_count_seen", MODE_PRIVATE)
                                .getLong("count_seen_notification4578", 0L);
                        if (stored_value == total_count)
                            alert.setVisibility(View.GONE);
                        if (stored_value < total_count)
                            alert.setVisibility(View.VISIBLE);
                        if (stored_value > total_count)
                            getContextNullSafety().getSharedPreferences("notification_count_seen", MODE_PRIVATE).edit()
                                    .putLong("count_seen_notification4578", total_count).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()+"").child("dplink").exists()){
                    String user_dp=snapshot.child(user.getUid()).child("dplink").getValue(String.class);
                    try {
                        getContextNullSafety().getSharedPreferences("imageBASE64_data",MODE_PRIVATE).edit()
                                .putString("base64_user_image",user_dp).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else{
                    try {
                        getContextNullSafety().getSharedPreferences("imageBASE64_data", MODE_PRIVATE).edit()
                                .putString("base64_user_image", "").apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

      user_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(user.getUid()+"").child("progress").exists())
                    user_reference.child(user.getUid()+"").child("progress").child("xp").setValue(0);
                String name_str=snapshot.child(user.getUid()+"").child("name").getValue(String.class);
                name_str="Hi "+name_str+"!";
                try {
                    name.setText(name_str);
                //Verifying identity and storing it.
                if(snapshot.child(user.getUid()+"").child("identity").exists()){
                    if (Objects.equals(snapshot.child(user.getUid()+"").child("identity").getValue(String.class), "guest")){
                        getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                                .putString("2509_0101_1310_identity","guest").apply();
                        layout_member.setVisibility(View.GONE);
                        layout_guest.setVisibility(View.VISIBLE);
                    }
                    else if (Objects.equals(snapshot.child(user.getUid()+"").child("identity").getValue(String.class), "member")){
                        getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                                .putString("2509_0101_1310_identity","member").apply();
                        layout_member.setVisibility(View.VISIBLE);
                        layout_guest.setVisibility(View.GONE);
                    }
                    else if (Objects.equals(snapshot.child(user.getUid()+"").child("identity").getValue(String.class), "chapter-head")){
                        getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                                .putString("2509_0101_1310_identity","chapter-head").apply();
                        layout_member.setVisibility(View.VISIBLE);
                        layout_guest.setVisibility(View.GONE);
                    }
                    else if (Objects.equals(snapshot.child(user.getUid()+"").child("identity").getValue(String.class), "admin")){
                        getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                                .putString("2509_0101_1310_identity","admin").apply();
                        layout_member.setVisibility(View.VISIBLE);
                        layout_guest.setVisibility(View.GONE);
                    }
                }
                else if(snapshot.child(user.getUid()+"").child("request").exists()){
                    if (Objects.equals(snapshot.child(user.getUid()+"").child("request").getValue(String.class), "given")){
                        getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                                .putString("2509_0101_1310_identity","given").apply();
                        layout_member.setVisibility(View.GONE);
                        layout_guest.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE).edit()
                            .putString("2509_0101_1310_identity","guest").apply();
                    layout_member.setVisibility(View.GONE);
                    layout_guest.setVisibility(View.VISIBLE);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        events_ref=FirebaseDatabase.getInstance().getReference().child("Events");
        formatted_dates=new ArrayList<>();
        list=new ArrayList<>();
        cardAdapterClass=new CardAdapterClass(list,getContext(),total_amount_list);

        events.setOnClickListener(v->{
            if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction().
                        remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
            }
            ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.drawer,new Events())
                    .commit();
        });

        smoothBottomBar=getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(1);
        smoothBottomBar.setVisibility(View.VISIBLE);
        website=getActivity().findViewById(R.id.website);
        toolbar=getActivity().findViewById(R.id.toolbar);
        website.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);


        updates.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new updates())
                .addToBackStack(null)
                .commit());

        pub_notes.setOnClickListener(v->{
            identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                    .getString("2509_0101_1310_identity","");
            if(identity.equals("member") || identity.equals("chapter-head") || identity.equals("admin")) {
                if (((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new announcement_list(), "list_announcement")
                        .commit();
            }
            else{
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Access Denied!",
                        "You are in guest mode.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
            }
        });
        private_task.setOnClickListener(v->{
                if (((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new task_private(), "privateTask")
                        .commit();
        });
        groupTask.setOnClickListener(v->{
                if (((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.drawer, new group_task_list(), "groupTask")
                        .commit();
        });
        HorizontalCalendarView calendarView = view.findViewById(R.id.calendar);

        Calendar starttime = Calendar.getInstance();
        starttime.add(Calendar.MONTH,-6);

        Calendar endtime = Calendar.getInstance();
        endtime.add(Calendar.MONTH,6);

        listener=events_ref.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && first_run) {
                    try {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            m = check_month(Objects.requireNonNull(ds.getKey()).substring(4, 7));
                            d = ds.getKey().substring(8, 10);
                            y = ds.getKey().substring(11);

                            String conctenate_dates = d + "-" + m + "-" + y;
                            formatted_dates.add(conctenate_dates);

                        }
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        calendarView.setUpCalendar(starttime.getTimeInMillis(),
                                endtime.getTimeInMillis(),
                                formatted_dates,
                                date -> show_dialog(date));
                        first_run = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        card_ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");
        card_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shimmerfluid.hideShimmer();
                shimmerfluid.setVisibility(View.GONE);
                for (DataSnapshot ds:snapshot.getChildren()){
                    list.add(ds.getValue(fluid_Cards_Data.class));
                    if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("transactions").exists()) {
                        for (DataSnapshot amount_ds : snapshot.child(ds.getKey()).child("transactions").getChildren()) {
                            amount =amount + Long.parseLong(Objects.requireNonNull(snapshot.child(ds.getKey()).child("transactions").child(Objects.requireNonNull(amount_ds.getKey())).child("amount_paid").getValue(String.class)));
                        }
                        total_amount_list.add(amount+"");
                        amount=0L;
                    }
                    else{
                        if(snapshot.child(Objects.requireNonNull(ds.getKey())).child("contributed").exists())
                            total_amount_list.add(snapshot.child(Objects.requireNonNull(ds.getKey())).child("contributed").getValue(String.class));
                        else
                            total_amount_list.add("");
                    }
                }
                Collections.reverse(total_amount_list);
                Collections.reverse(list);
                cardAdapterClass=new CardAdapterClass(list,getContext(),total_amount_list);
                cardAdapterClass.notifyDataSetChanged();
                if(recyclerView!=null && cardAdapterClass!=null)
                    recyclerView.setAdapter(cardAdapterClass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
    private void init_widgets() {
       /* new Boom(txt_title);

        txt_swithcher_position.setFactory(new Txt_Factory(R.style.CustomTextView, true,getContextNullSafety()));
        txt_swithcher_position.setCurrentText(position.get(0));
        txt_title.setText(titles.get(0));*/

        manage_widgets_on_swipe(0);
    }
    private void generate_items(){
        feedItemList = new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("slider");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot keys: snapshot.getChildren()){
                    /*counter++;
                    position.add(""+counter+" / "+snapshot.getChildrenCount());
                    titles.add(snapshot.child(Objects.requireNonNull(keys.getKey())).child("head_text").getValue(String.class));*/
                    ModelSmoolider gift = new ModelSmoolider();
                    gift.setImage_url(snapshot.child(Objects.requireNonNull(keys.getKey())).child("image_url").getValue(String.class));
                    gift.setHead_text(snapshot.child(Objects.requireNonNull(keys.getKey())).child("head_text").getValue(String.class));
                    gift.setDes_text(snapshot.child(Objects.requireNonNull(keys.getKey())).child("des_text").getValue(String.class));

                    feedItemList.add(gift);
                }
                Collections.reverse(titles);
                Collections.reverse(feedItemList);
                shimmer_slider.hideShimmer();
                shimmer_slider.setVisibility(View.GONE);
                //slider
                viewPager.setAdapter(new SmooliderAdapter(feedItemList,getContextNullSafety()));
                manage_autoplay();
                init_widgets();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void manage_widgets_on_swipe(int pos) {
        int[] animH = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int[] animV = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

       /* txt_swithcher_position.setInAnimation(getContextNullSafety(), animH[0]);
        txt_swithcher_position.setOutAnimation(getContextNullSafety(), animH[1]);
        txt_swithcher_position.setText(position.get(pos % position.size())); //crash 01 - 1.9

        txt_title.setVisibility(View.INVISIBLE);
        txt_title.startAnimation(AnimationUtils.loadAnimation(getContextNullSafety(), R.anim.fade_in));
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(titles.get(pos % titles.size())+"");*/

        currentPosition = pos;
    }

    private void manage_autoplay(){
        if(is_autoplay){
            is_autoplay = false;
            stop_autoplay_ViewPager();
        } else {
            is_autoplay = true;
            autoplay_viewpager(viewPager,feedItemList.size());
        }

    }

    private void getting_device_token() {

                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {

                }).addOnFailureListener(e -> {
                    //handle e
                }).addOnCanceledListener(() -> {
                    //handle cancel
                }).addOnCompleteListener(task ->
                {
                    try {
                        DeviceToken = task.getResult();
                        user_reference.child(user.getUid()).child("token").setValue(DeviceToken+"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void show_dialog(String date) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, Locale.getDefault());
        Date todaysDate = sdf.parse(date);

        int ch=0;
        String store = null;
        int whiteend=0;
        String str=todaysDate+"";
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

        final boolean[] first_open = {true};
        dates_ref=FirebaseDatabase.getInstance().getReference().child("Events");
        String finalStore = store;
        valueListener=dates_ref.child(store).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(first_open[0] && snapshot.exists()){
                    for (DataSnapshot ds:snapshot.getChildren()) {
                        String title = snapshot.child(Objects.requireNonNull(ds.getKey())).child("title").getValue(String.class);
                        String location = snapshot.child(ds.getKey()).child("location").getValue(String.class);
                        String message=snapshot.child(ds.getKey()).child("message").getValue(String.class)+"";
                        String image_url=snapshot.child(ds.getKey()).child("image").getValue(String.class);

                        Dialog dialog = new Dialog(requireContext());
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.dialog_event_home);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

                        TextView title_text=dialog.findViewById(R.id.textTitle);
                        TextView location_text=dialog.findViewById(R.id.textSubtitle);
                        TextView message_text=dialog.findViewById(R.id.textDateTime);
                        TextView date=dialog.findViewById(R.id.date);
                        RoundedImageView image=dialog.findViewById(R.id.imageNote);

                        title_text.setText(title);
                        location_text.setText(location);
                        if(!message.equals("null"))
                            message_text.setText(message);
                        date.setText(finalStore);

                        try{
                            if(image_url!=null) {
                                Glide.with(requireContext()).asBitmap()
                                        .load(image_url)
                                        .listener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                new Handler(Looper.myLooper()).postDelayed(() ->
                                                       image.setImageBitmap(resource), 700);
                                                return false;
                                            }
                                        })
                                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                                        .placeholder(R.drawable.ic_good_team)
                                        .into(image);
                            }
                            else{
                                image.setImageResource(R.drawable.ic_logo_icon);
                            }
                            if(!requireActivity().isFinishing())
                            {
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                first_open[0] =false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String check_month(String month){

        String date_month = "";
        switch (month) {
            case "Jan":
                date_month = "01";
                break;
            case "Feb":
                date_month = "02";
                break;
            case "Mar":
                date_month = "03";
                break;
            case "Apr":
                date_month = "04";
                break;
            case "May":
                date_month = "05";
                break;
            case "Jun":
                date_month = "06";
                break;
            case "Jul":
                date_month = "07";
                break;
            case "Aug":
                date_month = "08";
                break;
            case "Sep":
                date_month = "09";
                break;
            case "Oct":
                date_month = "10";
                break;
            case "Nov":
                date_month = "11";
                break;
            case "Dec":
                date_month = "12";
                break;
        }
        return date_month;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getContext(), Splash.class));
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
    @Override
    public void onDestroy() {
        super.onDestroy();

        pub_notes.setOnClickListener(null);
        events.setOnClickListener(null);
        name=null;
        if(listener!=null)
            events_ref.removeEventListener(listener);
        if(valueListener!=null)
            dates_ref.removeEventListener(valueListener);
        imageArray.clear();
        formatted_dates.clear();
        recyclerView=null;
    }
}