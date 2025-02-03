package com.example.simple_linear_regression_model;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LinearRegression";
    Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        EditText inputEditText = findViewById(R.id.inputEditText);
        Button predictButton = findViewById(R.id.predictButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        // Load the TFLite model
        try {
            Log.d(TAG, "onCreate: Loading model...");
            MappedByteBuffer model = loadModelFile();

            if (model != null) {
                Log.d(TAG, "Model loaded successfully.");
                interpreter = new Interpreter(model);
            } else {
                Log.e(TAG, "Failed to load model.");
            }
        } catch (IOException e) {
            interpreter = null;
            Log.e(TAG, "onCreate: Exception occurred while loading the model" + e);
        }

        // Set up button click listener
        predictButton.setOnClickListener(v -> {
            String inputText = inputEditText.getText().toString();
            if (!inputText.isEmpty()) {
                try {
                    float inputValue = Float.parseFloat(inputText);
                    Log.d(TAG, "onCreate: inputText: " + inputText);
                    Log.d(TAG, "onCreate: inputValue: " + inputValue);

                    float[] inputArray = {inputValue};
                    float[][] outputArray = new float[1][1];

                    // Perform inference
                    if (interpreter != null) {
                        interpreter.run(inputArray, outputArray);
                        // Display the result
                        resultTextView.setText(String.format("Result: %.2f", outputArray[0][0]));
                    } else {
                        Log.e("InterpreterError", "Interpreter is null. Check initialization.");
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Input cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load the TFLite model file from assets
    private MappedByteBuffer loadModelFile() throws IOException {
        try (AssetFileDescriptor fileDescriptor = getAssets().openFd("simple_linear_regression_model.tflite");
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {

            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();

            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interpreter != null) {
            interpreter.close();
        }
    }
}
