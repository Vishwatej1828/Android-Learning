package com.example.learnigCustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ChaptersViewAdapter extends ArrayAdapter<ChaptersView> {

    // invoke the suitable constructor of the ArrayAdapter class
    public ChaptersViewAdapter(@NonNull Context context, ArrayList<ChaptersView> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused; otherwise, inflate a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Get the data item for this position
        ChaptersView currentItem = getItem(position);

        // Populate the view with data
        ImageView imageView = convertView.findViewById(R.id.item_image);
        TextView textView = convertView.findViewById(R.id.item_name);

        // Set the data
        if (currentItem != null) {
            imageView.setImageResource(currentItem.getLogo());
            textView.setText(currentItem.getLink());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

