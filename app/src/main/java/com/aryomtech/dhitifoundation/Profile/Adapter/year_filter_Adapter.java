package com.aryomtech.dhitifoundation.Profile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.cities.onClickInterface;

import java.util.ArrayList;
import java.util.List;

public class year_filter_Adapter extends RecyclerView.Adapter<year_filter_Adapter.ViewHolder> {

    List<String> list;
    Context context;
    int previous_position;
    onClickInterface onClick;
    public year_filter_Adapter(Context contextNullSafety, ArrayList<String> year_list,onClickInterface onClickInterface) {
        this.context=contextNullSafety;
        this.list=year_list;
        this.onClick=onClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.year_list_mem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.date.setText(list.get(position));
        holder.date.setOnClickListener(v-> {
            onClick.setClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.textView141);
        }
    }
}
