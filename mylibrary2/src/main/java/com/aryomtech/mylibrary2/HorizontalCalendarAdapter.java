package com.aryomtech.mylibrary2;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.MyViewHolder> {
    private final ArrayList<HorizontalCalendarModel> list;
    private final Context mCtx;
    private HorizontalCalendarView.OnCalendarListener onCalendarListener;

    public void setOnCalendarListener(HorizontalCalendarView.OnCalendarListener onCalendarListener) {
        this.onCalendarListener = onCalendarListener;
    }

    public HorizontalCalendarAdapter(ArrayList<HorizontalCalendarModel> list, Context mCtx) {
        this.list = list;
        this.mCtx = mCtx;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date,month,day;
        LinearLayout parent;

        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            month = view.findViewById(R.id.month);
            parent = view.findViewById(R.id.parent);
            day = view.findViewById(R.id.day);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_calendar_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final HorizontalCalendarModel model = list.get(position);

        Display display = ((Activity)mCtx).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;

        holder.parent.setMinimumWidth(Math.round(width/7));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM EEE", Locale.getDefault());
        final SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        holder.date.setText(sdf.format(model.getTimeinmilli()).split(" ")[0]);
        holder.month.setText(sdf.format(model.getTimeinmilli()).split(" ")[1]);
        holder.day.setText(sdf.format(model.getTimeinmilli()).split(" ")[2]);

        if(model.getStatus()==0){
            holder.date.setTextColor(mCtx.getColor(R.color.red_500));
            holder.month.setTextColor(mCtx.getColor(R.color.red_500));
            holder.day.setTextColor(mCtx.getColor(R.color.red_500));
            holder.parent.setBackgroundColor(Color.TRANSPARENT);
        }else{
            holder.date.setTextColor(mCtx.getColor(R.color.blue_grey_900));
            holder.month.setTextColor(mCtx.getColor(R.color.blue_grey_900));
            holder.day.setTextColor(mCtx.getColor(R.color.blue_grey_900));
            holder.parent.setBackgroundResource(R.drawable.color_status_1);
        }

        holder.parent.setOnClickListener(view -> {
            try {
                onCalendarListener.onDateSelected(sdf1.format(model.getTimeinmilli()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
