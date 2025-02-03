package com.example.resnetimageclassification;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImageNetLabels {

    private static final String TAG = "ImageNetLabels";
    private static final String JSON_FILE = "imagenet_class_index.json";
    private Map<Integer, String> classMap;

    public ImageNetLabels(Context context) {
        classMap = new HashMap<>();
        loadLabelsFromJson(context);
    }

    private void loadLabelsFromJson(Context context) {
        try {
            InputStream is = context.getAssets().open(JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                int classIndex = Integer.parseInt(key);
//                String value = jsonObject.getString(key);
                String className = jsonObject.getJSONArray(key).getString(1); // Second value in array is class name
                classMap.put(classIndex, className);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading labels", e);
        }
    }

    public String getClassName(int index) {
        return classMap.getOrDefault(index, "Unknown Class");
    }
}
