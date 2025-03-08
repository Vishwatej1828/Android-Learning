package com.example.frameclassification;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class InceptionV3Classifier {
    protected String modelName;
    protected Context context;

    private static final int NUM_CLASSES = 1000; // Imagenet output classes

    protected Interpreter tflite;
    protected ImageNetLabels imageNetLabels;

    MappedByteBuffer modelFile;

    public InceptionV3Classifier(Context context, String modelName) {
        this.context = context;
        this.modelName = modelName;

        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException("Error loading TFLite model", e);
        }
    }

    public float[][][][] preprocessImage(Bitmap bitmap, int imageSize) {
        // resize the image the InceptionV3 Model expected inputshape
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true);
        float[][][][] input = new float[1][imageSize][imageSize][3];

        // prepare the input buffer with normalization.  (InceptionV3 expected normalization: [0,1])
        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
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

    public String predict(float[][][][] inputTensor, int imageSize) {

        int inputSize = imageSize * imageSize * 3 * 4; // 3 channels (RGB), 4 bytes per float
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(inputSize).order(ByteOrder.nativeOrder());


        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                inputBuffer.putFloat(inputTensor[0][y][x][0]);
                inputBuffer.putFloat(inputTensor[0][y][x][1]);
                inputBuffer.putFloat(inputTensor[0][y][x][2]);
            }
        }

        float[][] output = new float[1][NUM_CLASSES];

        if (tflite == null) {
            throw new IllegalStateException("TFLite model is not initialized.");
        }

//        Log.d("TFLite", "Allocated inputBuffer size: " + inputBuffer.capacity());
//        Log.d("TFLite", "Allocated output shape: [" + output.length + ", " + output[0].length + "]");
//
//        int[] inputShape = tflite.getInputTensor(0).shape();
//        Log.d("TFLite", "Expected Input Shape: " + Arrays.toString(inputShape));
//
//        int[] outputShape = tflite.getOutputTensor(0).shape();
//        Log.d("TFLite", "Expected Output Shape: " + Arrays.toString(outputShape));


        tflite.run(inputBuffer, output);

        int predictedIndex = getMaxIndex(output[0]);
        float confidence = output[0][predictedIndex];
        String predictedClass = imageNetLabels.getClassName(predictedIndex);
        return "Class: " + predictedClass + ", Confidence: " + confidence;
    }

    protected void init() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        modelFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
        tflite = new Interpreter(modelFile);
        imageNetLabels = new ImageNetLabels(this.context);
    }

    protected int getMaxIndex(float[] probabilities) {
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
