package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import ai.j0b.databinding.DialigAlertBinding;

public class AlertDialog extends Dialog {

    private final AppCompatActivity context;
    private DialigAlertBinding dialogBinding;
    private final OnClick listener;

    public interface OnClick {
        void onYesClick();
    }

    public AlertDialog(AppCompatActivity context, OnClick listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    public OnClick getListener() {
        return listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBinding = DialigAlertBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        DialigAlertBinding binding = dialogBinding;

        binding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onYesClick();
            }
        });

        setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }
}