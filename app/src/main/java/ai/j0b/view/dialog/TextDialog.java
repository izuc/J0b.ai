package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import ai.j0b.databinding.DialogTextBinding;

public class TextDialog extends Dialog {
    private final AppCompatActivity context;
    private final String data;
    private DialogTextBinding dialogBinding;
    private final String hint;
    private final OnAboutMeSelect listener;

    public interface OnAboutMeSelect {
        void onAddText(String str);
    }

    public TextDialog(AppCompatActivity context, String data, String hint, OnAboutMeSelect listener) {
        super(context);
        this.context = context;
        this.data = data;
        this.hint = hint;
        this.listener = listener;
    }

    public String getData() {
        return data;
    }

    public String getHint() {
        return hint;
    }

    public OnAboutMeSelect getListener() {
        return listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBinding = DialogTextBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        final DialogTextBinding binding = dialogBinding;

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.tvTitle.setText(getHint());
        binding.etText.setHint("Enter " + getHint() + " here");

        if (getData() != null) {
            binding.etText.setText(getData());
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = String.valueOf(binding.etText.getText());
                if (text.isEmpty()) {
                    binding.etText.setError("Enter " + hint + "  here");
                    return;
                }
                listener.onAddText(text);
                dismiss();
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