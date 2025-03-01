package com.example.imageclassification;

import android.content.Context;
import android.graphics.Bitmap;

public class VGG16Classifier extends ImageClassifier {
    public VGG16Classifier(Context context, String modelName) {
        super(context, modelName);
    }

    @Override
    public float[][][][] preprocessImage(Bitmap bitmap, int imageSize) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true);
        float[][][][] input = new float[1][imageSize][imageSize][3];

        // Mean values for ResNet50
        float meanR = 103.94f;
        float meanG = 116.78f;
        float meanB = 123.68f;

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
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
}
