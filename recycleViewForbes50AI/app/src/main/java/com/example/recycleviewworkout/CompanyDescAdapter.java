package com.example.recycleviewworkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanyDescAdapter extends RecyclerView.Adapter<CompanyDescAdapter.CompanyViewHolder> {

    private List<Company> companies;

    public CompanyDescAdapter(List<Company> companies) {
        this.companies = companies;
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        Company company = companies.get(position);

        // Set company name
        holder.nameTextView.setText(company.getName());
        holder.shortDescriptionTextView.setText(company.getShortDescription());

        // Set the full description (Initially hidden)
        holder.fullDescriptionTextView.setText(company.getFullDescription());

        // Set image resource (No need for Glide here, use setImageResource)
        holder.companyLogoImage.setImageResource(company.getImageResId());

        // Ensure visibility of description TextViews
        holder.fullDescriptionTextView.setVisibility(View.GONE);  // Initially hidden
        holder.shortDescriptionTextView.setVisibility(View.VISIBLE); // Initially visible

        // Set click listener to expand/collapse the description
        holder.itemView.setOnClickListener(v -> {
            if (holder.fullDescriptionTextView.getVisibility() == View.GONE) {
                holder.fullDescriptionTextView.setVisibility(View.VISIBLE);
                holder.shortDescriptionTextView.setVisibility(View.GONE);
            } else {
                holder.fullDescriptionTextView.setVisibility(View.GONE);
                holder.shortDescriptionTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView companyLogoImage;
        public TextView shortDescriptionTextView;
        public TextView fullDescriptionTextView;

        public CompanyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.company_name);
            companyLogoImage = itemView.findViewById(R.id.company_logo);
            shortDescriptionTextView = itemView.findViewById(R.id.company_short_description);
            fullDescriptionTextView = itemView.findViewById(R.id.company_full_description);
        }
    }
}
