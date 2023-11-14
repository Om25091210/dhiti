package com.aryomtech.dhitifoundation.privateTask.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.Adapter.CardAdapterClass;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.privateTask.Model.privateTaskData;
import com.aryomtech.dhitifoundation.privateTask.view_private_task;
import com.aryomtech.dhitifoundation.public_notes.view_announcement;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class privatetaskAdapter extends RecyclerView.Adapter<privatetaskAdapter.Viewholder> {

    ArrayList<privateTaskData> privateTaskData_list;
    Context context;
    ArrayList<Long> task_List;
    View view;

    public privatetaskAdapter(Context context, ArrayList<privateTaskData> privateTaskData_list, ArrayList<Long> list_tasks) {
        this.privateTaskData_list=privateTaskData_list;
        this.context=context;
        this.task_List=list_tasks;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_task_card,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {

        holder.task_title.setText(privateTaskData_list.get(position).getTask_title());
        holder.deadline.setText(privateTaskData_list.get(position).getTask_deadline());
        holder.task_description.setText(privateTaskData_list.get(position).getDescription());
        String txt=task_List.get(position)+" Task";
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
            args.putString("private_task_creator",privateTaskData_list.get(position).getCreator());
            view_private_task.setArguments(args);


            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_private_task)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return privateTaskData_list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView number_of_task,task_title,task_description,deadline;
        LinearLayout task_card_adapter;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            number_of_task=itemView.findViewById(R.id.number_of_task);
            task_title=itemView.findViewById(R.id.task_title);
            task_description=itemView.findViewById(R.id.task_description);
            deadline=itemView.findViewById(R.id.deadline);
            task_card_adapter=itemView.findViewById(R.id.task_card_adapter);
        }
    }
}
