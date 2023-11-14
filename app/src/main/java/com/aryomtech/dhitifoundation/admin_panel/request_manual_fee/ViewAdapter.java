package com.aryomtech.dhitifoundation.admin_panel.request_manual_fee;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city.bhi_pay;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city.bsp_pay;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city.chp_pay;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city.kor_pay;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.city.other_pay;

import org.jetbrains.annotations.NotNull;

public class ViewAdapter extends FragmentStateAdapter {

    public ViewAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){

            case 1:
                return new bhi_pay();
            case 2:
                return new kor_pay();
            case 3:
                return new chp_pay();
            case 4:
                return new other_pay();

        }

        return new bsp_pay();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
