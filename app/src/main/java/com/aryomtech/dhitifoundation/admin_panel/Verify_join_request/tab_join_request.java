package com.aryomtech.dhitifoundation.admin_panel.Verify_join_request;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Adapter.ViewPagerAdpaterVerifyRequest;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;


public class tab_join_request extends Fragment {

    View view;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_tab_join_request, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        tabLayout=view.findViewById(R.id.tablayout_id);
        viewPager2=view.findViewById(R.id.viewpager_id);
        ViewPagerAdpaterVerifyRequest adapter = new ViewPagerAdpaterVerifyRequest(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Member"));
        tabLayout.addTab(tabLayout.newTab().setText("Chapter-head"));

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_member_r);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_chapter_h);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());


                for(int index = 0; index < tab.view.getChildCount(); index++) {
                    View nextChild = tab.view.getChildAt(index);
                    if (nextChild instanceof TextView) {
                        TextView v = (TextView) nextChild;
                        v.setTypeface(null, Typeface.BOLD);

                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                for(int index = 0; index < tab.view.getChildCount(); index++) {
                    View nextChild = tab.view.getChildAt(index);
                    if (nextChild instanceof TextView) {
                        TextView v = (TextView) nextChild;
                        v.setTypeface(null, Typeface.NORMAL);
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
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
}