package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.GroupTask.Model.groupTaskData;
import com.aryomtech.dhitifoundation.GroupTask.view_group_task;
import com.aryomtech.dhitifoundation.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class group_task_Adapter extends RecyclerView.Adapter<group_task_Adapter.Viewholder>{

    ArrayList<groupTaskData> groupTaskData_list;
    Context context;
    ArrayList<Long> task_List;
    View view;
    public group_task_Adapter(Context context, ArrayList<groupTaskData> groupTaskData_list, ArrayList<Long> list_tasks) {
        this.groupTaskData_list=groupTaskData_list;
        this.context=context;
        this.task_List=list_tasks;
    }
    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_group_task,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        holder.task_title.setText(groupTaskData_list.get(position).getTask_title());
        holder.deadline.setText(groupTaskData_list.get(position).getTask_deadline());
        holder.task_description.setText(groupTaskData_list.get(position).getDescription());
        String txt=task_List.get(position)+" Added Task";
        holder.number_of_task.setText(txt);

        holder.task_card_adapter.setOnClickListener(v->{
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
            args.putString("sending_from","admin_grp_task_approval");
            view_group_task.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_group_task)
                    .addToBackStack(null)
                    .commit();
        });
        if(groupTaskData_list.get(position).getApproved()!=null) {
            if (groupTaskData_list.get(position).getApproved().equals("true")) {
                holder.change_color.setVisibility(View.VISIBLE);
                holder.not_done_card.setVisibility(View.GONE);
            }
        }
        else if ((groupTaskData_list.get(position).getSubmitted().equals("yes"))){
            holder.not_done_card.setVisibility(View.VISIBLE);
            holder.change_color.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return groupTaskData_list.size();
    }


    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView number_of_task,task_title,task_description,deadline;
        LinearLayout task_card_adapter;
        ImageView change_color,not_done_card;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            number_of_task=itemView.findViewById(R.id.number_of_task);
            task_title=itemView.findViewById(R.id.task_title);
            task_description=itemView.findViewById(R.id.task_description);
            deadline=itemView.findViewById(R.id.deadline);
            change_color=itemView.findViewById(R.id.imageView11);
            task_card_adapter=itemView.findViewById(R.id.task_card_adapter);
            not_done_card=itemView.findViewById(R.id.not_done_card);
        }
    }
}
