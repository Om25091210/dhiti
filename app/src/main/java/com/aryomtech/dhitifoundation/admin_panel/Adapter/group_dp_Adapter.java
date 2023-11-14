package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.cities.onClickInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class group_dp_Adapter extends RecyclerView.Adapter<group_dp_Adapter.ViewHolder> {

    ArrayList<String> dp_list;
    ArrayList<String> name_list;
    Context context;
    onClickInterface onClickInterface;
    public group_dp_Adapter(ArrayList<String> dp_list, ArrayList<String> name_list, Context context,onClickInterface onClickInterface) {
        this.dp_list = dp_list;
        this.name_list = name_list;
        this.onClickInterface = onClickInterface;
        this.context = context;
        Fresco.initialize(
                context,
                ImagePipelineConfig.newBuilder(context)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dp_layout_group,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // onClickInterface.setClick(position);
        holder.layout.setOnClickListener(v->onClickInterface.setClick(position));
        holder.name.setText(name_list.get(position));
        Uri uri = Uri.parse(dp_list.get(position));
        ImageRequest request = ImageRequest.fromUri(uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.dp.getController()).build();

        holder.dp.setController(controller);
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(dp_list.get(position)!=null){
                try{
                    Glide.with(context).asBitmap()
                            .load(dp_list.get(position))
                            .thumbnail(0.1f)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .override(80,80)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            if(dp_list.get(position)!=null){
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
    }

    @Override
    public int getItemCount() {
        return dp_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView dp;
        LinearLayout layout;
        TextView name;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            dp=itemView.findViewById(R.id.circleImageView);
            name=itemView.findViewById(R.id.textView82);
            layout=itemView.findViewById(R.id.layout);
        }
    }
}
