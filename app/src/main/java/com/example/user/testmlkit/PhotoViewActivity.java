package com.example.user.testmlkit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource;
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions;
import com.squareup.picasso.Picasso;

import java.nio.ByteBuffer;

public class PhotoViewActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView mTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        imageView = findViewById(R.id.image_view_prediction);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        run();
    }


    public void run()
    {

        try {
            getCloudModel();
            runInference();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    private void runInference() throws FirebaseMLException {
        FirebaseModelInterpreter firebaseInterpreter = createInterpreter();

        if( firebaseInterpreter == null){
            String TAG="ERROR";
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
            return;
        }

        float[][][][] input = bitmapToInputArray();

        //Bitmap mSelectedImage = getYourInputImage();

        //ByteBuffer imgData = convertBitmapToByteBuffer(mSelectedImage, mSelectedImage.getWidth(),
        //       mSelectedImage.getHeight());

        FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();


        // [START mlkit_run_inference]
        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input)  // add() as many input arrays as your model requires
                .build();



        firebaseInterpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {
                                float[][] output = result.getOutput(0);
                                float[] probabilities = output[0];

                                for(float i : probabilities){
                                    Log.e("prob",""+i);
                                }


                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FAIL","FAIL "+e.getMessage());
                    }
                }
        );

        // [END mlkit_run_inference]

    }

    private Bitmap getYourInputImage() {
        // This method is just for show
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        return bitmap;
    }

    private float[][][][] bitmapToInputArray() {

        Bitmap bitmap = getYourInputImage();
        int batchNum = 0;
        float[][][][] input = new float[1][32][32][3];
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                int pixel = bitmap.getPixel(x, y);
                input[batchNum][x][y][0] = Color.red(pixel) / 255.0f;
                input[batchNum][x][y][1] = Color.green(pixel) / 255.0f;
                input[batchNum][x][y][2] = Color.blue(pixel) / 255.0f;
            }
        }


        return input;
    }


    private void getCloudModel(){

        FirebaseModelDownloadConditions.Builder conditionsBuilder =
                new FirebaseModelDownloadConditions.Builder().requireWifi();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Enable advanced conditions on Android Nougat and newer.
            conditionsBuilder = conditionsBuilder
                    .requireCharging()
                    .requireDeviceIdle();
        }

        FirebaseModelDownloadConditions conditions = conditionsBuilder.build();

        // Build a FirebaseCloudModelSource object by specifying the name you assigned the model
        // when you uploaded it in the Firebase console.
        FirebaseCloudModelSource cloudSource = new FirebaseCloudModelSource.Builder("fruit-detector")
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build();

        boolean result = FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource);


        Log.e("%%%", "msg "+result);

    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {

        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 32, 32, 3})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 3})
                        .build();

        return inputOutputOptions;
    }

    private FirebaseModelInterpreter createInterpreter() throws FirebaseMLException {
        FirebaseModelOptions options = new FirebaseModelOptions.Builder()
                .setCloudModelName("fruit-detector")
                //.setLocalModelName("my_local_model")
                .build();
        FirebaseModelInterpreter firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);

        return firebaseInterpreter;
    }
}
