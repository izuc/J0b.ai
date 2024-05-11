package ai.j0b.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import ai.j0b.databinding.FragmentPersonalInformationBinding;
import ai.j0b.model.PhotoData;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.CameraActivity;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.activity.SystemConfiguration;
import ai.j0b.view.dialog.AboutMeDialog;
import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;

public class FragmentPersonalInformation extends FragmentCamera {
    private FragmentPersonalInformationBinding binding;
    private String image = "";
    private final ArrayList<String> aboutMeList = new ArrayList<>(Arrays.asList(
            "I am an organized and efficient person with an enquiring mind.",
            "I am a flexible person seeking employment which will allow development, growth and make use of my existing skills.",
            "I am a good listener and learner, and am able to communicate well with people from all walks of life."
            // Add more items as needed
    ));

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FragmentPersonalInformationBinding binding = FragmentPersonalInformationBinding.inflate(inflater, container, false);
        SystemConfiguration.setTransparentStatusBar(requireActivity(), SystemConfiguration.IconColor.ICON_LIGHT);
        this.binding = binding;

        if (binding != null) {
            binding.ivBack.setOnClickListener(view -> onBackButtonClicked());
            loadProfileImage();
            loadPersonalData();
            binding.ivProfile.setOnClickListener(view -> photoSelection());
            binding.tvSelectAboutMe.setOnClickListener(view -> showAboutMeDialog());
            binding.btnSubmit.setOnClickListener(view -> submitPersonalData());
        }

        return binding != null ? binding.getRoot() : null;
    }

    private void onBackButtonClicked() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onBackPressed();
        }
    }

    private void loadProfileImage() {
        Constant constant = Constant.INSTANCE;
        FragmentActivity activity = requireActivity();
        String imageData = constant.getData(activity, Constant.INSTANCE.getProfileImageKey());
        if (imageData != null) {
            this.image = imageData;
            Glide.with(requireActivity()).load(this.image).into(binding.ivProfile);
        }
    }

    private void loadPersonalData() {
        loadData(Constant.INSTANCE.getNameKey(), binding.etName);
        loadData(Constant.INSTANCE.getNumberKey(), binding.etNumber);
        loadData(Constant.INSTANCE.getEmailKey(), binding.etEmail);
        loadData(Constant.INSTANCE.getAddressKey(), binding.etAddress);
        loadData(Constant.INSTANCE.getFieldKey(), binding.etField);
        loadData(Constant.INSTANCE.getAboutMeKey(), binding.etAboutMe);
    }

    private void loadData(String key, AppCompatEditText editText) {
        Constant constant = Constant.INSTANCE;
        FragmentActivity activity = requireActivity();
        String data = constant.getData(activity, key);
        if (data != null) {
            editText.setText(data);
        }
    }

    private void showAboutMeDialog() {
        AboutMeDialog dialog = new AboutMeDialog((AppCompatActivity) requireActivity(), aboutMeList, text -> {
            Editable currentText = binding.etAboutMe.getText();
            if (currentText != null) {
                currentText.clear();
            }
            binding.etAboutMe.setText(text);
        });
        dialog.show();
    }

    @SuppressLint("WrongConstant")
    private void submitPersonalData() {
        if (image.isEmpty()) {
            Toast.makeText(requireActivity(), "Please Select Profile Image", 0).show();
            return;
        }

        String name = binding.etName.getText().toString().trim();
        String number = binding.etNumber.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String field = binding.etField.getText().toString().trim();
        String aboutMe = binding.etAboutMe.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError("Please Enter Name");
            return;
        }
        if (number.isEmpty()) {
            binding.etNumber.setError("Please Enter Mobile Number");
            return;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Please Enter Email Address");
            return;
        }
        if (address.isEmpty()) {
            binding.etAddress.setError("Please Enter Your Address");
            return;
        }
        if (field.isEmpty()) {
            binding.etField.setError("Please Enter Your Field");
            return;
        }
        if (aboutMe.isEmpty()) {
            binding.etAboutMe.setError("Please Enter About Me");
            return;
        }

        saveData(Constant.INSTANCE.getProfileImageKey(), image);
        saveData(Constant.INSTANCE.getNameKey(), name);
        saveData(Constant.INSTANCE.getNumberKey(), number);
        saveData(Constant.INSTANCE.getEmailKey(), email);
        saveData(Constant.INSTANCE.getAddressKey(), address);
        saveData(Constant.INSTANCE.getFieldKey(), field);
        saveData(Constant.INSTANCE.getAboutMeKey(), aboutMe);

        Toast.makeText(requireActivity(), "Data Saved !", 0).show();
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onBackPressed();
        }
    }

    private void saveData(String key, String value) {
        Constant constant = Constant.INSTANCE;
        FragmentActivity activity = requireActivity();
        constant.saveData(activity, key, value);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && (getActivity() instanceof CameraActivity)) {
            FragmentActivity activity = getActivity();
            if (activity instanceof CameraActivity) {
                PhotoData photoData = ((CameraActivity) activity).getPhotoData();
                File file = photoData != null ? photoData.getPhotoFile() : null;
                if (file != null) {
                    this.image = Constant.INSTANCE.createBase64(file);
                }
                RequestBuilder<Drawable> load = Glide.with(requireActivity()).load(file);
                load.into(binding.ivProfile);
            }
        }
    }
}