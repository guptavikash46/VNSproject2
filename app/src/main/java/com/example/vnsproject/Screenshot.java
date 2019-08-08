package com.example.vnsproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Screenshot {
    private Bitmap bitmap;
    private String fileName, root, fullDirectory;

    public  Bitmap takeScreenshot(View v, Context context){
        Date now = new Date();


        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/VNSproject");
        if (myDir.exists()) myDir.delete();
        myDir.mkdirs();

        fileName = "ScreenShot_"+now+".jpg";
        fullDirectory = root + "/DCIM/VNSproject/"+fileName;
        File file = new File(myDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Log.e("ERROR", file.getAbsolutePath());


        return bitmap;
    }

    public Bitmap returnRootViewOfScreenshot(View v, Context c){
        return takeScreenshot(v.getRootView(), c);
    }

    public String getImageFilePath() {
        return fullDirectory;
    }

}
