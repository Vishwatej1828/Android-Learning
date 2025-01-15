package com.example.cpptestapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "CPP Questions App";

    private static final String[] CORRECT_ANSWERS = {
            "int array[10]",    // Q1
            "#define",            // Q2
            "Deriving new classes from existing classes",  // Q3
            "Concept of allowing overiding of functions",   // Q4
            "In Structures, members are public by default whereas, in Classes, they are private by default",  // Q5
            "->*",    // Q6
            "p now points to b", // Q7
            "All of the mentioned", // Q8
            "constructor",        // Q9
            ";"                   // Q10
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        Button submitTest = findViewById(R.id.submitTestBtn);


        submitTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Submit button clicked.");
                // Get the selected options
                ArrayList<String> selectedOptions = getSelectedOptions();
                Log.d(TAG, "Selected options count: " + selectedOptions.size());

                // Check if all questions are answered
                if (selectedOptions.size() == 10) { // Assuming 10 questions
                    int correctCount = 0;
                    int wrongCount = 0;
                    StringBuilder result = new StringBuilder("Selected Options:\n");

                    // Compare selected answers with correct answers
                    for (int i = 0; i < selectedOptions.size(); i++) {
                        String selectedAnswer = selectedOptions.get(i);
                        String correctAnswer = CORRECT_ANSWERS[i];

                        result.append("Q").append(i + 1).append(": ").append(selectedAnswer)
                                .append(" (Correct Answer: ").append(correctAnswer).append(")\n");

                        Log.d(TAG, "Q" + (i + 1) + " selected: " + selectedAnswer);
                        Log.d(TAG, "Q" + (i + 1) + " correct: " + correctAnswer);

                        // Compare answers
                        if (selectedAnswer.equals(correctAnswer)) {
                            correctCount++;
                        } else {
                            wrongCount++;
                        }
                    }

                    // Display the result
                    result.append("\nCorrect Answers: ").append(correctCount);
                    result.append("\nWrong Answers: ").append(wrongCount);

                    Log.d(TAG, "Correct Answers: " + correctCount + "   Wrong Answer: " + wrongCount);

                    Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                    intent.putExtra("correct_count", correctCount);
                    intent.putExtra("wrong_count", wrongCount);
                    startActivity(intent);
                /*
                    StringBuilder result = new StringBuilder("Selected Options:\n");

                    for (int i = 0; i < selectedOptions.size(); i++) {
                        result.append("Q").append(i).append(": ").append(selectedOptions.get(i)).append("\n");
                        Log.d(TAG, "Q" + (i) + " selected: " + selectedOptions.get(i));
                    }
                    // Display the selected options in a Toast
                    Toast.makeText(MainActivity2.this, result.toString(), Toast.LENGTH_LONG).show();
                */
                } else {
                    Log.e(TAG, "Not all questions are answered. Selected options count: " + selectedOptions.size());
                    Toast.makeText(MainActivity2.this, "Please answer all questions.", Toast.LENGTH_SHORT).show();
                }
            }

            private ArrayList<String> getSelectedOptions() {
                ArrayList<String> selectedOptions = new ArrayList<>();
                // Iterate through each question's RadioGroup
                for (int i = 0; i < 10; i++) { // Assuming question IDs are sequential
                    int radioGroupId = getResources().getIdentifier("radioGroupQ" + i, "id", getPackageName());
                    Log.d(TAG, "Checking RadioGroup ID: radioGroupQ" + i + ", Resolved ID: " + radioGroupId);

                    RadioGroup radioGroup = findViewById(radioGroupId);

                    if (radioGroup != null) {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        Log.d(TAG, "RadioGroupQ" + i + " selected ID: " + selectedId);

                        if (selectedId != -1) {
                            RadioButton selectedButton = findViewById(selectedId);
                            if (selectedButton != null) {
                                String selectedText = selectedButton.getText().toString();
                                selectedOptions.add(selectedText);
                                Log.d(TAG, "RadioButton selected text: " + selectedText);
                            } else {
                                Log.e(TAG, "RadioButton not found for ID: " + selectedId);
                            }
                        } else {
                            Log.e(TAG, "No option selected for RadioGroupQ" + i);
                        }
                    } else {
                        Log.e(TAG, "RadioGroup not found for ID: radioGroupQ" + i);
                    }
                }
                return selectedOptions;
            }
        });

    }


    public void moveBack(View view) {
        finish();
    }

    public void closeApp(View view) {
        finishAffinity();
    }
}