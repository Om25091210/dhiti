package com.aryomtech.dhitifoundation.public_notes.Adapter;

import android.content.Context;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.public_notes.model.note_model;
import com.aryomtech.dhitifoundation.public_notes.view_announcement;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnnouncementAdapterClass extends RecyclerView.Adapter<AnnouncementAdapterClass.MyViewHolderblood>{

    ArrayList<note_model> list;
    Context context;
    String key_pin1,key_pin2,key_pin3;
    public AnnouncementAdapterClass(ArrayList<note_model> list, Context pub_notes) {
        this.list = list;
        this.context = pub_notes;
    }

    @NonNull
    @Override
    public MyViewHolderblood onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_announcement,parent,false);
        return new MyViewHolderblood(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderblood holder, int position) {
        key_pin1=context.getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE)
                .getString("pin_1_announcement","");
        key_pin2=context.getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE)
                .getString("pin_2_announcement","");
        key_pin3=context.getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE)
                .getString("pin_3_announcement","");

        holder.mainlayout.setOnClickListener(v->{
            view_announcement view_announcement=new view_announcement();
            Bundle args=new Bundle();
            args.putString("category0101",list.get(position).getCategory());
            args.putString("city0202",list.get(position).getCity());
            args.putString("content0303",list.get(position).getContent());
            args.putString("date0404",list.get(position).getDate());
            args.putString("head0505",list.get(position).getHead());
            args.putString("imagelink0606",list.get(position).getImagelink());
            args.putString("pdflink0707",list.get(position).getPdflink());
            args.putString("writtenby0808",list.get(position).getWrittenby());
            args.putString("url0909",list.get(position).getUrl());
            args.putString("an_push_key_1010",list.get(position).getAn_push_key());
            args.putString("uid_2020",list.get(position).getUid());
            args.putString("profile_dp_3030",list.get(position).getDplink());
            view_announcement.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_announcement)
                    .addToBackStack(null)
                    .commit();

        });

        if(key_pin1.equals(list.get(position).getAn_push_key()) || key_pin2.equals(list.get(position).getAn_push_key()) || key_pin3.equals(list.get(position).getAn_push_key())){
            holder.pin.setVisibility(View.VISIBLE);
        }
        else{
            holder.pin.setVisibility(View.GONE);
        }
        holder.mainlayout.setOnLongClickListener(v -> {
            go_to_data(position);
            return false;
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try{
                Glide.with(context).asBitmap()
                        .load(list.get(position).getDplink())
                        .thumbnail(0.1f)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .override(150,150)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .into(holder.profile_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try{
                Glide.with(context).asBitmap()
                        .load(list.get(position).getDplink())
                        .override(40,40)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .into(holder.profile_image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.name.setText(list.get(position).getWrittenby());
        holder.date.setText(list.get(position).getDate());
        if(list.get(position).getCategory().length()>15){
            String sub=list.get(position).getCategory().substring(0,13)+"...";
            holder.category.setText(sub);
        }
        else {
            holder.category.setText(list.get(position).getCategory());
        }
        holder.city.setText(list.get(position).getCity());
        holder.content.setText(list.get(position).getContent());
        if(list.get(position).getPdflink()!=null){
            holder.pdf.setVisibility(View.VISIBLE);
        }
        else{
            holder.pdf.setVisibility(View.GONE);
        }
        if (list.get(position).getImagelink()!=null){
            holder.image.setVisibility(View.VISIBLE);
        }
        else{
            holder.image.setVisibility(View.GONE);
        }

    }

    private void go_to_data(int position) {

        if(key_pin1.equals("")
                && !list.get(position).getAn_push_key().equals(key_pin2)
                && !list.get(position).getAn_push_key().equals(key_pin3)){
            list.add(0,list.get(position));
            list.remove(position+1);
            notifyDataSetChanged();
            context.getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE).edit()
                    .putString("pin_1_announcement",list.get(0).getAn_push_key()).apply();
            Toast.makeText(context, "Announcement pinned.", Toast.LENGTH_SHORT).show();
        }
        else if(key_pin2.equals("")
                && !list.get(position).getAn_push_key().equals(key_pin3)
                && !list.get(position).getAn_push_key().equals(key_pin1)){
            list.add(0,list.get(position));
            list.remove(position+1);
            notifyDataSetChanged();
            context.getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE).edit()
                    .putString("pin_2_announcement",list.get(0).getAn_push_key()).apply();
            Toast.makeText(context, "Announcement pinned.", Toast.LENGTH_SHORT).show();
        }
        else if(key_pin3.equals("")
                && !list.get(position).getAn_push_key().equals(key_pin1)
                && !list.get(position).getAn_push_key().equals(key_pin2)){
            list.add(0,list.get(position));
            list.remove(position+1);
            notifyDataSetChanged();
            context.getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE).edit()
                    .putString("pin_3_announcement",list.get(0).getAn_push_key()).apply();
            Toast.makeText(context, "Announcement pinned.", Toast.LENGTH_SHORT).show();
        }
        else if(!list.get(position).getAn_push_key().equals(key_pin1)
                && !list.get(position).getAn_push_key().equals(key_pin2)
                && !list.get(position).getAn_push_key().equals(key_pin3)){
            Toast.makeText(context,"can only pin 3 items.",Toast.LENGTH_SHORT).show();
        }
        else if(list.get(position).getAn_push_key().equals(key_pin1)){
            notifyDataSetChanged();
            key_pin1="";
            context.getSharedPreferences("AnnouncementAdapterClass_1",Context.MODE_PRIVATE).edit()
                    .putString("pin_1_announcement",key_pin1).apply();
            Toast.makeText(context, "Announcement unpinned.", Toast.LENGTH_SHORT).show();
        }
        else if(list.get(position).getAn_push_key().equals(key_pin2)){
            notifyDataSetChanged();
            key_pin2="";
            context.getSharedPreferences("AnnouncementAdapterClass_2",Context.MODE_PRIVATE).edit()
                    .putString("pin_2_announcement",key_pin2).apply();
            Toast.makeText(context, "Announcement unpinned.", Toast.LENGTH_SHORT).show();
        }
        else if(list.get(position).getAn_push_key().equals(key_pin3)){
            notifyDataSetChanged();
            key_pin3="";
            context.getSharedPreferences("AnnouncementAdapterClass_3",Context.MODE_PRIVATE).edit()
                    .putString("pin_3_announcement",key_pin3).apply();
            Toast.makeText(context, "Announcement unpinned.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull MyViewHolderblood holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.profile_image);
    }

    static class MyViewHolderblood extends RecyclerView.ViewHolder {

        TextView name,date,category,city,content,pdf,image;
        ConstraintLayout mainlayout;
        CircleImageView profile_image;
        ImageView pin;
        public MyViewHolderblood(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView28);
            date=itemView.findViewById(R.id.textView29);
            category=itemView.findViewById(R.id.textView31);
            city=itemView.findViewById(R.id.textView36);
            content=itemView.findViewById(R.id.textView32);
            pdf=itemView.findViewById(R.id.textView34);
            image=itemView.findViewById(R.id.textView35);
            mainlayout=itemView.findViewById(R.id.mainlayout);
            profile_image=itemView.findViewById(R.id.profile_image);
            pin=itemView.findViewById(R.id.imageView8);
        }
    }

}
