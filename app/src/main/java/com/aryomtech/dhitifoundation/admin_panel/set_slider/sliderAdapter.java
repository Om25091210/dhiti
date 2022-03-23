package com.aryomtech.dhitifoundation.admin_panel.set_slider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.slider.Model.ModelSmoolider;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class sliderAdapter extends RecyclerView.Adapter<sliderAdapter.ViewHolder> {

    Context context;
    List<ModelSmoolider> list;

    public sliderAdapter(Context context, ArrayList<ModelSmoolider> list) {
        this.context=context;
        this.list=list;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_slider_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        /*try{
            Glide.with(context).asBitmap()
                    .load(list.get(position).getImage_url())
                    .placeholder(R.drawable.ic_image_holder)
                    .into(holder.img_slider);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Uri uri = Uri.parse(list.get(position).getImage_url());
        ImageRequest request = ImageRequest.fromUri(uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.img_slider.getController()).build();

        holder.img_slider.setController(controller);

        holder.head_text.setText(list.get(position).getHead_text());
        holder.des_text.setText(list.get(position).getDes_text());
        holder.rel_content.setOnClickListener(v->{
            slider_card slider_card=new slider_card();
            Bundle args=new Bundle();
            args.putString("head_text_101",list.get(position).getHead_text());
            args.putString("des_text_102",list.get(position).getDes_text());
            args.putString("image_url_103",list.get(position).getImage_url());
            args.putString("pushkey_10465",list.get(position).getPushkey());
            slider_card.setArguments(args);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,slider_card)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView img_slider;
        TextView head_text,des_text;
        RelativeLayout rel_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            head_text=itemView.findViewById(R.id.textView110);
            des_text=itemView.findViewById(R.id.textView113);
            img_slider=itemView.findViewById(R.id.img_slider);
            rel_content=itemView.findViewById(R.id.rel_content);
        }
    }
}
