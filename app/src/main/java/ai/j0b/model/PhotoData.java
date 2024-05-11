package ai.j0b.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoData {
    private final Context context;
    private File photoFile;
    private Uri uri;

    public PhotoData(Context context) {
        this.context = context;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File file) {
        photoFile = file;
    }

    public File createImageFile() throws IOException {
        String imageFileName = String.valueOf(System.currentTimeMillis());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File tempFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return tempFile;
    }

    public File createImageFilePng() throws IOException {
        String imageFileName = String.valueOf(System.currentTimeMillis());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File tempFile = File.createTempFile(imageFileName, ".png", storageDir);
        return tempFile;
    }

    public void saveRotatedImageFile(String imagePath) throws IOException {
        // Read the original image file
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        // Get the EXIF orientation
        ExifInterface exif = new ExifInterface(imagePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        // Rotate the bitmap based on the orientation
        Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);

        // Save the rotated bitmap to the image file
        FileOutputStream outputStream = new FileOutputStream(imagePath);
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        // Recycle the bitmaps to free up memory
        bitmap.recycle();
        rotatedBitmap.recycle();
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}