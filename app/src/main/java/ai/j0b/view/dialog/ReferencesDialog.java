package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ai.j0b.databinding.DialogReferencesBinding; // Ensure you have this layout file created
import ai.j0b.model.ReferencesModel;

public class ReferencesDialog extends Dialog {
    private final AppCompatActivity context;
    private final ReferencesModel data;
    private final OnAddReference listener;
    private DialogReferencesBinding dialogBinding;

    public interface OnAddReference {
        void onAddReference(ReferencesModel referencesModel);
    }

    public ReferencesDialog(AppCompatActivity context, ReferencesModel data, OnAddReference listener) {
        super(context);
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogBinding = DialogReferencesBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        if (data != null) {
            dialogBinding.etNameReferences.setText(data.getName());
            dialogBinding.etEmailReferences.setText(data.getEmail());
            dialogBinding.etNumberReferences.setText(data.getNumber());
            dialogBinding.etOrganizationReferences.setText(data.getOrganization());
            dialogBinding.etDesignationReferences.setText(data.getDesignation());
        }

        dialogBinding.ivBackReferences.setOnClickListener(view -> dismiss());

        dialogBinding.btnSaveReferences.setOnClickListener(view -> {
            String name = dialogBinding.etNameReferences.getText().toString().trim();
            String email = dialogBinding.etEmailReferences.getText().toString().trim();
            String number = dialogBinding.etNumberReferences.getText().toString().trim();
            String organization = dialogBinding.etOrganizationReferences.getText().toString().trim();
            String designation = dialogBinding.etDesignationReferences.getText().toString().trim();

            if (validateFields(name, email, number, organization, designation)) {
                ReferencesModel newReference = new ReferencesModel(name, email, number, organization, designation);
                listener.onAddReference(newReference);
                dismiss();
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

    private boolean validateFields(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return false; // You can extend this to set error on specific fields as needed
            }
        }
        return true;
    }
}
