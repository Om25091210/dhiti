package com.aryomtech.dhitifoundation.admin_panel.Approve_Forms.Adapter;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.models.forms_data;

import java.util.ArrayList;

public class approve_forms_Adapter extends RecyclerView.Adapter<approve_forms_Adapter.ViewHolder> {

    Context context;
    ArrayList<forms_data> list;
    public approve_forms_Adapter(Context context, ArrayList<forms_data> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_approve_form,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        String str_talent=list.get(position).getDedicate_talent().replace("[","");
        String str_talent_final=str_talent.replace("]","");
        holder.detail.setText(str_talent_final);
        String str_txt=list.get(position).getName().charAt(0)+"";
        holder.circle_text.setText(str_txt);
        holder.email.setText(list.get(position).getEmail());
        holder.phone.setText(list.get(position).getContact());
        holder.address.setText(list.get(position).getAddress());
        if (list.get(position).getApprove()!=null){
            String str_tt="View";
            holder.button.setText(str_tt);
        }
        else{
            String str_tt="Approve";
            holder.button.setText(str_tt);
        }
        holder.button.setOnClickListener(v-> {
            View_form view_form=new View_form();
            Bundle args=new Bundle();
            args.putString("name_sending_form_2509_01",list.get(position).getName());
            args.putString("blood_sending_form_2509_02",list.get(position).getBlood());
            args.putString("city_sending_form_2509_03",list.get(position).getCity());
            args.putString("contact_sending_form_2509_04",list.get(position).getContact());
            args.putString("address_sending_form_2509_05",list.get(position).getAddress());
            args.putString("qualification_sending_form_2509_06",list.get(position).getQualification());
            args.putString("profession_sending_form_2509_07",list.get(position).getProfession());
            args.putString("email_sending_form_2509_08",list.get(position).getEmail());
            args.putString("age_sending_form_2509_09",list.get(position).getAge());
            args.putString("gender_sending_form_2509_10",list.get(position).getGender());
            args.putString("heard_about_dhiti_sending_form_2509_11",list.get(position).getHeard_about_dhiti());
            args.putString("dedicate_time_sending_form_2509_12",list.get(position).getDedicate_time());
            args.putString("suggestion_sending_form_2509_13",list.get(position).getSuggestion());
            args.putString("start_dhiti_city_sending_2509_14",list.get(position).getStart_city_dhiti());
            args.putString("represent_dhiti_s_c_sending_2509_15",list.get(position).getRepresent_dhiti());
            args.putString("experience_comm_sending_2509_16",list.get(position).getExperience_comm());
            args.putString("why_dhiti_sending_2509_17",list.get(position).getWhy_dhiti());
            args.putString("name_of_school_college_sending_2509_18",list.get(position).getName_of_school_college());
            args.putString("dedicate_talent_sending_2509_19",list.get(position).getDedicate_talent());
            args.putString("pushkey_sending_2509_20",list.get(position).getPushkey());
            args.putString("uid_sending_2509_021",list.get(position).getUid());
            args.putString("approved_or_not_sending",list.get(position).getApprove());
            args.putString("image_link_sending",list.get(position).getImage_link());
            view_form.setArguments(args);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations( R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_left, R.anim.exit_to_right)
                    .add(R.id.drawer,view_form)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView circle_text,name,detail,button,email,phone,address;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circle_text=itemView.findViewById(R.id.head1);
            phone=itemView.findViewById(R.id.textView132);
            email=itemView.findViewById(R.id.textView131);
            address=itemView.findViewById(R.id.address);
            name=itemView.findViewById(R.id.textView84);
            detail=itemView.findViewById(R.id.textView86);
            button=itemView.findViewById(R.id.view);
        }
    }
}

