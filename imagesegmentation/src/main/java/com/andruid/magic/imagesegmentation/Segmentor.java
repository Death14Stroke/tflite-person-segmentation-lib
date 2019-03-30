package com.andruid.magic.imagesegmentation;

import android.graphics.Bitmap;

public interface Segmentor {
    Bitmap segment(Bitmap bitmap);

    void close();
}