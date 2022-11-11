package com.example.dionisiopet.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ImageHelper {

    public static void saveFile(Context context, String fileName, byte[] data) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getFile(Context context, String fileName) throws IOException{
        File image = context.getFileStreamPath(fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);

        return bitmap;
    }

    public static void deleteFile(Context context, String fileName){
        context.deleteFile(fileName);
    }
}
