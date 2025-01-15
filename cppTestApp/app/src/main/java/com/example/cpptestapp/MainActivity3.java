package com.example.cpptestapp;

import android.app.Notification;
import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import androidx.core.content.ContextCompat;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main3);

        // Retrieve the counts from the Intent
        Intent intent = getIntent();
        int correctCount = intent.getIntExtra("correct_count", 0); // Default value is 0
        int wrongCount = intent.getIntExtra("wrong_count", 0); // Default value is 0

        // Display the counts in the TextView (you should have a TextView in your layout to display this)
        TextView correctAnswersText = findViewById(R.id.correctTextView);
        TextView wrongAnswersText = findViewById(R.id.wrongTextView);

        String correctAnswersResult = "Correct Answers: " + correctCount;
        String wrongAnswersResult = "Wrong Answers: " + wrongCount;

        correctAnswersText.setText(correctAnswersResult);
        wrongAnswersText.setText(wrongAnswersResult);


        // Initialize the PieChart
        PieChart marksPieChart = findViewById(R.id.totalMarksPieChart);

        // Ensure that the chart is properly cleared before adding new slices
        marksPieChart.clearChart();

        // Get colors from resources
        int correctColor = ContextCompat.getColor(this, R.color.green);
        int wrongColor = ContextCompat.getColor(this, R.color.red);
        
        // Set the data and color to the pie chart
        marksPieChart.addPieSlice(new PieModel("Correct", correctCount, correctColor));
        marksPieChart.addPieSlice(new PieModel("Wrong", wrongCount, wrongColor));

        // To animate the pie chart
        marksPieChart.startAnimation();
    }

    public void moveBack(View view) {
        finish();
    }
    public void closeApp(View view){
        finishAffinity();
    }
}