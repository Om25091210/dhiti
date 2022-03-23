package com.aryomtech.dhitifoundation.events.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.events.Eventmodel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Random;

public class EventAdapterClass extends RecyclerView.Adapter<EventAdapterClass.MyViewHolder>{

    ArrayList<Eventmodel> list;
    Context context;
    View view;
    String date;
    FirebaseAuth auth;
    String identity;
    FirebaseUser user;
    DatabaseReference events;

    public EventAdapterClass(ArrayList<Eventmodel> list, Context events, String finalStore, String identity) {
        this.list = list;
        this.context = events;
        this.date=finalStore;
        this.identity=identity;
    }

    @NonNull
    @Override
    public EventAdapterClass.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_show_event,parent,false);
        return new EventAdapterClass.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapterClass.MyViewHolder holder, int position) {
        if (list.get(position).getState()!=null) {
            if(!identity.equals("guest")) {
                holder.p_msg.setVisibility(View.GONE);
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();
                holder.title.setText(list.get(position).getTitle());

                holder.location.setText(list.get(position).getLocation());
                holder.date_date.setText(date);
                holder.message.setVisibility(View.GONE);

                holder.remove.setOnClickListener(v -> {
                    if (user.getUid().equals(list.get(position).getCreator())) {
                        Dialog dialog = new Dialog(context);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.dialog_for_sure);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        TextView cancel = dialog.findViewById(R.id.textView96);
                        TextView yes = dialog.findViewById(R.id.textView95);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show();
                        cancel.setOnClickListener(vi -> dialog.dismiss());
                        yes.setOnClickListener(vy -> {
                                String url = "Events/" + list.get(position).getCreatedon() + list.get(position).getTitle() + ".png";

                                StorageReference storageReference =
                                        FirebaseStorage.getInstance().getReference().child(url);

                                storageReference.delete().addOnSuccessListener(aVoid -> Log.e("Success", "onSuccess: deleted file successfully"))
                                        .addOnFailureListener(exception -> Log.e("Failure", "onFailure: File is not delete!"));
                                events = FirebaseDatabase.getInstance().getReference().child("Events");
                                events.child(date).child(list.get(position).getKey()).removeValue();
                                list.remove(position);
                                notifyItemChanged(position);
                                dialog.dismiss();
                        });
                    }
                });
                try {
                    Glide.with(context).asBitmap()
                            .load(list.get(position).getImage())
                            .thumbnail(0.1f)
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    new Handler(Looper.myLooper()).postDelayed(() ->
                                            holder.image.setImageBitmap(resource), 700);
                                    return false;
                                }
                            })
                            .placeholder(R.drawable.ic_good_team)
                            .into(holder.image);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!(list.get(position).getMessage() == null)) {
                    holder.message.setText(list.get(position).getMessage());
                    holder.message.setVisibility(View.VISIBLE);
                }
                Random random = new Random();
                int rand = random.nextInt(1000);
                if (rand % 2 == 0) {
                    holder.view_color.setBackgroundResource(R.color.amber_700);
                } else if (rand % 3 == 0) {
                    holder.view_color.setBackgroundResource(R.color.Algerian_red);
                } else if (rand % 5 == 0) {
                    holder.view_color.setBackgroundResource(R.color.light_green_A700);
                } else {
                    holder.view_color.setBackgroundResource(R.color.blue_A700);
                }
                if (user.getUid().equals(list.get(position).getCreator())) {
                    holder.remove.setVisibility(View.VISIBLE);
                } else {
                    holder.remove.setVisibility(View.GONE);
                }
            }
            else{
                holder.p_msg.setVisibility(View.VISIBLE);
                holder.remove.setVisibility(View.GONE);
            }
        }
        else {
            holder.p_msg.setVisibility(View.GONE);
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            holder.title.setText(list.get(position).getTitle());

            holder.location.setText(list.get(position).getLocation());
            holder.date_date.setText(date);
            holder.message.setVisibility(View.GONE);

            holder.remove.setOnClickListener(v -> {
                if (user.getUid().equals(list.get(position).getCreator())) {
                    Dialog dialog = new Dialog(context);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_for_sure);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    TextView cancel = dialog.findViewById(R.id.textView96);
                    TextView yes = dialog.findViewById(R.id.textView95);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();
                    cancel.setOnClickListener(vi -> dialog.dismiss());
                    yes.setOnClickListener(vy -> {
                            String url = "Events/" + list.get(position).getCreatedon() + list.get(position).getTitle() + ".png";

                            StorageReference storageReference =
                                    FirebaseStorage.getInstance().getReference().child(url);

                            storageReference.delete().addOnSuccessListener(aVoid -> Log.e("Success", "onSuccess: deleted file successfully"))
                                    .addOnFailureListener(exception -> Log.e("Failure", "onFailure: File is not delete!"));
                            events = FirebaseDatabase.getInstance().getReference().child("Events");
                            events.child(date).child(list.get(position).getKey()).removeValue();
                            list.remove(position);
                            notifyItemChanged(position);
                            dialog.dismiss();
                    });
                }
            });
            try {
                Glide.with(context).asBitmap()
                        .load(list.get(position).getImage())
                        .thumbnail(0.1f)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                new Handler(Looper.myLooper()).postDelayed(() ->
                                        holder.image.setImageBitmap(resource), 700);
                                return false;
                            }
                        })
                        .placeholder(R.drawable.ic_good_team)
                        .into(holder.image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!(list.get(position).getMessage() == null)) {
                holder.message.setText(list.get(position).getMessage());
                holder.message.setVisibility(View.VISIBLE);
            }
            Random random = new Random();
            int rand = random.nextInt(1000);
            if (rand % 2 == 0) {
                holder.view_color.setBackgroundResource(R.color.amber_700);
            } else if (rand % 3 == 0) {
                holder.view_color.setBackgroundResource(R.color.Algerian_red);
            } else if (rand % 5 == 0) {
                holder.view_color.setBackgroundResource(R.color.light_green_A700);
            } else {
                holder.view_color.setBackgroundResource(R.color.blue_A700);
            }
            if (user.getUid().equals(list.get(position).getCreator())) {
                holder.remove.setVisibility(View.VISIBLE);
            } else {
                holder.remove.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,message,location,p_msg;
        View view_color;
        RoundedImageView image;
        ConstraintLayout layoutNote;
        ImageView remove;
        TextView date_date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.textTitle);
            message=itemView.findViewById(R.id.textDateTime);
            location=itemView.findViewById(R.id.textSubtitle);
            view_color=itemView.findViewById(R.id.viewSubtitleIndicator);
            date_date=itemView.findViewById(R.id.date);
            image=itemView.findViewById(R.id.imageNote);
            remove=itemView.findViewById(R.id.remove);
            p_msg=itemView.findViewById(R.id.textView133);
            layoutNote=itemView.findViewById(R.id.layoutNote);
        }
    }
}
