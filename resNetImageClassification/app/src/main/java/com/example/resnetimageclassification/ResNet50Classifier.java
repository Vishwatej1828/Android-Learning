package com.example.resnetimageclassification;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

public class ResNet50Classifier {

    private static final String TAG = "ResNet50Classifier";
    private static final int IMAGE_SIZE = 224; // ResNet50 input size
    private static final int NUM_CLASSES = 1000; // ResNet50 output classes

    private final Interpreter tflite;
    private Context context;
    private ImageNetLabels imageNetLabels;

    public ResNet50Classifier(Context context, String modelName) {
        this.context = context;
        try {
            MappedByteBuffer modelFile = loadModelFile(context, modelName);
            tflite = new Interpreter(modelFile);
            imageNetLabels = new ImageNetLabels(this.context);
        } catch (IOException e) {
            throw new RuntimeException("Error loading TFLite model", e);
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        return FileUtil.loadMappedFile(context, modelName);
    }

    public Bitmap preprocessAndGetBitmap(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        return resizedBitmap;
    }

    public String predict(Bitmap bitmap) {
        float[][][][] inputTensor = preprocessImage(bitmap);
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(IMAGE_SIZE * IMAGE_SIZE * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                inputBuffer.putFloat(inputTensor[0][y][x][0]);
                inputBuffer.putFloat(inputTensor[0][y][x][1]);
                inputBuffer.putFloat(inputTensor[0][y][x][2]);
            }
        }

        float[][] output = new float[1][NUM_CLASSES];
        tflite.run(inputBuffer, output);

        int predictedIndex = getMaxIndex(output[0]);
        float confidence = output[0][predictedIndex];
        String predictedClass = imageNetLabels.getClassName(predictedIndex);
        return "Class: " + predictedClass + ", Confidence: " + confidence;
    }

    public float[][][][] preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        float[][][][] input = new float[1][IMAGE_SIZE][IMAGE_SIZE][3];

        // Mean values for ResNet50
        float meanR = 103.94f;
        float meanG = 116.78f;
        float meanB = 123.68f;

        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int pixel = resizedBitmap.getPixel(x, y);

                float r = ((pixel >> 16) & 0xFF);
                float g = ((pixel >> 8) & 0xFF);
                float b = (pixel & 0xFF);

                // Normalize by subtracting the mean values for each channel
                input[0][y][x][0] = r - meanR; // Red channel
                input[0][y][x][1] = g - meanG; // Green channel
                input[0][y][x][2] = b - meanB; // Blue channel
            }
        }

        return input;
    }

    private int getMaxIndex(float[] probabilities) {
        int maxIndex = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) maxIndex = i;
        }
        return maxIndex;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
        }
    }
}
