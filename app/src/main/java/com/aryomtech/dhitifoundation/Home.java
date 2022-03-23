package com.aryomtech.dhitifoundation;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.Profile.profile;
import com.aryomtech.dhitifoundation.noteactivity.NoteMain;
import com.aryomtech.dhitifoundation.website.d_Website;
import com.aryomtech.mylibrary.views.views.DuoDrawerLayout;
import com.aryomtech.mylibrary.views.views.DuoMenuView;
import com.aryomtech.mylibrary.views.widgets.DuoDrawerToggle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;
import www.sanju.motiontoast.MotionToast;

public class Home extends AppCompatActivity  implements DuoMenuView.OnMenuClickListener {

    private menuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;

    private FirebaseAuth mAuth;
    String sign_method="";
    LottieAnimationView website;
    GoogleSignInClient mGoogleSignInClient;
    SmoothBottomBar bottomBar;
    private ArrayList<String> mTitles = new ArrayList<>();
    Uri deep_link_uri;
    private AppUpdateManager mAppUpdateManager;
    private final int RC_APP_UPDATE = 999;
    private int inAppUpdateType;
    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        // Creates instance of the manager.
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        //lambda operation used for below listener
        //For flexible update
        installStateUpdatedListener = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        //For Immediate
        inAppUpdateType = AppUpdateType.IMMEDIATE; //1
        inAppUpdate();

