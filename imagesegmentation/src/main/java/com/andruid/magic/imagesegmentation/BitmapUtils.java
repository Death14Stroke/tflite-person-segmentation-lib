package com.andruid.magic.imagesegmentation;

import android.graphics.Bitmap;

public class BitmapUtils {
    private static float ratioX, ratioY;

    public static Bitmap resize(Bitmap src){
        int originalX = src.getWidth();
        int originalY = src.getHeight();
        ratioX = originalX / (float)ModelAPI.INPUT_SIZE;
        ratioY = originalY / (float)ModelAPI.INPUT_SIZE;
        return Bitmap.createScaledBitmap(src, ModelAPI.INPUT_SIZE, ModelAPI.INPUT_SIZE, false);
    }
}
