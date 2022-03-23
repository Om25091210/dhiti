package com.aryomtech.dhitifoundation.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.fluid_Overview;
import com.aryomtech.dhitifoundation.admin_panel.model.fluid_Cards_Data;
import com.aryomtech.dhitifoundation.donation_checkout;
import com.aryomtech.dhitifoundation.privateTask.ProgressBarAnimation;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;

public class CardAdapterClass extends RecyclerView.Adapter<CardAdapterClass.MyViewHolder>{

    ArrayList<fluid_Cards_Data> list;
    ArrayList<String> total_amount_list;
    Context context;
    View view;

    public CardAdapterClass(ArrayList<fluid_Cards_Data> list, Context context,ArrayList<String> total_amount_list) {
        this.list = list;
        this.context = context;
        this.total_amount_list = total_amount_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_fluid_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Fresco.initialize(
                context,
                ImagePipelineConfig.newBuilder(context)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        Uri uri = Uri.parse(list.get(position).getImage_link());
        ImageRequest request = ImageRequest.fromUri(uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.draweeView.getController()).build();

        holder.draweeView.setController(controller);

        holder.title.setText(list.get(position).getTitle());
        holder.deadline_date.setText(list.get(position).getDate_or_msg());
        holder.textViewBottom2.setText(list.get(position).getLocation());
        if(list.get(position).getDonation().equals("disabled")) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.donate.setVisibility(View.GONE);
        }
        else {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.donate.setVisibility(View.VISIBLE);
            String text="₹ "+list.get(position).getTarget();
            holder.target.setText(text);
            if(total_amount_list!=null){
                holder.contributed.setText(total_amount_list.get(position));
            }
            else {
                String text1 = "₹ " + list.get(position).getContributed();
                holder.contributed.setText(text1);
            }
        }

        if (list.get(position).getTarget()!=null && list.get(position).getContributed()!=null) {
            if (total_amount_list != null) {
                float per = (float) (Integer.parseInt(total_amount_list.get(position)) * 100) / Integer.parseInt(list.get(position).getTarget());
                ProgressBarAnimation anim = new ProgressBarAnimation(holder.progressBar, 0f, per);
                anim.setDuration(1000);
                holder.progressBar.startAnimation(anim);
            } else {
                float per = (float) (Integer.parseInt(list.get(position).getContributed()) * 100) / Integer.parseInt(list.get(position).getTarget());
                ProgressBarAnimation anim = new ProgressBarAnimation(holder.progressBar, 0f, per);
                anim.setDuration(1000);
                holder.progressBar.startAnimation(anim);
            }
        }
        else {
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.overview.setOnClickListener(v->{
            fluid_Overview fluid_overview=new fluid_Overview();
            Bundle args=new Bundle();
            args.putString("sending_key", list.get(position).getKey());
            fluid_overview.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, fluid_overview)
                    .addToBackStack(null)
                    .commit();
        });

        holder.donate.setOnClickListener(v->{
            donation_checkout donation_checkout=new donation_checkout();
            Bundle args=new Bundle();
            args.putString("sending_key", list.get(position).getKey());
            donation_checkout.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, donation_checkout)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        //ImageView photo;
        TextView textViewBottom2,total,deadline_date;
        TextView title,contributed,target;
        LinearLayout linearLayout;
        CardView donate;
        RelativeLayout overview;
        SimpleDraweeView draweeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //photo=itemView.findViewById(R.id.sampleImageView);
            draweeView=itemView.findViewById(R.id.sampleImageView);
            progressBar=itemView.findViewById(R.id.progressBar);
            textViewBottom2=itemView.findViewById(R.id.textViewBottom2);
            total=itemView.findViewById(R.id.total);
            deadline_date=itemView.findViewById(R.id.deadline_date);
            title=itemView.findViewById(R.id.sampleTextView);
            contributed=itemView.findViewById(R.id.textView98);
            target=itemView.findViewById(R.id.textView109);
            donate=itemView.findViewById(R.id.donate);
            linearLayout=itemView.findViewById(R.id.linearLayout11);
            overview=itemView.findViewById(R.id.overview);
        }
    }
}
