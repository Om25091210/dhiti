package com.aryomtech.dhitifoundation.admin_panel.Verify_join_request.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class requestAdapter extends RecyclerView.Adapter<requestAdapter.ViewHolder> {

    Context context;
    ArrayList<String> name_list,detail_list,key_list,uid_list;
    String reference;
    DatabaseReference member_ref,users_ref;
    public requestAdapter(String which, Context context, ArrayList<String> name_list, ArrayList<String> detail_list, ArrayList<String> key_list, ArrayList<String> uid_list) {
        this.context=context;
        this.name_list=name_list;
        this.detail_list=detail_list;
        this.key_list=key_list;
        this.reference=which;
        this.uid_list=uid_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_request,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(name_list.get(position)!=null) {
            String txt = name_list.get(position).charAt(0) + "";
            holder.circle_text.setText(txt);
            holder.name.setText(name_list.get(position));
        }
        holder.detail.setText(detail_list.get(position));//index out of bounds 5 index,size 5.
        holder.button.setOnClickListener(v->{
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                dialog.dismiss();
                send_data_to_firebase(position);
            });
        });
        holder.del.setOnClickListener(view -> {
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                dialog.dismiss();
                delete_data(position);
            });
        });
    }

    private void delete_data(int position) {
        if (reference.equals("member")) {
            member_ref = FirebaseDatabase.getInstance().getReference().child("member_Request");
            member_ref.child(key_list.get(position)).removeValue();
            users_ref = FirebaseDatabase.getInstance().getReference().child("users");
            users_ref.child(uid_list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("identity").exists()) {
                        if (!Objects.equals(snapshot.child("identity").getValue(String.class), "member")) {
                            if (snapshot.child("token").exists()) {
                                String device_token = snapshot.child("token").getValue(String.class) + "";
                                Specific specific = new Specific();
                                specific.noti("Request rejected ☹", "Your request to continue as a member has been declined by dhiti foundation.", device_token);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else if(reference.equals("chapter")){
            member_ref= FirebaseDatabase.getInstance().getReference().child("chapter_head_Request");
            member_ref.child(key_list.get(position)).removeValue();
            users_ref = FirebaseDatabase.getInstance().getReference().child("users");
            users_ref.child(uid_list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("identity").exists()) {
                        if (!Objects.equals(snapshot.child("identity").getValue(String.class), "chapter-head")) {
                            if (snapshot.child("token").exists()) {
                                String device_token = snapshot.child("token").getValue(String.class) + "";
                                Specific specific = new Specific();
                                specific.noti("Request rejected ☹", "Your request to continue as a chapter-head has been declined by dhiti foundation.", device_token);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void send_data_to_firebase(int position){
        if (reference.equals("member")){
            member_ref= FirebaseDatabase.getInstance().getReference().child("member_Request");
            users_ref=FirebaseDatabase.getInstance().getReference().child("users");
            users_ref.child(uid_list.get(position)).child("request").setValue("approved");
            users_ref.child(uid_list.get(position)).child("identity").setValue("member");
            member_ref.child(key_list.get(position)).removeValue();
            users_ref.child(uid_list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("token").exists()) {
                        String device_token = snapshot.child("token").getValue(String.class) + "";
                        Specific specific = new Specific();
                        specific.noti("Request approved", "You can now continue as a member.", device_token);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
        else if(reference.equals("chapter")){
            member_ref= FirebaseDatabase.getInstance().getReference().child("chapter_head_Request");
            users_ref=FirebaseDatabase.getInstance().getReference().child("users");
            users_ref.child(uid_list.get(position)).child("request").setValue("approved");
            users_ref.child(uid_list.get(position)).child("identity").setValue("chapter-head");
            member_ref.child(key_list.get(position)).removeValue();
            users_ref.child(uid_list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("token").exists()) {
                        String device_token = snapshot.child("token").getValue(String.class) + "";
                        Specific specific = new Specific();
                        specific.noti("Request approved", "You can now continue as a Chapter-head.", device_token);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
    @Override
    public int getItemCount() {
        return name_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView del;
        TextView circle_text,name,detail,button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circle_text=itemView.findViewById(R.id.head1);
            name=itemView.findViewById(R.id.textView84);
            detail=itemView.findViewById(R.id.textView86);
            button=itemView.findViewById(R.id.view);
            del=itemView.findViewById(R.id.del);
        }
    }
}
