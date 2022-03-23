package com.aryomtech.dhitifoundation.admin_panel;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.Profile.profile;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Approve_Forms.Adapter.approve_forms;
import com.aryomtech.dhitifoundation.admin_panel.Files.track_files;
import com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.tab_join_request;
import com.aryomtech.dhitifoundation.admin_panel.request_manual_fee.manual_request;
import com.aryomtech.dhitifoundation.admin_panel.set_slider.slider_card;
import com.aryomtech.dhitifoundation.admin_panel.set_slider.slider_list;

import java.util.Objects;

public class Control_panel extends Fragment {

    View view;
    String identity="";
    private Context contextNullSafe;
    CardView private_task,group_task,approve_task,control_event,approve_member,form_approval,doc_card,slider_card,manual_card;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_control_panel, container, false);
        doc_card=view.findViewById(R.id.doc_card);
        doc_card.setVisibility(View.GONE);
        if (contextNullSafe == null) getContextNullSafety();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));
        window.setNavigationBarColor(ContextCompat.getColor(getContextNullSafety(), R.color.veryLightGrey));

        identity=getContextNullSafety().getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        if(identity.equals("admin"))
            doc_card.setVisibility(View.VISIBLE);

        doc_card.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new track_files())
                    .addToBackStack(null)
                    .commit();
        });

        approve_member=view.findViewById(R.id.approve_member);
        approve_member.setOnClickListener(v->{
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new tab_join_request())
                    .addToBackStack(null)
                    .commit();
        });

        private_task=view.findViewById(R.id.private_task);
        private_task.setOnClickListener(v->{

            getContextNullSafety().getSharedPreferences("mode_of_task_preference", Context.MODE_PRIVATE).edit()
                    .putString("789_mode_of_task","private task").apply();

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new members_list())
                    .addToBackStack(null)
                    .commit();
        });
        group_task=view.findViewById(R.id.group_task);
        group_task.setOnClickListener(v->{

            getContextNullSafety().getSharedPreferences("mode_of_task_preference", Context.MODE_PRIVATE).edit()
                    .putString("789_mode_of_task","group task").apply();

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,new members_list())
                    .addToBackStack(null)
                    .commit();
        });
        approve_task=view.findViewById(R.id.approve_task);
        approve_task.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new Approve_tasks())
                .addToBackStack(null)
                .commit());
        control_event=view.findViewById(R.id.control_event);
        control_event.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new ManageFunds())
                        .addToBackStack(null)
                        .commit());
        form_approval=view.findViewById(R.id.form_approval);
        form_approval.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new approve_forms())
                .addToBackStack(null)
                .commit());

        slider_card=view.findViewById(R.id.slider_card);
        slider_card.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.drawer,new slider_list())
                .addToBackStack(null)
                .commit());

        manual_card=view.findViewById(R.id.manual_card);
        manual_card.setOnClickListener(v->
                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,new manual_request())
                        .addToBackStack(null)
                        .commit());


        view.findViewById(R.id.imageBack).setOnClickListener(v->onback());
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
                        .replace(R.id.container,new profile())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
        return view;
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
    private void onback() {
        if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer) != null) {
            ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                    .beginTransaction().
                    remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.drawer))).commit();
        }
        ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,new profile())
                .commit();
    }

}