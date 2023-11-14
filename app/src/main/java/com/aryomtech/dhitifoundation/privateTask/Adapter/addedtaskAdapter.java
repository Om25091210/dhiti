package com.aryomtech.dhitifoundation.privateTask.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class addedtaskAdapter extends RecyclerView.Adapter<addedtaskAdapter.Viewholder>{


    View view;
    Context context;
    ArrayList added_task_list;
    String color;
    public addedtaskAdapter(Context context, ArrayList added_task_list,String color) {
        this.context=context;
        this.added_task_list=added_task_list;
        this.color=color;
    }

    @NonNull
    @NotNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.added_task_content,parent,false);
        return new addedtaskAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
            String added_text=added_task_list.get(position)+"";
            holder.task.setText(added_text);
            holder.task.setOnClickListener(v->{
                // if the text is not having strike then set strike else vice versa
                if (!holder.task.getPaint().isStrikeThruText()) {
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.card.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.veryLightGrey, null));
                } else {
                    holder.task.setPaintFlags(holder.task.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.card.setCardBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, null));
                }
            });
            if(color.equals("yellow"))
                holder.task.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ff9800")));
            else
                holder.task.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#669900")));
    }

    @Override
    public int getItemCount() {
        return added_task_list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        CheckBox task;
        CardView card;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            task=itemView.findViewById(R.id.todoCheckBox);
            card=itemView.findViewById(R.id.card);
        }
    }
}
