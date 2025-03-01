package com.example.imageclassification;

import android.content.Context;
import android.graphics.Bitmap;

public class MobileNetV2Classifier extends ImageClassifier {
    public MobileNetV2Classifier(Context context, String modelName) {
        super(context, modelName);
    }

    @Override
    public float[][][][] preprocessImage(Bitmap bitmap, int imageSize) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true);
        float[][][][] input = new float[1][299][299][3];

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
}
