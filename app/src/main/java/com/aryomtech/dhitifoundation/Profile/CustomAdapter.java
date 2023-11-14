package com.aryomtech.dhitifoundation.Profile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryomtech.dhitifoundation.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Integer> flags;
    ArrayList<String> countryNames;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, ArrayList<Integer> flags, ArrayList<String> countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_layout, null);
        ImageView icon = view.findViewById(R.id.imageView);
        TextView names =view.findViewById(R.id.textView);
        icon.setImageResource(flags.get(i));
        names.setText(countryNames.get(i));
        return view;
    }
}
