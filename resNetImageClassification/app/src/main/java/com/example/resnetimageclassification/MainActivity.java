package com.example.resnetimageclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE_GALLERY = 50;
    private static final int PERMISSION_REQUEST_CODE_CAMERA = 100;
    private static final String TAG = "MainActivity";

    private ImageView imageView;
    private TextView resultTextView;
    private ResNet50Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        Button pickImageButton = findViewById(R.id.pickImageButton);
        Button cameraButton = findViewById(R.id.cameraButton);

        // Load TFLite model
        Log.d(TAG, "Loading TFLite model...");
        classifier = new ResNet50Classifier(this, "resNet50.tflite");

        // Pick Image from Gallery
        pickImageButton.setOnClickListener(v -> {
            Log.d(TAG, "Pick Image button clicked");
            requestGalleryPermission();
        });

        // Open Camera Activity
        cameraButton.setOnClickListener(v -> {
            Log.d(TAG, "Camera button clicked");
            requestCameraPermission();
        });
    }

    private void requestGalleryPermission() {
        Log.d(TAG, "Requesting Gallery permissions...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "requestGalleryPermission: Gallery Permission is already granted");
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE_GALLERY);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "requestGalleryPermission: Gallery Permission is already granted");
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE_GALLERY);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE_GALLERY);
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PERMISSION_REQUEST_CODE_CAMERA);
    }

    private void requestCameraPermission() {
        Log.d(TAG, "Requesting Camera permissions...");
        // Check if camera permission is granted before opening the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera Permission not granted, requesting...");
            // Request camera permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
        } else {
            Log.d(TAG, "Camera Permission granted, opening camera...");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Gallery Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Gallery Permission Granted");

                // Permission granted, open gallery
                openGallery();
            } else {
                Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Gallery Permission Denied");
            }
        }
        else if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Camera Permission Granted");

                // Permission granted, open camera
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Camera Permission Denied");
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + ", requestCode=" + requestCode + ", data=" + data);

        if (requestCode == PERMISSION_REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "Selected Image URI: " + selectedImageUri);

            if (selectedImageUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                    if (inputStream == null) {
                        Log.e(TAG, "Unable to open input stream");
                        resultTextView.setText("Unable to open input stream");
                        return;
                    }

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);

                    // Predict the class
                    try {
                        Log.d(TAG, "Starting prediction...");
                        Bitmap processedBitmap = classifier.preprocessAndGetBitmap(bitmap);
                        String prediction = classifier.predict(processedBitmap);

                        Log.d(TAG, "Prediction: " + prediction);
                        resultTextView.setText(prediction);
                    } catch (Exception e) {
                        Log.e(TAG, "Prediction error: ", e);
                        resultTextView.setText("Error predicting the image");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image", e);
                    resultTextView.setText("Error loading image");
                }
            } else {
                Log.e(TAG, "No image selected or invalid URI");
                resultTextView.setText("No image selected or invalid URI");
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            // Predict the class
            try {
                Log.d(TAG, "Starting prediction...");
                Bitmap processedBitmap = classifier.preprocessAndGetBitmap(bitmap);
                String prediction = classifier.predict(processedBitmap);

                Log.d(TAG, "Prediction: " + prediction);
                resultTextView.setText(prediction);
            } catch (Exception e) {
                Log.e(TAG, "Prediction error: ", e);
                resultTextView.setText("Error predicting the image");
            }
        } else {
            Log.d(TAG, "Unhandled requestCode: " + requestCode);
            resultTextView.setText("Failed to select image");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) {
            classifier.close();
        }
    }
}