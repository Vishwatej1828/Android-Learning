package com.example.resnetimageclassification;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

public class InceptionV3Classifier {

    private static final String TAG = "InceptionV3Classifier";
    private static final int IMAGE_SIZE = 299; // InceptionV3 input size
    private static final int NUM_CLASSES = 1000; // InceptionV3 output classes

    private final Interpreter tflite;
    private Context context;
    private ImageNetLabels imageNetLabels;

    public InceptionV3Classifier(Context context, String modelName) {
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

    public String predict(float[][][][] inputTensor) {
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
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true);
        float[][][][] input = new float[1][299][299][3];

        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int pixel = resizedBitmap.getPixel(x, y);

                float r = ((pixel >> 16) & 0xFF) / 127.5f - 1.0f;
                float g = ((pixel >> 8) & 0xFF) / 127.5f - 1.0f;
                float b = (pixel & 0xFF) / 127.5f - 1.0f;

                input[0][y][x][0] = r;
                input[0][y][x][1] = g;
                input[0][y][x][2] = b;
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
