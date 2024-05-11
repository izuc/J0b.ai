package ai.j0b.view.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.CameraActivity;
import ai.j0b.view.dialog.PhotoOptionsDialog;

public abstract class FragmentCamera extends Fragment implements PhotoOptionsDialog.onOptionSelected {

    public void photoSelection() {
        if (getActivity() instanceof CameraActivity) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CameraActivity) {
                if (((CameraActivity) activity).checkAndRequestPermissions(0)) {
                    FragmentActivity currentActivity = getActivity();
                    if (currentActivity instanceof AppCompatActivity) {
                        new PhotoOptionsDialog((AppCompatActivity) currentActivity, this).show();
                    }
                }
            }
        }
    }

    @Override
    public void onCameraClick() {
        if (getActivity() instanceof CameraActivity) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CameraActivity) {
                if (((CameraActivity) activity).checkAndRequestPermissions(Constant.REQUEST_ID_CAMERA_PERMISSIONS)) {
                    ((CameraActivity) activity).dispatchTakePictureIntent();
                }
            }
        }
    }

    @Override
    public void onGalleryClick() {
        if (getActivity() instanceof CameraActivity) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CameraActivity) {
                if (((CameraActivity) activity).checkAndRequestPermissions(Constant.REQUEST_ID_GALLERY_PERMISSIONS)) {
                    ((CameraActivity) activity).dispatchPickUpPictureIntent();
                }
            }
        }
    }
}