package com.aryomtech.dhitifoundation.events.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.events.model.att_Data;

import java.util.ArrayList;

public class AttAdapter extends RecyclerView.Adapter<AttAdapter.ViewHolder> {

    Context context;
    ArrayList<att_Data> list;
    ArrayList<String> name_list;
    public AttAdapter(Context context, ArrayList<att_Data> list, ArrayList<String> name_list) {
        this.context=context;
        this.list=list;
        this.name_list=name_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.att_card_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.name.setText(name_list.get(position));
            String text=name_list.get(position).charAt(0)+"";
            holder.circle_text.setText(text);
            holder.description.setText(list.get(position).getReason());
            holder.time.setText(list.get(position).getTime());
            holder.location.setText(list.get(position).getLocation());
            holder.view_in_map.setOnClickListener(v->{
                if(list.get(position).getLatitude()!=null &&  list.get(position).getLongitude()!=null){
                    if(list.get(position).getLatitude().equals("") ||  list.get(position).getLongitude().equals("")){
                        Uri uri=Uri.parse("geo:0,0?q="+list.get(position).getLocation());
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        intent.setPackage("com.google.android.apps.maps");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else {
                        String uri = "http://maps.google.com/maps?daddr=" + list.get(position).getLatitude() + "," + list.get(position).getLongitude() + " (" + "Where the party is at" + ")";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,description,time,circle_text,view_in_map,location;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.textView84);
            description=itemView.findViewById(R.id.textView130);
            time=itemView.findViewById(R.id.textView85);
            circle_text=itemView.findViewById(R.id.head1);
            view_in_map=itemView.findViewById(R.id.view);
            location=itemView.findViewById(R.id.textView86);
        }
    }
}
