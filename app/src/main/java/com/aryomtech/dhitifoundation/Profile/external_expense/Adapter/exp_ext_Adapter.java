package com.aryomtech.dhitifoundation.Profile.external_expense.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aryomtech.dhitifoundation.Profile.external_expense.Model.expenseData;
import com.aryomtech.dhitifoundation.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;


public class exp_ext_Adapter extends RecyclerView.Adapter<exp_ext_Adapter.ViewHolder> {

    Context context;
    ArrayList<expenseData> list;
    DatabaseReference expense_record;
    FirebaseAuth auth;
    FirebaseUser user;
    String who;
    public exp_ext_Adapter(Context context, ArrayList<expenseData> list,String who) {
        this.context=context;
        this.list=list;
        this.who=who;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        String file_name="File - "+list.get(position).getFile_name();
        holder.name_file.setText(file_name);
        holder.city.setText(list.get(position).getCity());
        holder.caption.setText(list.get(position).getDescription());
        holder.open.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri fileuri =  Uri.parse(list.get(position).getFile_link()) ;
            intent.setDataAndType(fileuri,"*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent in = Intent.createChooser(intent,"open file");
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        });
        holder.del_ext.setOnClickListener(v->{
            expense_record= FirebaseDatabase.getInstance().getReference().child("expense_record");
            String path = "expense_record/"+user.getUid()+ list.get(position).getKey()+"/"+list.get(position).getFile_name();
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vi-> dialog.dismiss());
            yes.setOnClickListener(v1->{
                expense_record.child(user.getUid()).child(list.get(position).getKey()).removeValue();
                if (list.get(position).getFile_link() != null) {
                    StorageReference storageReference =
                            FirebaseStorage.getInstance().getReference().child(path);
                    storageReference.delete();
                    dialog.dismiss();
                    MotionToast.Companion.darkColorToast((Activity) context,
                            "Deleted!!",
                            "Deleted successfully.",
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular));
                }
            });
        });
        if (who.equals("admin"))
            holder.del_ext.setVisibility(View.GONE);
        else
            holder.del_ext.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView caption,open,name_file,city;
        ImageView del_ext;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            caption=itemView.findViewById(R.id.textView136);
            open=itemView.findViewById(R.id.open);
            del_ext=itemView.findViewById(R.id.del_ext);
            name_file=itemView.findViewById(R.id.textView138);
            city=itemView.findViewById(R.id.textView139);
        }
    }
}
