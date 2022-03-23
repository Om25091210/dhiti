package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Create_ManageFund;
import com.aryomtech.dhitifoundation.admin_panel.fluid_Overview;
import com.aryomtech.dhitifoundation.admin_panel.model.fluid_Cards_Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;

public class edit_fluid_card_Adapter extends RecyclerView.Adapter<edit_fluid_card_Adapter.ViewHolder>{

    Context context;
    ArrayList<fluid_Cards_Data> list;
    FirebaseAuth auth;
    String identity="";
    FirebaseUser user;
    public edit_fluid_card_Adapter(Context context, ArrayList<fluid_Cards_Data> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_edit_fluid_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        identity=context.getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");

        holder.title.setText(list.get(position).getTitle());
        holder.message.setText(list.get(position).getDate_or_msg());
        holder.location.setText(list.get(position).getLocation());
        if(list.get(position).getDonation().equals("disabled"))
            holder.linearLayout.setVisibility(View.GONE);
        else {
            holder.linearLayout.setVisibility(View.VISIBLE);
            String text="₹ "+list.get(position).getTarget();
            holder.target.setText(text);
            String text1="₹ "+list.get(position).getContributed();
            holder.contributed.setText(text1);
        }
        holder.card_grp.setOnClickListener(v->{
            if (list.get(position).getUid().equals(user.getUid()) || identity.equals("admin")) {
                Create_ManageFund create_manageFund = new Create_ManageFund();
                Bundle args = new Bundle();
                args.putString("sending_image", list.get(position).getImage_link());
                args.putString("sending_title", list.get(position).getTitle());
                args.putString("check_for_donation", list.get(position).getDonation());
                args.putString("sending_location", list.get(position).getLocation());
                args.putString("sending_date-or-msg", list.get(position).getDate_or_msg());
                args.putString("sending_target_amount", list.get(position).getTarget());
                args.putString("sending_contributed", list.get(position).getContributed());
                args.putString("sending_key", list.get(position).getKey());
                args.putString("sending_goal", list.get(position).getGoal());
                args.putString("sending_uid_of_creator", list.get(position).getUid());
                args.putString("sending_sub_heading_101", list.get(position).getSub_heading_1());
                args.putString("sending_sub_heading_202", list.get(position).getSub_heading_2());
                args.putString("sending_content_101", list.get(position).getContent_1());
                args.putString("sending_content_202", list.get(position).getContent_2());
                create_manageFund.setArguments(args);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer, create_manageFund)
                        .addToBackStack(null)
                        .commit();

            }
            else{
                MotionToast.Companion.darkColorToast((Activity) context,
                        "Access Denied",
                        "Only the creator of this card can make changes.",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context,R.font.helvetica_regular));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,message,location,contributed,target;
        LinearLayout  linearLayout;
        ConstraintLayout card_grp;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.sampleTextView);
            contributed=itemView.findViewById(R.id.textView98);
            target=itemView.findViewById(R.id.textView109);
            location=itemView.findViewById(R.id.textViewBottom2);
            message=itemView.findViewById(R.id.textView85);
            linearLayout=itemView.findViewById(R.id.linearLayout11);
            card_grp=itemView.findViewById(R.id.card_grp);
        }
    }
}
