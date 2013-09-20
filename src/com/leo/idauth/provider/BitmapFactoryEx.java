/* Copyright (C) 2011, Motorola, Inc,
 * All Rights Reserved
 * Class name: BitmapFactoryEx.java
 * Description: What the class does.
 *
 * Modification History:
 **********************************************************
 * Date     Author      Comments
 *
 **********************************************************
 */

package com.leo.idauth.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class BitmapFactoryEx extends BitmapFactory {
    final static String TAG = "BitmapFactoryEx";
    static HashMap<String, Bitmap> hashStringBitmap = new HashMap<String, Bitmap>();
    static HashMap<Integer, Bitmap> hashResBitmap = new HashMap<Integer, Bitmap>();

    static class WorkThread extends Thread {
        public Handler mHandler;
        public boolean bReady = false;

        public void run() {
            Looper.prepare();
            mHandler = new Handler();
            bReady = true;
            Looper.loop();
        }
    }

    static WorkThread myWorkThread = new WorkThread();
    static {
        myWorkThread.start();
    }

    public static Bitmap decodeFile(String filepath) {
        if (hashStringBitmap.containsKey(filepath))
            return hashStringBitmap.get(filepath);

        Bitmap b = BitmapFactory.decodeFile(filepath);
        hashStringBitmap.put(filepath, b);
        return b;
    }

    public static void decodeFileAsync(final ImageView iv, final String filepath) {
        while (!myWorkThread.bReady) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
        }
        myWorkThread.mHandler.post(new Runnable() {
            Bitmap b = null;

            @Override
            public void run() {
                if (hashStringBitmap.containsKey(filepath)){
                    b = hashStringBitmap.get(filepath);
                } else{
                    b = BitmapFactory.decodeFile(filepath);
                    hashStringBitmap.put(filepath, b);
                }
                //TODO: to add handle bitmap logic
                /*
                TV.singleton.mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        iv.setImageBitmap(b);
                    }
                });
                */

            }
        });

    }
    public static void decodeFileAsyncAndconvert(final ImageView iv, final String filepath) {
        while (!myWorkThread.bReady) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
            }
        }
        myWorkThread.mHandler.post(new Runnable() {
            Bitmap b = null;
            @Override
            public void run() {
                if (hashStringBitmap.containsKey(filepath)){
                    b = hashStringBitmap.get(filepath);
                } else {
                    b = BitmapFactory.decodeFile(filepath);
                    hashStringBitmap.put(filepath, b);
                }
                //TODO: to add handle bitmap logic
                /*                
                   TV.singleton.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BitmapDrawable bd = new BitmapDrawable(b);
                        Drawable cbd = convertToGrayscale(bd);
                        iv.setImageDrawable(cbd);
                    }
                });
                */
            }
        });
    }
    static protected Drawable convertToGrayscale(Drawable drawable)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        drawable.setColorFilter(filter);
        return drawable;
    }

    public static Bitmap decodeResourceEx(Resources res, int id) {
        if (hashResBitmap.containsKey(Integer.valueOf(id))) {
            return hashResBitmap.get(Integer.valueOf(id));
        }
        Bitmap b = BitmapFactory.decodeResource(res, id);
        hashResBitmap.put(Integer.valueOf(id), b);

        return b;
    }

    public static void clearCache() {
        Iterator it = hashStringBitmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Bitmap b = (Bitmap) pairs.getValue();
            b.recycle();
        }

        hashStringBitmap.clear();

        it = hashResBitmap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Bitmap b = (Bitmap) pairs.getValue();
            b.recycle();
        }
        hashResBitmap.clear();
    }
}
