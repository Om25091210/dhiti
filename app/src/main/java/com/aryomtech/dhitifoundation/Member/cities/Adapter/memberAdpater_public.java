package com.aryomtech.dhitifoundation.Member.cities.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
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

import com.aryomtech.dhitifoundation.Member.view_profile;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.cities.model.member_data;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class memberAdpater_public extends RecyclerView.Adapter<memberAdpater_public.Viewholder>{

    Context context;
    String identity="";
    DatabaseReference users_ref;
    ArrayList<member_data> list;

    public memberAdpater_public(ArrayList<member_data> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_public_member,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        if(list.get(position).getName()!=null)
            holder.name.setText(list.get(position).getName());
        Fresco.initialize(
                context,
                ImagePipelineConfig.newBuilder(context)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        if(list.get(position).getDplink()!=null) {
            Uri uri = Uri.parse(list.get(position).getDplink());
            ImageRequest request = ImageRequest.fromUri(uri);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder.draweeView.getController()).build();

            holder.draweeView.setController(controller);
        }
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(list.get(position).getDplink()!=null){
                try{
                    Glide.with(context).asBitmap()
                            .load(list.get(position).getDplink())
                            .thumbnail(0.1f)
                            .fitCenter()
                            .override(150,150)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            if(list.get(position).getDplink()!=null){
                try{
                    Glide.with(context).asBitmap()
                            .load(list.get(position).getDplink())
                            .thumbnail(0.1f)
                            .fitCenter()
                            .override(40,40)
                            .placeholder(R.drawable.ic_undraw_profile_pic_ic5t)
                            .into(holder.dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
        if(list.get(position).getPart()!=null)
            holder.part.setText(list.get(position).getPart());
        else
            holder.part.setText("");

        holder.button.setOnClickListener(v->{
            view_profile view_profile=new view_profile();
            Bundle args=new Bundle();
            args.putString("passing_list_uid_784521",list.get(position).getUid());
            view_profile.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_profile)
                    .addToBackStack(null)
                    .commit();
        });

        identity=context.getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");
        if(identity.equals("admin"))
            holder.del.setVisibility(View.VISIBLE);
        else
            holder.del.setVisibility(View.GONE);
        holder.del.setOnClickListener(v->{
            del_or_not(position);
        });
    }

    private void del_or_not(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_for_sure);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView cancel=dialog.findViewById(R.id.textView96);
        TextView yes=dialog.findViewById(R.id.textView95);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        cancel.setOnClickListener(vc-> dialog.dismiss());
        yes.setOnClickListener(v1->{
            users_ref= FirebaseDatabase.getInstance().getReference().child("users");
            users_ref.child(list.get(position).getUid()).child("identity").setValue("guest");
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Viewholder extends RecyclerView.ViewHolder{

        SimpleDraweeView draweeView;
        TextView name,button,part;
        ImageView dp,del;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            draweeView  = itemView.findViewById(R.id.sampleImageView);
            name=itemView.findViewById(R.id.textView84);
            dp=itemView.findViewById(R.id.imageView);
            part=itemView.findViewById(R.id.part);
            del=itemView.findViewById(R.id.del);
            button=itemView.findViewById(R.id.view);
        }
    }
}
