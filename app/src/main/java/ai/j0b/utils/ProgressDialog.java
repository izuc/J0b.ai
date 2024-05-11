package ai.j0b.utils;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import ai.j0b.databinding.DialogProgressbarBinding;

public final class ProgressDialog extends Dialog {
    private final AppCompatActivity context;
    private DialogProgressbarBinding dialogBinding;

    public ProgressDialog(AppCompatActivity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogProgressbarBinding binding = DialogProgressbarBinding.inflate(LayoutInflater.from(context));
        dialogBinding = binding;
        if (binding != null) {
            setContentView(binding.getRoot());
        }
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = getWindow();
        if (window != null) {
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
    }
}