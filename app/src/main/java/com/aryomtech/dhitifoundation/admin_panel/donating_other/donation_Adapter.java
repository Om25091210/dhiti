package com.aryomtech.dhitifoundation.admin_panel.donating_other;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import www.sanju.motiontoast.MotionToast;

public class donation_Adapter extends RecyclerView.Adapter<donation_Adapter.ViewHolder> {

    List<donations_data> list;
    Context context;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser user;
    String identity;
    String key;
    String text;
    String identity_admin="";

    public donation_Adapter(Context context, List<donations_data> list,String identity,String key) {
        this.context=context;
        this.list=list;
        this.key=key;
        this.identity=identity;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_lists,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        identity_admin=context.getSharedPreferences("Identity_2509_0101_1310",MODE_PRIVATE)
                .getString("2509_0101_1310_identity","");
        Uri uri = Uri.parse(list.get(position).getImage_url());
        ImageRequest request = ImageRequest.fromUri(uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(holder.image.getController()).build();

        holder.image.setController(controller);
        /*try{
            Glide.with(context).asBitmap()
                    .load(list.get(position).getImage_url())
                    .placeholder(R.drawable.ic_image_holder)
                    .into(holder.image);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(list.get(position).getCategory()!=null) {
            holder.category.setText(list.get(position).getCategory());
            switch (list.get(position).getCategory()) {
                case "Food":
                    holder.category.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_food_, 0, 0, 0);
                    break;
                case "Clothes":
                    holder.category.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cloth_, 0, 0, 0);
                    break;
                case "Education":
                    holder.category.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_books, 0, 0, 0);
                    break;
                case "Other":
                    holder.category.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_other_, 0, 0, 0);
                    break;
            }
        }
        if (list.get(position).getContact()!=null) {
            holder.call.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                intent.setData(Uri.parse("tel: " + list.get(position).getContact())); // Data with intent respective action on intent
                context.startActivity(intent);
            });
        }
        if(list.get(position).getDescription()!=null)
            holder.donation.setText(list.get(position).getDescription());
        if (list.get(position).getAddress()!=null)
            holder.address.setText(list.get(position).getAddress());
        if(list.get(position).getUid()!=null) {
            holder.edit.setOnClickListener(v -> {
                if(list.get(position).getUid().equals(user.getUid()) || identity_admin.equals("admin")){
                    creating_donation creating_donation=new creating_donation();
                    Bundle args=new Bundle();
                    args.putString("image_url_key_donation",list.get(position).getImage_url());
                    args.putString("category_key_donation",list.get(position).getCategory());
                    args.putString("address_key_donation",list.get(position).getAddress());
                    args.putString("contact_key_donation",list.get(position).getContact());
                    args.putString("what_is_the_donation",list.get(position).getDescription());
                    args.putString("pick_up_donation",list.get(position).getPick_by());
                    args.putString("key_of_donation",list.get(position).getKey());
                    args.putString("uid_of_donation",list.get(position).getUid());
                    args.putString("keys_for_creating_donation",key);
                    creating_donation.setArguments(args);

                    ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right).add(R.id.drawer, creating_donation)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        holder.pick.setOnClickListener(v->{
            if(!holder.text_pick.getText().toString().equals("Responded")) {
                Dialog dialog = new Dialog(context);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_for_sure);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                TextView cancel = dialog.findViewById(R.id.textView96);
                TextView yes = dialog.findViewById(R.id.textView95);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                cancel.setOnClickListener(vc -> dialog.dismiss());
                yes.setOnClickListener(vy -> {
                    dialog.dismiss();
                    DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference().child("users");
                    users_ref.child(list.get(position).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child("token").exists()) {
                                String token = snapshot.child("token").getValue(String.class);
                                Specific specific = new Specific();
                                assert token != null;
                                specific.noti("Donation pick-up accepted", "Your donation will be further processed by our member. Check details for more.", token);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    reference = FirebaseDatabase.getInstance().getReference().child("fluid_Cards").child(key).child("cause_donations").child(list.get(position).getKey());
                    reference.child("pick_by").setValue(user.getUid());
                    String res = "Responded";
                    holder.text_pick.setText(res);
                    holder.text_pick.setTextColor(Color.parseColor("#66BB6A"));
                });
            }
        });
        holder.del.setOnClickListener(v->{
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(v1-> dialog.dismiss());
            yes.setOnClickListener(v1-> {
                reference = FirebaseDatabase.getInstance().getReference().child("fluid_Cards").child(key).child("cause_donations").child(list.get(position).getKey());
                reference.removeValue();
                String imagepath = "donations/" + list.get(position).getKey() + "_donation_img" + ".png";
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReference().child(imagepath);
                storageReference.delete();
                delete_element(position);
                dialog.dismiss();
                MotionToast.Companion.darkColorToast((Activity) context,
                        "Donation removed",
                        "Deleted successfully.",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(context, R.font.helvetica_regular));
            });
        });
        if(identity.equals("")){
            holder.pick.setVisibility(View.VISIBLE);
            holder.call.setVisibility(View.VISIBLE);
        }
        else{
            holder.pick.setVisibility(View.GONE);
            holder.call.setVisibility(View.GONE);
        }
        if(user.getUid().equals(list.get(position).getUid()) || identity_admin.equals("admin")){
            holder.edit.setVisibility(View.VISIBLE);
            holder.del.setVisibility(View.VISIBLE);
        }
        else{
            holder.edit.setVisibility(View.GONE);
            holder.del.setVisibility(View.GONE);
        }
        if(list.get(position).getPick_by()!=null){
            String res="Responded";
            holder.text_pick.setText(res);
            holder.text_pick.setTextColor(Color.parseColor("#66BB6A"));
        }
        else{
            String res="Pick";
            holder.text_pick.setText(res);
            holder.text_pick.setTextColor(Color.parseColor("#FF2371FA"));
        }


    }
    public void delete_element(int position) {
        if (list != null) {
            list.remove(position);
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,address,donation,category;
        SimpleDraweeView image;
        CardView pick;
        ImageView call;
        ConstraintLayout layout_card;
        TextView edit,text_pick;
        ImageView del;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image=itemView.findViewById(R.id.imageNote);
            text_pick=itemView.findViewById(R.id.textView111);
            layout_card=itemView.findViewById(R.id.layout_card);
            name=itemView.findViewById(R.id.textView124);
            address=itemView.findViewById(R.id.address);
            donation=itemView.findViewById(R.id.donating_data);
            category=itemView.findViewById(R.id.food);
            pick=itemView.findViewById(R.id.donate);
            call=itemView.findViewById(R.id.imageView23);
            edit=itemView.findViewById(R.id.edit);
            del=itemView.findViewById(R.id.del);
        }
    }


}
