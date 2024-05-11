package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import ai.j0b.databinding.DialogAcademicBinding;
import ai.j0b.model.AcademicModel;

public class AcademicDialog extends Dialog {

    private final AppCompatActivity context;
    private final AcademicModel data;
    private DialogAcademicBinding dialogBinding;
    private final OnAboutMeSelect listener;

    public interface OnAboutMeSelect {
        void onAddAcademic(AcademicModel academicModel);
    }

    public AcademicDialog(AppCompatActivity context, AcademicModel data, OnAboutMeSelect listener) {
        super(context);
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public AcademicModel getData() {
        return data;
    }

    public OnAboutMeSelect getListener() {
        return listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBinding = DialogAcademicBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        final DialogAcademicBinding binding = dialogBinding;

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (getData() != null) {
            binding.etName.setText(getData().getName());
            binding.etInstitute.setText(getData().getInstitute());
            binding.etYear.setText(getData().getYear());
            if (getData().getPercentage() != null) {
                binding.etPercentage.setText(getData().getPercentage());
                binding.rbPercentage.setChecked(true);
            } else if (getData().getCgpa() != null) {
                binding.etPercentage.setText(getData().getCgpa());
                binding.rbCgpa.setChecked(true);
            }
        }

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(binding.etName.getText());
                String institute = String.valueOf(binding.etInstitute.getText());
                String percentage = String.valueOf(binding.etPercentage.getText());
                String year = String.valueOf(binding.etYear.getText());

                if (name.isEmpty()) {
                    binding.etName.setError("Enter Course / Degree Name");
                    return;
                }
                if (institute.isEmpty()) {
                    binding.etInstitute.setError("Enter Institute");
                    return;
                }
                if (percentage.isEmpty()) {
                    binding.etPercentage.setError("Enter Percentage / CGPA");
                    return;
                }
                if (year.isEmpty()) {
                    binding.etYear.setError("Enter Graduated / Pursuing Year");
                    return;
                }

                listener.onAddAcademic(new AcademicModel(
                        name,
                        institute,
                        binding.rbPercentage.isChecked() ? percentage : null,
                        binding.rbCgpa.isChecked() ? percentage : null,
                        year
                ));
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