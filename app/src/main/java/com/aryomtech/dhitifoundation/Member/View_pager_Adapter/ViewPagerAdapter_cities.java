package com.aryomtech.dhitifoundation.Member.View_pager_Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aryomtech.dhitifoundation.Member.cities.bhi;
import com.aryomtech.dhitifoundation.Member.cities.bsp;
import com.aryomtech.dhitifoundation.Member.cities.champ;
import com.aryomtech.dhitifoundation.Member.cities.kor;
import com.aryomtech.dhitifoundation.Member.cities.oth;


import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter_cities extends FragmentStateAdapter {

    public ViewPagerAdapter_cities(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){

            case 1:
                return new bhi();
            case 2:
                return new kor();
            case 3:
                return new champ();
            case 4:
                return new oth();

        }

        return new bsp();
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}
