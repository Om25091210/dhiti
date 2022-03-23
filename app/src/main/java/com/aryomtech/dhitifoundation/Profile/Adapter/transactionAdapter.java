package com.aryomtech.dhitifoundation.Profile.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.GroupTask.Adapter.ViewmemAdapter;
import com.aryomtech.dhitifoundation.Profile.model.history_data;
import com.aryomtech.dhitifoundation.R;

import java.util.ArrayList;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.ViewHolder> {


    Context context;
    ArrayList<history_data> list;
    public transactionAdapter(Context context, ArrayList<history_data> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.design_mem_transaction,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (list.get(position).getPayment().equals("successfull")) {
            holder.circular_success.setVisibility(View.VISIBLE);
            holder.circular_success.setText("S");
            holder.circular_failed.setVisibility(View.GONE);
            holder.payment.setText(list.get(position).getPayment());
            holder.payment.setTextColor(Color.parseColor("#008643"));
            String amount_paid="₹ "+list.get(position).getAmount_paid();
            holder.amount.setText(amount_paid);
            holder.amount.setTextColor(Color.parseColor("#008643"));
        }
        else if (list.get(position).getPayment().equals("failed")){
            holder.circular_success.setVisibility(View.GONE);
            holder.circular_failed.setVisibility(View.VISIBLE);
            holder.circular_failed.setText("F");
            holder.payment.setText(list.get(position).getPayment());
            holder.payment.setTextColor(Color.parseColor("#e32636"));
            String amount_paid="₹ "+list.get(position).getAmount_paid();
            holder.amount.setText(amount_paid);
            holder.amount.setTextColor(Color.parseColor("#e32636"));
        }
        if(list.get(position).getPayment_id()!=null) {
            holder.pay_id.setVisibility(View.VISIBLE);
            String p_id = "Payment id - " + list.get(position).getPayment_id();
            holder.pay_id.setText(p_id);
        }
        else{
            holder.pay_id.setVisibility(View.GONE);
        }
        holder.paid_on.setText(list.get(position).getPaid_on());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView circular_success,circular_failed,paid_on,payment,amount,pay_id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circular_success=itemView.findViewById(R.id.head3);
            pay_id=itemView.findViewById(R.id.textView142);
            circular_failed=itemView.findViewById(R.id.failed);
            paid_on=itemView.findViewById(R.id.textView9);
            payment=itemView.findViewById(R.id.part);
            amount=itemView.findViewById(R.id.textView121);
        }
    }
}