        website=findViewById(R.id.website);
        website.setOnClickListener(v->{
            Intent web=new Intent(Home.this, d_Website.class);
            startActivity(web);
        });
        Window window = Home.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Home.this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(Home.this, R.color.red_200));

        bottomBar=findViewById(R.id.bottomBar);
        bottomBar.setItemActiveIndex(1);
        bottomBar.setOnItemSelectedListener(i -> {
            if(i==0){
                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new NoteMain(this))
                        .commit();
            }
            else if(i==1){
                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new MainFragment())
                        .commit();
            }
            else if(i==2){
                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new profile())
                        .commit();
            }
            return false;
        });

        sign_method=getSharedPreferences("SignInGOOGLE201",MODE_PRIVATE)
                .getString("203google_signin","phone");

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));

        // Initialize the views
        mViewHolder = new ViewHolder();

        // Handle menu actions
        handleMenu();

        // Handle drawer actions
        handleDrawer();

        deep_link_uri = getIntent().getData();//deep link value
        // checking if the uri is null or not.

        // Show main fragment in container
        goToFragment(new MainFragment());
        mMenuAdapter.setViewSelected(0);
        setTitle(mTitles.get(0));

        findViewById(R.id.card_fb).setOnClickListener(s-> {
            String facebookUrl ="https://www.facebook.com/dhitifoundation/";
            Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(facebookAppIntent);
        });
        findViewById(R.id.card_twitter).setOnClickListener(s->{
            String url = "https://twitter.com/DhitiFoundation?s=08";
            Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(twitterAppIntent);
        });
        findViewById(R.id.card_linkedin).setOnClickListener(s->{
            String url = "https://www.linkedin.com/in/dhiti-welfare-foundation-9779981ab";
            Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(linkedInAppIntent);
        });
        findViewById(R.id.card_whatsapp).setOnClickListener(s->{
            String url = "https://api.whatsapp.com/send?phone=" +"+91"+ "8359996222";
            try {
                PackageManager pm = getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        findViewById(R.id.card_insta).setOnClickListener(s->{
            Intent insta_in;
            String scheme = "http://instagram.com/_u/"+"dhiti_foundation";
            String path = "https://instagram.com/"+"dhiti_foundation";
            String nomPackageInfo ="com.instagram.android";
            try {
                getPackageManager().getPackageInfo(nomPackageInfo, 0);
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            } catch (Exception e) {
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            }
            startActivity(insta_in);
        });
    }
    private void handleMenu() {
        mMenuAdapter = new menuAdapter(mTitles);

        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void goToFragment(Fragment fragment) {
        if(deep_link_uri!=null){
            if (deep_link_uri.toString().equals("https://www.dhitifoundation.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else if(deep_link_uri.toString().equals("http://www.dhitifoundation.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else if(deep_link_uri.toString().equals("www.dhitifoundation.android")){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, fragment,"mainFrag").commit();
            }
            else{
                // if the uri is not null then we are getting the
                // path segments and storing it in list.
                List<String> parameters = deep_link_uri.getPathSegments();
                // after that we are extracting string from that parameters.
                if(parameters!=null) {
                    if(parameters.size()>1) {
                        String param = parameters.get(parameters.size() - 1);
                        String uid = parameters.get(parameters.size() - 2);
                        // on below line we are setting
                        // that string to our text view
                        // which we got as params.
                        Log.e("deep_link_value", param + "");
                        Log.e("deep_link_value_uid", uid + "");
                        Bundle args = new Bundle();
                        args.putString("deep_link_value", param);
                        args.putString("deep_link_uid_value", uid);
                        fragment.setArguments(args);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, fragment, "mainFrag").commit();
                    }
                    else{
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, fragment,"mainFrag").commit();
                    }
                }
            }
        }
        else{
             FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
             transaction.add(R.id.container, fragment,"mainFrag").commit();
        }

    }

    @Override
    public void onFooterClicked() {
        if(sign_method!=null) {
            if (sign_method.equals("google")) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(Home.this, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(Home.this, task -> MotionToast.Companion.darkColorToast(Home.this,
                                "Logout Successfull",
                                "Sign out Successfull!",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(Home.this, R.font.helvetica_regular)));
            }
        }
        mAuth.signOut();
        startActivity(new Intent(Home.this , Splash.class));
        finish();
    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
        // Set the toolbar title


        // Set the right options selected
        mMenuAdapter.setViewSelected(position);

        // Navigate to the right fragment
        if(position==1) {
            DatabaseReference reference_privacy= FirebaseDatabase.getInstance().getReference().child("policy");
            reference_privacy.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String link=snapshot.getValue(String.class);
                    Intent web=new Intent(Home.this, d_Website.class);
                    web.putExtra("weblink",link);
                    startActivity(web);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==2) {
            String url = "https://www.dhitifoundation.com/our-story/";
            Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(linkedInAppIntent);
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==3) {
            String title ="*Dhiti welfare foundation*"+"\n\n"+"Download our app to support us in making the world a better place."; //Text to be shared
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+"This is a playstore link to download.. " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==4) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new about_aryomtech())
                    .addToBackStack(null)
                    .commit();
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else if(position==5){
            DatabaseReference reference_privacy= FirebaseDatabase.getInstance().getReference().child("termsandcondition");
            reference_privacy.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String link=snapshot.getValue(String.class);
                    Intent web=new Intent(Home.this, d_Website.class);
                    web.putExtra("weblink",link);
                    startActivity(web);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            mMenuAdapter.setViewSelected(0);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        else {
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }
        // Close the drawer

    }
    private void inAppUpdate() {

        try {
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)) {
                    // Request the update.

                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                inAppUpdateType,
                                // The current activity making the update request.
                                Home.this,
                                // Include a request code to later monitor this update request.
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            //when user clicks update button
            if (resultCode == RESULT_OK) {
                Toast.makeText(Home.this, "App download starts...", Toast.LENGTH_LONG).show();
            } else if (resultCode != RESULT_CANCELED) {
                //if you want to request the update again just call checkUpdate()
                Toast.makeText(Home.this, "App download canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
                Toast.makeText(Home.this, "App download failed.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void popupSnackbarForCompleteUpdate() {
        try {
            Snackbar snackbar =
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            "An update has just been downloaded.\nRestart to update",
                            Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("INSTALL", view -> {
                if (mAppUpdateManager != null){
                    mAppUpdateManager.completeUpdate();
                }
            });
            snackbar.setActionTextColor(ResourcesCompat.getColor(getResources(),R.color.green_A400,null));
            snackbar.show();

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        try {
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                inAppUpdateType,
                                this,
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });


            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                //For flexible update
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();
    }
    @Override
    protected void onDestroy() {
        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onDestroy();
    }
    private class ViewHolder {
        private final DuoDrawerLayout mDuoDrawerLayout;
        private final DuoMenuView mDuoMenuView;
        private final ImageView mToolbar;

        ViewHolder() {
            mDuoDrawerLayout =findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = findViewById(R.id.toolbar);
        }
    }
}