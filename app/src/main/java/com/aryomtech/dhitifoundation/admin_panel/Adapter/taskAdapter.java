package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.ViewHolder> {

    Context context;
    ArrayList<String> task_list;
    public taskAdapter(Context context, ArrayList<String> task_list) {
        this.context=context;
        this.task_list=task_list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_design_create,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.textView.setText(task_list.get(position));
    }

    @Override
    public int getItemCount() {
        return task_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.task_content);
        }
    }
}
