package com.example.posedetectionapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.nio.file.Files;

public class PoseDetector {
    private Interpreter interpreter;
    private final Context context;

    public PoseDetector(Context context) throws IOException {
        try {
            this.context = context;
            MappedByteBuffer tfliteModel = loadModelFile(context, "model.tflite");
            interpreter = new Interpreter(tfliteModel);
        } catch (IOException e) {
            Log.e("PoseDetector", "IOException loading the tflite file", e);
            throw e; // Rethrow to handle it in the activity
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public float[] recognizeImage(Bitmap bitmap) {
        TensorImage tensorImage = new TensorImage();
        tensorImage.load(bitmap);

        // The model's output is a float array
        float[][] output = new float[1][4];
        interpreter.run(tensorImage.getBuffer(), output);
        return output[0];
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
