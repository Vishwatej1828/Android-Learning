package com.example.imageclassification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";

    private ImageView imageView;
    private List<TextView> classTextViews;
    private List<TextView> confidenceTextViews;
    private List<ImageClassifier> classifiers;

    // ResNet50, VGG16, MobileNetV2 input size(224)
    // InceptionV3 input size 229
    private List<Integer> imageSize = List.of(224, 224, 299, 224);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        requestPermissions();
        loadClassifiers();

        findViewById(R.id.pickImageButton).setOnClickListener(v -> pickImageFromGallery());
        findViewById(R.id.cameraButton).setOnClickListener(v -> openCamera());
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        classTextViews = Arrays.asList(
                findViewById(R.id.txtResNet50Class),
                findViewById(R.id.txtVGG16Class),
                findViewById(R.id.txtInceptionV3Class),
                findViewById(R.id.txtMobileNetV2Class)
        );
        confidenceTextViews = Arrays.asList(
                findViewById(R.id.txtResNet50Confidence),
                findViewById(R.id.txtVGG16Confidence),
                findViewById(R.id.txtInceptionV3Confidence),
                findViewById(R.id.txtMobileNetV2Confidence)
        );
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void loadClassifiers() {
        classifiers = Arrays.asList(
                new ResNet50Classifier(this, "resNet50.tflite"),
                new VGG16Classifier(this, "vgg16.tflite"),
                new InceptionV3Classifier(this, "inceptionV3.tflite"),
                new MobileNetV2Classifier(this, "mobilenetV2.tflite")
        );
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        Bitmap bitmap = null;
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            Uri selectedImageUri = data.getData();
            try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri)) {
                if (inputStream != null) bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "Error loading image", e);
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            classifyImage(bitmap);
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

    private void classifyImage(Bitmap bitmap) {
        for (int i = 0; i < classifiers.size(); i++) {
            try {
                float[][][][] inputTensor = classifiers.get(i).preprocessImage(bitmap, imageSize.get(i));
                String prediction = classifiers.get(i).predict(inputTensor, imageSize.get(i));

                Log.d(TAG, "classifyImage: " + prediction);
                String[] result = extractClassConfidence(prediction);
                classTextViews.get(i).setText("\uD83D\uDD39" + result[0]);
                confidenceTextViews.get(i).setText("\uD83D\uDD39" + result[1]);
            } catch (Exception e) {
                Log.e(TAG, "Prediction error", e);
                classTextViews.get(i).setText("Error predicting image");
            }
        }
    }

    private String[] extractClassConfidence(String prediction) {
        return prediction.split(",");  // prediction format: "class: class_name, confidence: 0.1234"
    }
}
