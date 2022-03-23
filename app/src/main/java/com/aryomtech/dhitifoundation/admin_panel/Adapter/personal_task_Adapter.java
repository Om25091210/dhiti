package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.privateTask.Model.privateTaskData;
import com.aryomtech.dhitifoundation.privateTask.view_private_task;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class personal_task_Adapter extends RecyclerView.Adapter<personal_task_Adapter.Viewholder>{

    ArrayList<privateTaskData> privateTaskData_list;
    Context context;
    ArrayList<Long> task_List;
    View view;
    public personal_task_Adapter(Context context, ArrayList<privateTaskData> privateTaskData_list, ArrayList<Long> list_tasks) {
        this.privateTaskData_list=privateTaskData_list;
        this.context=context;
        this.task_List=list_tasks;
    }
    @NonNull
    @NotNull
    @Override
    public personal_task_Adapter.Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_personal_task,parent,false);
        return new personal_task_Adapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull personal_task_Adapter.Viewholder holder, int position) {
        holder.task_title.setText(privateTaskData_list.get(position).getTask_title());
        holder.deadline.setText(privateTaskData_list.get(position).getTask_deadline());
        holder.task_description.setText(privateTaskData_list.get(position).getDescription());
        String txt=task_List.get(position)+" Added Task";
        holder.number_of_task.setText(txt);

        holder.task_card_adapter.setOnClickListener(v->{
            view_private_task view_private_task=new view_private_task();
            Bundle args=new Bundle();
            args.putString("private_task_title",privateTaskData_list.get(position).getTask_title());
            args.putString("private_task_deadline",privateTaskData_list.get(position).getTask_deadline());
            args.putString("private_task_description",privateTaskData_list.get(position).getDescription());
            args.putString("private_task_given_on",privateTaskData_list.get(position).getGiven_on());
            args.putString("private_task_image_link",privateTaskData_list.get(position).getImage_link());
            args.putString("private_task_key",privateTaskData_list.get(position).getKey());
            args.putString("private_task_status",privateTaskData_list.get(position).getStatus());
            args.putString("private_task_added_task_count",task_List.get(position)+" Added Task");
            args.putString("private_task_submitted_or_not",privateTaskData_list.get(position).getSubmitted());
            args.putString("private_task_approved_or_not",privateTaskData_list.get(position).getApproved());
            args.putString("private_task_uid010",privateTaskData_list.get(position).getUid());
            args.putString("approving_private_task","Approving");
            view_private_task.setArguments(args);


            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_private_task)
                    .addToBackStack(null)
                    .commit();
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(privateTaskData_list.get(position).getDp()!=null){
                try{
                    Glide.with(context).asBitmap()
                            .load(privateTaskData_list.get(position).getDp())
                            .thumbnail(0.1f)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .override(100,100)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            if(privateTaskData_list.get(position).getDp()!=null){
                try{
                    Glide.with(context).asBitmap()
                            .load(privateTaskData_list.get(position).getDp())
                            .override(40,40)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (privateTaskData_list.get(position).getApproved()!=null) {
            if (privateTaskData_list.get(position).getApproved().equals("true")) {
                holder.change_color.setVisibility(View.VISIBLE);
                holder.not_done_card.setVisibility(View.GONE);
            } else {
                holder.not_done_card.setVisibility(View.VISIBLE);
                holder.change_color.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return privateTaskData_list.size();
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull Viewholder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.dp);
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView number_of_task,task_title,task_description,deadline;
        CircleImageView dp;
        LinearLayout task_card_adapter;
        ImageView change_color,not_done_card;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            number_of_task=itemView.findViewById(R.id.number_of_task);
            task_title=itemView.findViewById(R.id.task_title);
            task_description=itemView.findViewById(R.id.task_description);
            deadline=itemView.findViewById(R.id.deadline);
            dp=itemView.findViewById(R.id.profile_image);
            change_color=itemView.findViewById(R.id.imageView11);
            task_card_adapter=itemView.findViewById(R.id.task_card_adapter);
            not_done_card=itemView.findViewById(R.id.not_done_card);
        }
    }
}
