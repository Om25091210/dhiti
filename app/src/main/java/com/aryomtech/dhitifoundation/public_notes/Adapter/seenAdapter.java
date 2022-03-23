package com.aryomtech.dhitifoundation.public_notes.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class seenAdapter extends RecyclerView.Adapter<seenAdapter.MyViewHolder> {

    ArrayList<String> dp_list;
    ArrayList<String> name_list;
    ArrayList<String> time_list;
    Context context;
    public seenAdapter(ArrayList<String> seen_people_pictures, Context view_note, ArrayList<String> seen_people_name
            , ArrayList<String> seen_people_time) {
        this.dp_list=seen_people_pictures;
        this.context=view_note;
        this.name_list=seen_people_name;
        this.time_list=seen_people_time;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seen_content,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Glide.with(context)
                        .asBitmap()
                        .load(dp_list.get(position))
                        .override(80, 80)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(holder.dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                Glide.with(context)
                        .asBitmap()
                        .load(dp_list.get(position))
                        .override(40, 40)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(holder.dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.name.setText(name_list.get(position));
        holder.time.setText(time_list.get(position));

    }

    @Override
    public int getItemCount() {
        return name_list.size();
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.dp);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView dp;
        TextView name,time;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.name_seen);
            dp=itemView.findViewById(R.id.dp_seen);
            time=itemView.findViewById(R.id.textView56);

        }
    }
}
