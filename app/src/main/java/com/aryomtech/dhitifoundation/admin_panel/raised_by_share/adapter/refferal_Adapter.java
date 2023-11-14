package com.aryomtech.dhitifoundation.admin_panel.raised_by_share.adapter;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Approve_Forms.Adapter.approve_forms_Adapter;
import com.aryomtech.dhitifoundation.admin_panel.fluid_view_supporters;
import com.aryomtech.dhitifoundation.admin_panel.raised_by_share.show_donation.show_refferal_donation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class refferal_Adapter extends RecyclerView.Adapter<refferal_Adapter.ViewHolder> {

    Context context;
    String key;
    ArrayList<String> list,name_list,dp_list,uid_list;
    public refferal_Adapter(Context context, ArrayList<String> list, ArrayList<String> name_list, ArrayList<String> dp_list, ArrayList<String> uid_list, String key) {
        this.context=context;
        this.list=list;
        this.uid_list=uid_list;
        this.name_list=name_list;
        this.dp_list=dp_list;
        this.key=key;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_refferal,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(name_list.get(position));
        holder.total_raised.setText(list.get(position));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!dp_list.get(position).equals("")){
                try{
                    Glide.with(context).asBitmap()
                            .load(dp_list.get(position))
                            .thumbnail(0.1f)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .override(150,150)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            if(!dp_list.get(position).equals("")){
                try{
                    Glide.with(context).asBitmap()
                            .load(dp_list.get(position))
                            .override(40,40)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        holder.button.setOnClickListener(v->{
            show_refferal_donation show_refferal_donation=new show_refferal_donation();
            Bundle args=new Bundle();
            args.putString("sending_key_789321", key);
            args.putString("sending_uid_6580",uid_list.get(position));
            args.putString("sending_name_6548256",name_list.get(position));
            show_refferal_donation.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, show_refferal_donation)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return name_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView dp;
        TextView name,total_raised,button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView84);
            total_raised=itemView.findViewById(R.id.headdp);
            button=itemView.findViewById(R.id.view);
            dp=itemView.findViewById(R.id.imageView);
        }
    }
}
