package com.aryomtech.dhitifoundation.Profile.membership_payments.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.admin_panel.model.card_donation_history_data;

import java.util.ArrayList;


public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.ViewHolder>{

    Context context;
    ArrayList<card_donation_history_data> list;
    public transactionAdapter(Context context, ArrayList<card_donation_history_data> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_id_member,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.identity.setText(list.get(position).getPaid_on());
        if(list.get(position).getPayment_id()!=null) {
            holder.pay_id.setVisibility(View.VISIBLE);
            String p_id = "Payment id - " + list.get(position).getPayment_id();
            holder.pay_id.setText(p_id);
        }
        else{
            holder.pay_id.setVisibility(View.GONE);
        }
        String amount_paid="₹ "+list.get(position).getAmount_paid();
        holder.amount.setText(amount_paid);
        String suc_txt="Success";
        holder.payment.setText(suc_txt);
        String txt_name="S";
        holder.circular_success.setText(txt_name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView circular_success,identity,payment,amount,pay_id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circular_success=itemView.findViewById(R.id.head3);
            identity=itemView.findViewById(R.id.textView9);
            pay_id=itemView.findViewById(R.id.textView142);
            payment=itemView.findViewById(R.id.part);
            amount=itemView.findViewById(R.id.textView121);
        }
    }
}
