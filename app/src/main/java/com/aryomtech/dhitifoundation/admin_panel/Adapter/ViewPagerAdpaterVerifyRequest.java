package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.aryomtech.dhitifoundation.admin_panel.TasksType.group_tasks_approval;
import com.aryomtech.dhitifoundation.admin_panel.TasksType.private_tasks_approval;
import com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.RequestType.chapter_head_request;
import com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.RequestType.member_request;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdpaterVerifyRequest extends FragmentStateAdapter {

    public ViewPagerAdpaterVerifyRequest(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new chapter_head_request();
        }

        return new member_request();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
