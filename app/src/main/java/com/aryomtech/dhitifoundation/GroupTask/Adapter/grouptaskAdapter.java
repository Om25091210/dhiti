package com.aryomtech.dhitifoundation.GroupTask.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.GroupTask.Model.groupTaskData;
import com.aryomtech.dhitifoundation.GroupTask.view_group_task;
import com.aryomtech.dhitifoundation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class grouptaskAdapter extends RecyclerView.Adapter<grouptaskAdapter.ViewHolder> {

    ArrayList<groupTaskData> groupTaskData_list;
    ArrayList<Long> list_tasks;
    ArrayList<Long> membership_list;
    Context context;
    ArrayList<String> names;
    String searched;
    public grouptaskAdapter(Context context, ArrayList<groupTaskData> groupTaskData_list, ArrayList<Long> list_tasks, ArrayList<Long> membership_list
            , ArrayList<String> names,String searched) {
        this.context=context;
        this.groupTaskData_list=groupTaskData_list;
        this.list_tasks=list_tasks;
        this.membership_list=membership_list;
        this.names=names;
        this.searched=searched;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_grp_task,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.title.setText(groupTaskData_list.get(position).getTask_title());
        holder.date.setText(groupTaskData_list.get(position).getGiven_on());
        String total_member_txt=membership_list.get(position)+" people";
        holder.total_member.setText(total_member_txt);
        holder.date.setText(groupTaskData_list.get(position).getGiven_on());
        if(groupTaskData_list.get(position).getState().equals("public"))
            holder.join.setVisibility(View.VISIBLE);
        else if (groupTaskData_list.get(position).getState().equals("private"))
            holder.join.setVisibility(View.GONE);
        String arrayList=names.get(position);
        List<String> list = new ArrayList<>();

        StringTokenizer token = new StringTokenizer(arrayList, ",");
        while (token.hasMoreTokens()) {
            String newString = token.nextToken().replace("]", "");
            String newStr=newString.replace("[","");
            list.add(newStr.toUpperCase().trim());
        }
        if(searched.equals("yes")){
            holder.head1.setVisibility(View.GONE);
            holder.head2.setVisibility(View.GONE);
            holder.head3.setVisibility(View.GONE);
            holder.plus.setVisibility(View.GONE);
            holder.des.setVisibility(View.VISIBLE);
            holder.des.setText(groupTaskData_list.get(position).getDescription());
            String te="Deadline : "+groupTaskData_list.get(position).getTask_deadline();
            holder.total_member.setText(te);
        }
        else if(searched.equals("no")){
            holder.head1.setVisibility(View.VISIBLE);
            holder.head2.setVisibility(View.VISIBLE);
            holder.head3.setVisibility(View.VISIBLE);
            holder.plus.setVisibility(View.VISIBLE);
            holder.des.setVisibility(View.GONE);
            if(list.size()>2){
                String s1=list.get(0).charAt(0)+"";
                String s2=list.get(1).charAt(0)+"";
                String s3=list.get(2).charAt(0)+"";
                holder.head1.setText(s1);
                holder.head2.setText(s2);
                holder.head3.setText(s3);
            }
            else {
                String s1=list.get(0).charAt(0)+"";
                String s2=list.get(1).charAt(0)+"";
                holder.head1.setText(s1);
                holder.head2.setText(s2);
                holder.head3.setVisibility(View.GONE);
            }
            if (list.size()>3)
                holder.plus.setVisibility(View.VISIBLE);
            else
                holder.plus.setVisibility(View.GONE);
        }

        holder.card.setOnClickListener(v->{
            switch (groupTaskData_list.get(position).getStatus()) {
                case "ongoing": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#FA9744"));
                    break;
                }
                case "finished": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.BLUE);
                    break;
                }
                case "expired": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.RED);
                    break;
                }
            }
            view_group_task view_group_task=new view_group_task();
            Bundle args=new Bundle();
            args.putString("sending_city",groupTaskData_list.get(position).getCity());
            args.putString("sending_description",groupTaskData_list.get(position).getDescription());
            args.putString("sending_given_on",groupTaskData_list.get(position).getGiven_on());
            args.putString("sending_head",groupTaskData_list.get(position).getHead());
            args.putString("sending_imagelink",groupTaskData_list.get(position).getImage_link());
            args.putString("sending_key",groupTaskData_list.get(position).getKey());
            args.putString("sending_location",groupTaskData_list.get(position).getLocation());
            args.putString("sending_state",groupTaskData_list.get(position).getState());
            args.putString("sending_status",groupTaskData_list.get(position).getStatus());
            args.putString("sending_task_deadline",groupTaskData_list.get(position).getTask_deadline());
            args.putString("sending_task_title",groupTaskData_list.get(position).getTask_title());
            args.putString("sending_task_creator",groupTaskData_list.get(position).getCreator());
            args.putString("sending_from","group_section");
            view_group_task.setArguments(args);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                        .add(R.id.drawer,view_group_task)
                        .addToBackStack(null)
                        .commit();
        });
        holder.join.setOnClickListener(v->{
            switch (groupTaskData_list.get(position).getStatus()) {
                case "ongoing": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#FA9744"));
                    break;
                }
                case "finished": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.BLUE);
                    break;
                }
                case "expired": {
                    Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_indication);
                    assert unwrappedDrawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                    DrawableCompat.setTint(wrappedDrawable, Color.RED);
                    break;
                }
            }
            view_group_task view_group_task=new view_group_task();
            Bundle args=new Bundle();
            args.putString("sending_city",groupTaskData_list.get(position).getCity());
            args.putString("sending_description",groupTaskData_list.get(position).getDescription());
            args.putString("sending_given_on",groupTaskData_list.get(position).getGiven_on());
            args.putString("sending_head",groupTaskData_list.get(position).getHead());
            args.putString("sending_imagelink",groupTaskData_list.get(position).getImage_link());
            args.putString("sending_key",groupTaskData_list.get(position).getKey());
            args.putString("sending_location",groupTaskData_list.get(position).getLocation());
            args.putString("sending_state",groupTaskData_list.get(position).getState());
            args.putString("sending_status",groupTaskData_list.get(position).getStatus());
            args.putString("sending_task_deadline",groupTaskData_list.get(position).getTask_deadline());
            args.putString("sending_task_title",groupTaskData_list.get(position).getTask_title());
            args.putString("sending_from","group_section");
            args.putString("joining_request","joining_request");
            args.putString("submitted_or_not",groupTaskData_list.get(position).getSubmitted());
            view_group_task.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_group_task)
                    .addToBackStack(null)
                    .commit();
        });
        switch (groupTaskData_list.get(position).getStatus()) {
            case "ongoing": {
                holder.view.setVisibility(View.GONE);
                break;
            }
            case "finished": {
                holder.view.setVisibility(View.VISIBLE);
                holder.join.setVisibility(View.GONE);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupTaskData_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,date,total_member,join,head1,head2,head3,plus,des,view;
        ConstraintLayout card;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.textView84);
            total_member=itemView.findViewById(R.id.textView86);
            date=itemView.findViewById(R.id.textView85);
            join=itemView.findViewById(R.id.textView87);
            head1=itemView.findViewById(R.id.head1);
            head2=itemView.findViewById(R.id.head2);
            head3=itemView.findViewById(R.id.head3);
            plus=itemView.findViewById(R.id.plus);
            des=itemView.findViewById(R.id.textView91);
            card=itemView.findViewById(R.id.card_grp);
            view=itemView.findViewById(R.id.view);
        }
    }
}
