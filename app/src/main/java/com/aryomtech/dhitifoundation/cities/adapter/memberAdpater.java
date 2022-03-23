package com.aryomtech.dhitifoundation.cities.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Assign_task;
import com.aryomtech.dhitifoundation.admin_panel.members_list;
import com.aryomtech.dhitifoundation.cities.model.member_data;
import com.aryomtech.dhitifoundation.cities.onAgainClickInterface;
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

public class memberAdpater extends RecyclerView.Adapter<memberAdpater.Viewholder> {

    Context context;
    ArrayList<member_data> list;
    String city;
    String mode_of_task;
    boolean is_selected=false;
    onClickInterface onClickInterface;
    onAgainClickInterface onAgainClickInterface;
    public memberAdpater(ArrayList<member_data> list, Context context, String city, String mode_of_task, onClickInterface onClickInterface,onAgainClickInterface onAgainClickInterface) {
        this.list = list;
        this.context = context;
        this.city=city;
        this.mode_of_task=mode_of_task;
        this.onClickInterface = onClickInterface;
        this.onAgainClickInterface=onAgainClickInterface;
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
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_member_list,parent,false);
        return new memberAdpater.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {

        if(list.get(position).getName()!=null)
            holder.name.setText(list.get(position).getName());

        assert city!=null;
        if (city.equals("other")) {
            holder.city.setText(list.get(position).getCity());
        }
        else
            holder.city.setText("");

        if(list.get(position).getDplink()!=null) {
            Uri uri = Uri.parse(list.get(position).getDplink());
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
            try{
                Glide.with(context).asBitmap()
                        .load(list.get(position).getDplink())
                        .thumbnail(0.1f)
                        .fitCenter()
                        .override(100,100)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .into(holder.dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Glide.with(context).asBitmap()
                        .load(list.get(position).getDplink())
                        .thumbnail(0.1f)
                        .fitCenter()
                        .override(40, 40)
                        .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                        .into(holder.dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
       if(mode_of_task!=null){
           if (mode_of_task.equals("group task"))
               holder.add_button.setVisibility(View.VISIBLE);
           else
               holder.add_button.setVisibility(View.GONE);
       }
       holder.add_button.setOnClickListener(v->{
           if(holder.add_button.getText().toString().equalsIgnoreCase("add")){
               onClickInterface.setClick(position);
               String added="Added";
               holder.add_button.setText(added);
               holder.add_button.setBackgroundResource(R.drawable.added_person_bg);
           }
           else if(holder.add_button.getText().toString().equalsIgnoreCase("added")){
               onAgainClickInterface.set_remove_click(position);
               String add="Add";
               holder.add_button.setText(add);
               holder.add_button.setBackgroundResource(R.drawable.add_person_bg);
           }
       });
       holder.card.setOnClickListener(v->{
           if(mode_of_task!=null){
               Assign_task assign_task=new Assign_task();
               Bundle args=new Bundle();
               args.putString("uid unique key",list.get(position).getUid());
               assign_task.setArguments(args);

               if(mode_of_task.equals("private task")){
                   ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                           .beginTransaction()
                           .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                           .add(R.id.drawer,assign_task)
                           .addToBackStack(null)
                           .commit();
               }
           }
       });
       if(list.get(position).getPart()!=null)
           holder.part.setText(list.get(position).getPart());
       else
           holder.part.setText("");

       if (is_selected){
           String added="Added";
           holder.add_button.setText(added);
           holder.add_button.setBackgroundResource(R.drawable.added_person_bg);
       }
       else{
           String add="Add";
           holder.add_button.setText(add);
           holder.add_button.setBackgroundResource(R.drawable.add_person_bg);
       }
    }
    public void selectAll(){
        is_selected=true;
        notifyDataSetChanged();
    }
    public void unselectall(){
        is_selected=false;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder{

        TextView name,city,add_button,part;
        SimpleDraweeView dp;
        CardView card;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.textView9);
            dp=itemView.findViewById(R.id.circleImageView);
            city=itemView.findViewById(R.id.city);
            part=itemView.findViewById(R.id.part);
            add_button=itemView.findViewById(R.id.textView21);
            card=itemView.findViewById(R.id.card);
        }
    }
}
