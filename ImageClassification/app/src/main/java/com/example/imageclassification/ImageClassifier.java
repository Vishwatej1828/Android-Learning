package com.example.imageclassification;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class ImageClassifier {
    protected String modelName;
    protected Context context;

    private static final int NUM_CLASSES = 1000; // Imagenet output classes

    protected Interpreter tflite;
    protected ImageNetLabels imageNetLabels;

    MappedByteBuffer modelFile;
    public ImageClassifier(Context context, String modelName) {
        this.context = context;
        this.modelName = modelName;

        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException("Error loading TFLite model", e);
        }
    }

    abstract float[][][][] preprocessImage(Bitmap bitmap, int imageSize);

    public String predict(float[][][][] inputTensor, int imageSize) {
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(imageSize * imageSize * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
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
