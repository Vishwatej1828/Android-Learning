package com.example.listviewapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CountryAdapter extends ArrayAdapter<Country> {

    public CountryAdapter(Context context, List<Country> countries) {
        super(context, 0, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_country, parent, false);
        }

        Country country = getItem(position);

        ImageView countryImage = convertView.findViewById(R.id.countryflagImage);
        TextView countryName = convertView.findViewById(R.id.countryName);
        TextView countryCapital = convertView.findViewById(R.id.countryCapital);

        countryImage.setImageResource(country.getImageResource());
        countryName.setText(country.getName());
        countryCapital.setText(country.getCapital());

        return convertView;
    }
}
