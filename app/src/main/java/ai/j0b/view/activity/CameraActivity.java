package ai.j0b.view.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import ai.j0b.model.PhotoData;
import ai.j0b.utils.Constant;
import ai.j0b.view.fragment.FragmentPersonalInformation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CameraActivity extends AppCompatActivity {
    private String currentPhotoPath;
    private Fragment frag;
    private PhotoData photoData;

    public abstract void showPhotos();

    public PhotoData getPhotoData() {
        return photoData;
    }

    public void setPhotoData(PhotoData photoData) {
        this.photoData = photoData;
    }

    public Fragment getFrag() {
        return frag;
    }

    public void setFrag(Fragment fragment) {
        this.frag = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoData = new PhotoData(this);
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = photoData != null ? photoData.createImageFile() : null;
            photoData.setPhotoFile(photoFile);
            currentPhotoPath = photoFile != null ? photoFile.getAbsolutePath() : null;
        } catch (IOException e) {
            // Error occurred while creating the File
        }

        File photoFile = photoData != null ? photoData.getPhotoFile() : null;
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            photoData.setUri(photoURI);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, Constant.CAMERA_REQUEST_CODE);
        }
    }

    public void dispatchPickUpPictureIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, Constant.GALLERY_REQUEST_CODE);
    }

    public boolean checkAndRequestPermissions() {
        return checkAndRequestPermissions(Constant.REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    public boolean checkAndRequestPermissions(int requestCode) {
        int wExtStorePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (wExtStorePermission != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            Log.e("TAG", "checkAndRequestPermissions android 13: ");
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            File f = currentPhotoPath != null ? new File(currentPhotoPath) : null;
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = f != null ? Uri.fromFile(f) : null;
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            // Save the rotated image file
            if (currentPhotoPath != null) {
                try {
                    photoData.saveRotatedImageFile(currentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            showPhotos();
        }
        if (requestCode == Constant.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImage = data != null ? data.getData() : null;
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver != null ? contentResolver.query(selectedImage, filePathColumn, null, null, null) : null;
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    currentPhotoPath = filePath;
                    File photoFile = filePath != null ? new File(filePath) : null;
                    photoData.setPhotoFile(photoFile);
                    photoData.setUri(Uri.fromFile(photoFile));
                    cursor.close();

                    // Save the rotated image file
                    if (filePath != null) {
                        try {
                            photoData.saveRotatedImageFile(filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    showPhotos();
                }
            }

            Log.e("onActivityResult: ", String.valueOf(photoData != null ? photoData.getUri() : null));
            Log.e("tag", "onActivityResult: Gallery Image Uri: " + currentPhotoPath);
        }

        if (frag instanceof FragmentPersonalInformation) {
            ((FragmentPersonalInformation) frag).onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), CameraActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Log.e("TAG", "checkAndRequestPermissions android 13: ");
                }
            }
        } else if (requestCode == Constant.REQUEST_ID_CAMERA_PERMISSIONS) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        } else if (requestCode == Constant.REQUEST_ID_GALLERY_PERMISSIONS) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                Log.e("TAG", "checkAndRequestPermissions android 13: ");
            }
        }
    }
}