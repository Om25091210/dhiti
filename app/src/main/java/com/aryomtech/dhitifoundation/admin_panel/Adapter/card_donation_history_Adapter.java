package com.aryomtech.dhitifoundation.admin_panel.Adapter;

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


public class card_donation_history_Adapter extends RecyclerView.Adapter<card_donation_history_Adapter.ViewHolder> {

    Context context;
    ArrayList<card_donation_history_data> list;
    public card_donation_history_Adapter(Context context, ArrayList<card_donation_history_data> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.content_donation_transaction,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.identity.setText(list.get(position).getName());
        String amount_paid="₹ "+list.get(position).getAmount_paid();
        holder.amount.setText(amount_paid);
        holder.payment.setText(list.get(position).getPaid_on());
        String txt_name=list.get(position).getName().charAt(0)+"";
        holder.circular_success.setText(txt_name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView circular_success,identity,payment,amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circular_success=itemView.findViewById(R.id.head3);
            identity=itemView.findViewById(R.id.textView9);
            payment=itemView.findViewById(R.id.part);
            amount=itemView.findViewById(R.id.textView121);
        }
    }
}
