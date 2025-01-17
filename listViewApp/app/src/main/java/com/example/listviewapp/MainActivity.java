package com.example.listviewapp;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.countryListView);

        List<Country> countries = new ArrayList<>();
        countries.add(new Country("Vietnam", "Hanoi", R.drawable.vietnam_flag));
        countries.add(new Country("India", "Delhi", R.drawable.india_flag));
        countries.add(new Country("United States", "Washington D.C.", R.drawable.usa_flag));
        countries.add(new Country("Russia", "Moscow", R.drawable.russia_flag));

        countries.add(new Country("Australia", "Canberra", R.drawable.australia_flag));
        countries.add(new Country("Argentina", "Buenos Aires", R.drawable.argentina_flag));
        countries.add(new Country("Canada", "Ottawa", R.drawable.canada_flag));

        countries.add(new Country("Germany", "Berlin", R.drawable.germany_flag));
        countries.add(new Country("Spain", "Madrid", R.drawable.spain_flag));

        CountryAdapter adapter = new CountryAdapter(this, countries);
        listView.setAdapter(adapter);
    }

    public void closeApp() {
        finishAffinity();
    }
}
