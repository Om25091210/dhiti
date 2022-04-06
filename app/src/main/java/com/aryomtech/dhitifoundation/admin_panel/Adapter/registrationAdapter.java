package com.aryomtech.dhitifoundation.admin_panel.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.Register_cause;
import com.aryomtech.dhitifoundation.admin_panel.fluid_view_supporters;
import com.aryomtech.dhitifoundation.admin_panel.model.set_form_data;
import com.aryomtech.dhitifoundation.admin_panel.see_registration;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class registrationAdapter extends RecyclerView.Adapter<registrationAdapter.ViewHolder> {

    Context context;
    List<set_form_data> list;
    DatabaseReference ref;
    String key;

    public registrationAdapter(Context context,String key,List<set_form_data> list){
        this.context=context;
        this.list=list;
        this.key=key;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.see_layout_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ref= FirebaseDatabase.getInstance().getReference().child("fluid_Cards");

        if(list.get(position).getHead1()!=null)
            holder.txt.setText(list.get(position).getHead1());

        holder.open.setOnClickListener(v->{
            Register_cause reg_form=new Register_cause();
            Bundle args=new Bundle();
            args.putString("keys_for_creating_donation",key);
            args.putString("head1",list.get(position).getHead1());
            args.putString("head2",list.get(position).getHead2());
            args.putString("head3",list.get(position).getHead3());
            args.putString("head4",list.get(position).getHead4());
            args.putString("head5",list.get(position).getHead5());
            args.putString("head6",list.get(position).getHead6());
            args.putString("head7",list.get(position).getHead7());
            args.putString("head8",list.get(position).getHead8());
            args.putString("head9",list.get(position).getHead9());
            args.putString("head10",list.get(position).getHead10());
            reg_form.setArguments(args);

            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer, reg_form)
                    .addToBackStack(null)
                    .commit();
        });

        holder.del_ext.setOnClickListener(v->{
            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vi-> dialog.dismiss());
            yes.setOnClickListener(vi-> {
                ref.child(key).child("registrations").child(list.get(position).getUid()).removeValue();
                list.remove(position);
                Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt,open;
        ImageView del_ext;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt=itemView.findViewById(R.id.textView136);
            open=itemView.findViewById(R.id.open);
            del_ext=itemView.findViewById(R.id.del_ext);
        }
    }
}
