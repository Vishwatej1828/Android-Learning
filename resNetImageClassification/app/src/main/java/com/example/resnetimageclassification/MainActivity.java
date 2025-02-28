package com.example.resnetimageclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";

    private ImageView imageView;
    private TextView txtResNet50Class, txtResNet50Confidence,
            txtVGG16Class, txtVGG16Confidence,
            txtInceptionV3Class, txtInceptionV3Confidence,
            txtMobileNetV2Class, txtMobileNetV2Confidence;
    private ResNet50Classifier resNet50Classifier;
    private VGG16Classifier vgg16Classifier;
    private InceptionV3Classifier inceptionV3Classifier;
    private MobileNetV2Classifier mobileNetV2Classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);


        txtResNet50Class = findViewById(R.id.txtResNet50Class);
        txtResNet50Confidence = findViewById(R.id.txtResNet50Confidence);

        txtVGG16Class = findViewById(R.id.txtVGG16Class);
        txtVGG16Confidence = findViewById(R.id.txtVGG16Confidence);

        txtInceptionV3Class = findViewById(R.id.txtInceptionV3Class);
        txtInceptionV3Confidence = findViewById(R.id.txtInceptionV3Confidence);

        txtMobileNetV2Class = findViewById(R.id.txtMobileNetV2Class);
        txtMobileNetV2Confidence = findViewById(R.id.txtMobileNetV2Confidence);


        Button pickImageButton = findViewById(R.id.pickImageButton);
        Button cameraButton = findViewById(R.id.cameraButton);

        // Request permissions
        requestPermissions();

        // Load TFLite model
        Log.d(TAG, "Loading TFLite model...");
        resNet50Classifier = new ResNet50Classifier(this, "resNet50.tflite");
        vgg16Classifier = new VGG16Classifier(this, "vgg16.tflite");
        inceptionV3Classifier = new InceptionV3Classifier(this, "inceptionV3.tflite");
        mobileNetV2Classifier = new MobileNetV2Classifier(this, "mobilenetV2.tflite");


        // Pick Image from Gallery
        pickImageButton.setOnClickListener(v -> {
            Log.d(TAG, "Pick Image button clicked");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
        });

        // Open Camera Activity
        cameraButton.setOnClickListener(v -> {
            Log.d(TAG, "Camera button clicked");
            openCamera();
        });
    }

    private void requestPermissions() {
        Log.d(TAG, "Requesting permissions...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Gallery Permission not granted, requesting...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Gallery Permission already granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera Permission not granted, requesting...");
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Log.d(TAG, "Camera Permission already granted");
        }
    }

    private void openCamera() {

        // Check if camera permission is granted before opening the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera Permission granted, opening camera...");
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Log.d(TAG, "Camera Permission denied, requesting...");
            // Request camera permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + ", requestCode=" + requestCode + ", data=" + data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "Selected Image URI: " + selectedImageUri);

            if (selectedImageUri != null) {
                try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                    if (inputStream == null) {
                        Log.e(TAG, "Unable to open input stream");
                        Toast.makeText(this, "Unable to open input stream", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);

                    // Predict the class
                    try {
                        Log.d(TAG, "Starting prediction...");
                        float[][][][] inputTensor = resNet50Classifier.preprocessImage(bitmap);
                        String prediction = resNet50Classifier.predict(inputTensor);
                        Log.d(TAG, "ResNet50 Prediction: " + prediction);
                        String result[];
                        result = getClassConfi(prediction);
                        txtResNet50Class.setText("\uD83D\uDD39 Class: " + result[0]);
                        txtResNet50Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                        inputTensor = vgg16Classifier.preprocessImage(bitmap);
                        prediction = vgg16Classifier.predict(inputTensor);
                        Log.d(TAG, "VGG16 Prediction: " + prediction);
                        result = getClassConfi(prediction);
                        txtVGG16Class.setText("\uD83D\uDD39 Class: " + result[0]);
                        txtVGG16Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                        inputTensor = inceptionV3Classifier.preprocessImage(bitmap);
                        prediction = inceptionV3Classifier.predict(inputTensor);
                        Log.d(TAG, "InceptionV3 Prediction: " + prediction);
                        result = getClassConfi(prediction);
                        txtInceptionV3Class.setText("\uD83D\uDD39 Class: " + result[0]);
                        txtInceptionV3Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                        inputTensor = mobileNetV2Classifier.preprocessImage(bitmap);
                        prediction = mobileNetV2Classifier.predict(inputTensor);
                        Log.d(TAG, "MobileNetV2 Prediction: " + prediction);
                        result = getClassConfi(prediction);
                        txtMobileNetV2Class.setText("\uD83D\uDD39 Class: " + result[0]);
                        txtMobileNetV2Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);

                    } catch (Exception e) {
                        Log.e(TAG, "Prediction error: ", e);
                        txtResNet50Class.setText("Error predicting the image by ResNet50");
                        txtVGG16Class.setText("Error predicting the image by VGG16");
                        txtInceptionV3Class.setText("Error predicting the image by InceptionV3");
                        txtMobileNetV2Class.setText("Error predicting the image by MobileNetV2");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image", e);
                    txtResNet50Class.setText("Error loading image by ResNet50");
                    txtVGG16Class.setText("Error loading image by VGG16");
                    txtInceptionV3Class.setText("Error loading image by InceptionV3");
                    txtMobileNetV2Class.setText("Error loading image by MobileNetV2");

                }
            } else {
                Log.e(TAG, "No image selected or invalid URI");
                txtResNet50Class.setText("No image selected or invalid URI");
                txtVGG16Class.setText("No image selected or invalid URI");
                txtInceptionV3Class.setText("No image selected or invalid URI");
                txtMobileNetV2Class.setText("No image selected or invalid URI");

            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            // Predict the class
            try {
                Log.d(TAG, "Starting prediction...");
                float[][][][] inputTensor = resNet50Classifier.preprocessImage(bitmap);
                String prediction = resNet50Classifier.predict(inputTensor);

                Log.d(TAG, "ResNet50 Prediction: " + prediction);
                String result[];
                result = getClassConfi(prediction);
                txtResNet50Class.setText("\uD83D\uDD39 Class: " + result[0]);
                txtResNet50Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                inputTensor = vgg16Classifier.preprocessImage(bitmap);
                prediction = vgg16Classifier.predict(inputTensor);
                Log.d(TAG, "VGG16 Prediction: " + prediction);
                result = getClassConfi(prediction);
                txtVGG16Class.setText("\uD83D\uDD39 Class: " + result[0]);
                txtVGG16Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                inputTensor = inceptionV3Classifier.preprocessImage(bitmap);
                prediction = inceptionV3Classifier.predict(inputTensor);
                Log.d(TAG, "InceptionV3 Prediction: " + prediction);
                result = getClassConfi(prediction);
                txtInceptionV3Class.setText("\uD83D\uDD39 Class: " + result[0]);
                txtInceptionV3Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);


                inputTensor = mobileNetV2Classifier.preprocessImage(bitmap);
                prediction = mobileNetV2Classifier.predict(inputTensor);
                Log.d(TAG, "MobileNetV2 Prediction: " + prediction);
                result = getClassConfi(prediction);
                txtMobileNetV2Class.setText("\uD83D\uDD39 Class: " + result[0]);
                txtMobileNetV2Confidence.setText("\uD83D\uDD39 Confidence: " + result[1]);

            } catch (Exception e) {
                Log.e(TAG, "Prediction error: ", e);
                txtResNet50Class.setText("Error predicting the image");
                txtVGG16Class.setText("Error predicting the image");
                txtInceptionV3Class.setText("Error predicting the image");
                txtMobileNetV2Class.setText("Error predicting the image");
            }
        } else {
            Log.d(TAG, "Unhandled requestCode: " + requestCode);
            txtResNet50Class.setText("Failed to select image");
            txtVGG16Class.setText("Failed to select image");
            txtInceptionV3Class.setText("Failed to select image");
            txtMobileNetV2Class.setText("Failed to select image");
        }
    }

    public static String[] getClassConfi(String prediction) {
        // Extract class and confidence using regex
        String regex = "Class:\\s*(\\w+),\\s*Confidence:\\s*([0-9.]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(prediction);

        if (matcher.find()) {
            String className = matcher.group(1);
            String confidence = matcher.group(2);
            return new String[]{className, confidence};
        }

        // Return empty values if no match is found
        return new String[]{"", ""};
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resNet50Classifier != null) {
            resNet50Classifier.close();
            vgg16Classifier.close();
            inceptionV3Classifier.close();
            mobileNetV2Classifier.close();
        }
    }
}