package com.andruid.magic.imagesegmentationlib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.andruid.magic.imagesegmentation.BitmapUtils;
import com.andruid.magic.imagesegmentation.ModelAPI;
import com.andruid.magic.imagesegmentation.Segmentor;
import com.andruid.magic.imagesegmentationlib.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 0;
    private ActivityMainBinding binding;
    private Segmentor segmentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.fab.setOnClickListener(v -> pickImage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            segmentor = ModelAPI.create(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        segmentor.close();
    }

    public void pickImage(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getApplicationContext(),response.getPermissionName()+" denied",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            if(data==null)
                return;
            Uri uri = data.getData();
            Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Bitmap resized = BitmapUtils.resize(resource);
                            Bitmap outBitmap = segmentor.segment(resized);
                            binding.imageView.setImageBitmap(outBitmap);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
    }
}