package com.aryomtech.dhitifoundation.Profile.membership_payments.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.Profile.membership_payments.show_transactions.mem_transactions;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.raised_by_share.show_donation.show_refferal_donation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.ArrayList;

public class membersAdapter extends RecyclerView.Adapter<membersAdapter.ViewHolder> {
    Context context;
    ArrayList<String> list,name_list,dp_list,uid_list;

    public membersAdapter(Context context, ArrayList<String> list, ArrayList<String> name_list, ArrayList<String> dp_list, ArrayList<String> uid_list) {
        this.context=context;
        this.list=list;
        this.uid_list=uid_list;
        this.name_list=name_list;
        this.dp_list=dp_list;
        Fresco.initialize(
                context,
                ImagePipelineConfig.newBuilder(context)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
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
        if(dp_list.get(position)!=null) {
            Uri uri = Uri.parse(dp_list.get(position));
            ImageRequest request = ImageRequest.fromUri(uri);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.dp.getController()).build();

            holder.dp.setController(controller);
        }
        else{
            Uri uri = Uri.parse("");
            ImageRequest request = ImageRequest.fromUri(uri);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.dp.getController()).build();

            holder.dp.setController(controller);
        }
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
        }*/
        holder.button.setOnClickListener(v->{
            mem_transactions mem_transactions=new mem_transactions();
            Bundle args=new Bundle();
            args.putString("sending_uid_6580",uid_list.get(position));
            args.putString("sending_name_6548256",name_list.get(position));
            mem_transactions.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, mem_transactions)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return name_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView dp;
        TextView name,total_raised,button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textView84);
            total_raised=itemView.findViewById(R.id.headdp);
            button=itemView.findViewById(R.id.view);
            dp=itemView.findViewById(R.id.circleImageView);
        }
    }
}
