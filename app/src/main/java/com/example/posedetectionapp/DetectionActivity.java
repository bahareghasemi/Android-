package com.example.posedetectionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class DetectionActivity extends AppCompatActivity {
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private WebView webView;
    private ImageView poseImage;
    private Interpreter tflite;
    private PoseDetector poseDetector;
    private TensorFlowImageClassifier classifier;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        poseImage = findViewById(R.id.poseImage);

        try {
            classifier = new TensorFlowImageClassifier(this, "model.tflite");
        } catch (IOException e) {
            Toast.makeText(this, "Error loading model", Toast.LENGTH_LONG).show();
            finish();
        }

        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> openImageSelector());
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    poseImage.setImageBitmap(bitmap);
                    handleImage(bitmap);  // Make sure to use the loaded bitmap
                } catch (IOException e) {
                    Log.e("DetectionActivity", "Error processing image", e);
                }
            }
        }
    }


    private void handleImage(Bitmap image) {
        float[] results = classifier.classify(image);
        displayResults(results);
    }

    private void displayResults(float[] results) {
        if (results == null || results.length != 4) {
            Toast.makeText(this, "Invalid results", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] poses = {"Standing", "Sitting", "Sleeping", "Running"};
        float maxProb = 0;
        int maxIndex = -1;
        for (int i = 0; i < results.length; i++) {
            if (results[i] > maxProb) {
                maxProb = results[i];
                maxIndex = i;
            }
        }
        if (maxIndex != -1) {
            String message = "Detected pose: " + poses[maxIndex] + " with probability: " + maxProb;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to determine pose", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        classifier.close();
    }
}





