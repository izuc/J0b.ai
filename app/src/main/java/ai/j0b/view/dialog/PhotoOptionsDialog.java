package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import ai.j0b.databinding.DialogPhotoOptionBinding;

public class PhotoOptionsDialog extends Dialog {
    private final AppCompatActivity context;
    private DialogPhotoOptionBinding dialogbinding;
    private final onOptionSelected listener;

    public interface onOptionSelected {
        void onCameraClick();
        void onGalleryClick();
    }

    public PhotoOptionsDialog(AppCompatActivity context, onOptionSelected listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogPhotoOptionBinding inflate = DialogPhotoOptionBinding.inflate(LayoutInflater.from(context));
        dialogbinding = inflate;
        if (inflate != null) {
            setContentView(inflate.getRoot());
        }

        if (dialogbinding != null) {
            AppCompatImageView closeIcon = dialogbinding.iconClose;
            if (closeIcon != null) {
                closeIcon.setOnClickListener(view -> dismiss());
            }

            AppCompatTextView submitButton = dialogbinding.btnDone;
            if (submitButton != null) {
                submitButton.setOnClickListener(view -> {
                    if (dialogbinding.gallery.isChecked() && listener != null) {
                        listener.onGalleryClick();
                    }
                    if (dialogbinding.camera.isChecked() && listener != null) {
                        listener.onCameraClick();
                    }
                    dismiss();
                });
            }
        }

        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
    }
}