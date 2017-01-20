package com.example.m_fawzy.etg_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamedfawzy.etg_android.R;

public class Vision_Activity extends AppCompatActivity {

    private TextView mImageDetails;
    private TextView mImageDetails2;
    private ImageView mMainImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mImageDetails2 = (TextView) findViewById(R.id.image_details2);
        mMainImage = (ImageView) findViewById(R.id.main_image);

        Intent intent = getIntent();
        Bundle B = intent.getExtras();
        String imageDetails1 = B.get("imgDetails1").toString();
        String imageDetails2 = B.get("imgDetails2").toString();
        byte[] byteArray = B.getByteArray("image");
        Bitmap bit = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        mImageDetails.setText(imageDetails1);
        mImageDetails2.setText(imageDetails2);
        mMainImage.setImageBitmap(bit);

        setContentView(R.layout.activity_vision_);

        /*** Sara  ****/
        /*** Strings to be used in TTS are : imageDetails1 , imageDetails2  **/

    }
}
