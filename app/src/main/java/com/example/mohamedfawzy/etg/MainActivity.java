package com.example.mohamedfawzy.etg;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedfawzy.etg.LocationUtils.LocationOperations;
import com.example.mohamedfawzy.etg.LocationUtils.LocationResponse;
import com.example.mohamedfawzy.etg_android.R;
import com.google.android.gms.location.places.Place;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    /******* Fawzy  *******/
    private static final String CLOUD_VISION_API_KEY = " AIzaSyAVKpfKs3ogNhlFBvUA41CL2QVpkBIUcrg";
    public static final String FILE_NAME = "temp.jpg";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    String storesResults[];  // to store the results in case of being lost
    TextView detailsText1 , detailsText2;
    /*********** 3bd el3al  ***************/
    LocationOperations locationOperations;
    Button btn;

    boolean checkLostOrVision;
    /* if visionButton is clicked checkLostOrVision will be false
     else if lostButton is clicked checkLostOrVision will be true */

    /*********** 3bd el3al  ***************/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    /*********** 3bd el3al  ***************/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*********** sara code  ***************/
        textToSpeech = new TextToSpeech(this, this);
       /*********** sara code  ***************/

        storesResults = new String[2];
        detailsText1 = (TextView) findViewById(R.id.detailsText1);
        detailsText2 = (TextView) findViewById(R.id.detailsText2);
        final Button visionButton = (Button) findViewById(R.id.button_vision);
        visionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCamera();

                checkLostOrVision=false;
                /** Sara **/
                /** Deal with the strings in detailsText1 , detailsText1 **/

               // speakOut(detailsText1.getText().toString());
            }
        });

        locationOperations=new LocationOperations(MainActivity.this);

        final Button locationButton = (Button) findViewById(R.id.button_location);
        locationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*** Mohamed Mostafa ****/
                /** You can use detailsText1 , detailsText2 TextViews to display your results **/

                locationOperations.getCurrentPlace();
                locationOperations.setOnLocationResponse(new LocationResponse() {
                    @Override
                    public void onLoctionDetected(Place currentPlace) {

                        try {
                            String s, ss;
                            s = currentPlace.getName().toString();
                            ss = currentPlace.getAddress().toString();
                            detailsText1.setText(s);
                            detailsText2.setText(ss);

                            speakOut(s);
                            speakOut(ss);
                        }catch (Exception e){}
                    }
                });
            }
        });

        btn =(Button)findViewById(R.id.button_lost);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /************* Vision *************/

                startCamera();
                checkLostOrVision=true;

                /************** Location **************//*
                locationOperations.getCurrentPlace();
                locationOperations.setOnLocationResponse(new LocationResponse() {
                    @Override
                    public void onLoctionDetected(Place currentPlace) {
                        String s,ss;
                        s=currentPlace.getName().toString();
                        ss=currentPlace.getAddress().toString();
                        detailsText1.setText(s);
                        detailsText2.setText(ss);

                        speakOut(s);
                        speakOut(ss);

                    }
                });*/
            }
        });

    }

    public void startCamera() {

        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uploadImage(Uri.fromFile(getCameraFile()));
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(
                requestCode,
                CAMERA_PERMISSIONS_REQUEST,
                grantResults)) {
            startCamera();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading

        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        detailsText1.setText("Loading...Please wait!");
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String[]>() {
            @Override
            protected String[] doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want

                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);

                            //Landmark Detection
                            Feature landmarkDetection = new Feature();
                            landmarkDetection.setType("LANDMARK_DETECTION");
                            landmarkDetection.setMaxResults(10);
                            add(landmarkDetection);
                        }});


                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                String errors[] = {"Cloud Vision API request failed. Check logs for details.", "Cloud Vision API request failed. Check logs for details."};
                return errors;
            }

            protected void onPostExecute(String[] result) {

                storesResults = result;
                detailsText1.setText(result[0]);
                detailsText2.setText(result[1]);

                /*********************** Speaking ***********************/
              //  speakOut(result[0]);
                //speakOut(result[1]);
                final String s1=result[0];
                final String s2=result[1];
                //speakOut(s1+s2);
                // check which button is clicked visionButton or lostButton
          if(checkLostOrVision) {
              /************** Location **************/
              locationOperations.getCurrentPlace();
              locationOperations.setOnLocationResponse(new LocationResponse() {
                  @Override
                  public void onLoctionDetected(Place currentPlace) {
                      // Sarah Idea
                      String s, ss,sss;
                      s =  s1+" "+s2;
                      ss = currentPlace.getName().toString()+" "+currentPlace.getAddress().toString();
                      detailsText1.setText(s);
                      detailsText2.setText(ss);

                     sss=s+" "+ss;
                     // speakOut(s);
                      speakOut(sss);
                      //speakOut(s1);
                      //speakOut(s2);

                  }
              });


          }
            }
        }.execute();
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);


            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }


    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
    private String[] convertResponseToString(BatchAnnotateImagesResponse response) {
        String [] messages = new String [2];
        messages[0] = "Labels:\n\n";
        messages[1] = "Landmarks:\n\n";


        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                messages[0] += String.format("%.3f: %s", label.getScore(), label.getDescription());
                messages[0] += "\n";
            }
        } else {
            messages[0] += "nothing";
        }

        List<EntityAnnotation> landmarks = response.getResponses().get(0).getLandmarkAnnotations();
        if (landmarks != null) {
            for (EntityAnnotation label : landmarks) {
                messages[1] += String.format("%.3f: %s", label.getScore(), label.getDescription());
                messages[1] += "\n";
            }
        } else {
            messages[1] += "nothing";
        }

        return messages;
    }
//
    /************************************************************/

    /*************** sarah code *****************/
    private TextToSpeech textToSpeech;

    @Override
    public void onInit(int status) {


        if (status == TextToSpeech.SUCCESS)
        {
            Toast.makeText(MainActivity.this,
                    "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();

            int  result = textToSpeech.setLanguage(Locale.UK);
            if ( result == TextToSpeech.LANG_NOT_SUPPORTED||result == TextToSpeech.LANG_MISSING_DATA)

            {
                Log.e("TTS", "This Language is not supported");
            }
            else
            {

                Log.e("TTS","Initialization success");
                //btn.setEnabled(true);
                //speakOut("Hello");

            }

        } else if(status==TextToSpeech.ERROR)
        {
            Log.e("TTS","Initialization failed");
            Toast.makeText(MainActivity.this,
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        }

    }

    public void speakOut(String text)
    {

        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null );

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
/*************** sarah code *****************/
}
