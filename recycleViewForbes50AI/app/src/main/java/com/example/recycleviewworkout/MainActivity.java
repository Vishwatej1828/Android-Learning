package com.example.recycleviewworkout;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CompanyDescAdapter adapter;
    private List<Company> companyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the company list with sample data
        companyList = new ArrayList<>();
        companyList.add(new Company("ABRIDGE", R.drawable.abridge, R.string.abridge_short_desc, R.string.abridge_long_desc));
        companyList.add(new Company("ADEPT", R.drawable.adept, R.string.adept_short_desc, +R.string.adept_long_desc));
        companyList.add(new Company("ANDRUIL INDUSTRIES", R.drawable.andruil_industries, R.string.anthropic_short_desc, R.string.andruil_industries_long_desc));
        companyList.add(new Company("ANTHROPIC", R.drawable.anthropic, R.string.anthropic_short_desc, R.string.anthropic_long_desc));
        companyList.add(new Company("ANTHROPIC", R.drawable.anyscale, R.string.anyscale_short_desc, R.string.anyscale_long_desc));

        // Set the adapter
        adapter = new CompanyDescAdapter(companyList);
        recyclerView.setAdapter(adapter);
    }
}