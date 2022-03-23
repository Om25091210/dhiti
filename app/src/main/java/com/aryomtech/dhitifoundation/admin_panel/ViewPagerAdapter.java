package com.aryomtech.dhitifoundation.admin_panel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aryomtech.dhitifoundation.cities.bhilai;
import com.aryomtech.dhitifoundation.cities.bilaspur;
import com.aryomtech.dhitifoundation.cities.champa;
import com.aryomtech.dhitifoundation.cities.korba;
import com.aryomtech.dhitifoundation.cities.other;

import org.jetbrains.annotations.NotNull;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){

            case 1:
                return new bhilai();
            case 2:
                return new korba();
            case 3:
                return new champa();
            case 4:
                return new other();

        }

        return new bilaspur();
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}
