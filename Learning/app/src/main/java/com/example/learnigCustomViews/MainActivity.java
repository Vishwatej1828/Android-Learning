package com.example.learnigCustomViews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create a arraylist of the type NumbersView
        final ArrayList<ChaptersView> arrayList = new ArrayList<ChaptersView>();

        // add all the values from 1 to 15 to the arrayList
        // the items are of the type NumbersView

        arrayList.add(new ChaptersView(R.drawable.gfg, "Java"));
        arrayList.add(new ChaptersView(R.drawable.javat_point, "Python"));
        arrayList.add(new ChaptersView(R.drawable.w3_schools, "DSA"));
        arrayList.add(new ChaptersView(R.drawable.simpli_learn, "ML with Python"));
        arrayList.add(new ChaptersView(R.drawable.ibm, "Networking"));
        arrayList.add(new ChaptersView(R.drawable.github, "GitHub"));
        arrayList.add(new ChaptersView(R.drawable.interview_bit, "InterviewBit"));
        arrayList.add(new ChaptersView(R.drawable.aws, "SQL"));
        arrayList.add(new ChaptersView(R.drawable.linkedin, "LinkedIn"));


        // Now create the instance of the NumebrsViewAdapter and pass
        // the context and arrayList created above
        ChaptersViewAdapter numbersArrayAdapter = new ChaptersViewAdapter(this, arrayList);

        // create the instance of the ListView to set the numbersViewAdapter
        ListView numbersListView = findViewById(R.id.listView);

        // set the numbersViewAdapter for ListView
        numbersListView.setAdapter(numbersArrayAdapter);

        // Set the onItemClickListener to handle clicks
        numbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Define the URLs for each list item
                String[] urls = {
                        "https://www.geeksforgeeks.org/java/",  // java - GFG
                        "https://www.javatpoint.com/python-tutorial", // python - javaPoint
                        "https://www.w3schools.com/dsa/", // DSA-w3Schools
                        "https://www.simplilearn.com/big-data-and-analytics/machine-learning-certification-training-course",   // ML with Python
                        "https://www.ibm.com/topics/networking",  // Networking - IBM
                        "https://github.com/", // GitHub
                        "https://www.interviewbit.com/data-structure-interview-questions/", // interviewBit
                        "https://aws.amazon.com/what-is/sql/", // SQL
                        "https://www.linkedin.com" // LinkedIn
                };

                // Open the URL corresponding to the clicked item
                if (position < urls.length) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[position]));
                    startActivity(browserIntent);
                }
            }
        });
    }
}