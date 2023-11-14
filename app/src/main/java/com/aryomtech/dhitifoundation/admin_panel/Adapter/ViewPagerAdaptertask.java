package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aryomtech.dhitifoundation.admin_panel.TasksType.group_tasks_approval;
import com.aryomtech.dhitifoundation.admin_panel.TasksType.private_tasks_approval;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdaptertask  extends FragmentStateAdapter {

    public ViewPagerAdaptertask(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 1) {
            return new group_tasks_approval();
        }

        return new private_tasks_approval();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
