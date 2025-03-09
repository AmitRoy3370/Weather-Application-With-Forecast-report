package com.example.myweatherapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ForecastAdapter extends ArrayAdapter<String> {
    public ForecastAdapter(@NonNull Context context, List<String> forecastList) {
        super(context, 0, forecastList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String forecast = getItem(position);

        if(convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(forecast);

        return convertView;

    }

}
