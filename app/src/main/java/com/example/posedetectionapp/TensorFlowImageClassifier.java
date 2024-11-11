package com.example.posedetectionapp;

import android.content.Context;
import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.TensorImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class TensorFlowImageClassifier {
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 255.0f;
    private static final int INPUT_SIZE = 224;
    private Interpreter interpreter;
    private List<String> labelList;
    private TensorOperator probabilityProcessor;
    private ImageProcessor imageProcessor;

    public TensorFlowImageClassifier(Context context, String modelPath) throws IOException {
        this.interpreter = new Interpreter(loadModelFile(context, modelPath));
        setUpImageProcessor();
    }

    private void setUpImageProcessor() {
        this.imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                .build();
    }

    public TensorImage processImage(Bitmap bitmap) {
        TensorImage tensorImage = new TensorImage();
        tensorImage.load(bitmap);
        return imageProcessor.process(tensorImage);
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        FileInputStream fis = new FileInputStream(context.getAssets().openFd(modelName).getFileDescriptor());
        FileChannel fileChannel = fis.getChannel();
        long startOffset = context.getAssets().openFd(modelName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Method to run inference using the interpreter
    public float[] classify(Bitmap bitmap) {
        TensorImage inputImage = processImage(bitmap);

        float[][] output = new float[1][4];
        interpreter.run(inputImage.getBuffer(), output);
        return output[0];
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}
