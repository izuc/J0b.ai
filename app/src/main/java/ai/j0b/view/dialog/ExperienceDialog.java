package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ai.j0b.databinding.DialogExperienceBinding;
import ai.j0b.model.ExperienceModel;
import ai.j0b.utils.Constant;

public class ExperienceDialog extends Dialog {
    private final Calendar calendarFrom;
    private final Calendar calendarTo;
    private final AppCompatActivity context;
    private final ExperienceModel data;
    private DialogExperienceBinding dialogBinding;
    private String fromDate;
    private boolean isCheck;
    private final OnAddExperience listener;
    private String toDate;

    public interface OnAddExperience {
        void onAddExperience(ExperienceModel experienceModel);
    }

    public ExperienceDialog(AppCompatActivity context, ExperienceModel data, OnAddExperience listener) {
        super(context);
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.toDate = "";
        this.fromDate = "";
        this.calendarFrom = Calendar.getInstance();
        this.calendarTo = Calendar.getInstance();
    }

    public ExperienceModel getData() {
        return data;
    }

    public OnAddExperience getListener() {
        return listener;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogBinding = DialogExperienceBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        final DialogExperienceBinding binding = dialogBinding;

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (getData() != null) {
            binding.etOrganization.setText(getData().getOrganization());
            binding.etRole.setText(getData().getRole());
            binding.etFromDate.setText(getData().getFromDate());
            binding.etToDate.setText(getData().getToDate());
//            if (getData().isContinue()) {
//                calendarFrom.setTime(Constant.INSTANCE.strToCalendar(getData().getFromDate()).getTime());
//                binding.checkCurrentWorking.setChecked(true);
//                setCheck(true);
//            } else {
//                calendarFrom.setTime(Constant.INSTANCE.strToCalendar(getData().getFromDate()).getTime());
//                calendarTo.setTime(Constant.INSTANCE.strToCalendar(getData().getToDate()).getTime());
//            }
        }

//        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                                Calendar fromCal = Calendar.getInstance();
//                                fromCal.setTimeInMillis(0);
//                                fromCal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
//                                fromDate = new SimpleDateFormat("dd-MM-yyyy").format(fromCal.getTime());
//                                binding.etFromDate.setText(fromDate);
//                                calendarFrom.setTime(fromCal.getTime());
//                            }
//                        },
//                        calendarFrom.get(Calendar.YEAR),
//                        calendarFrom.get(Calendar.MONTH),
//                        calendarFrom.get(Calendar.DAY_OF_MONTH)
//                );
//                datePickerDialog.show(context.getSupportFragmentManager(), "DatePickerDialog");
//            }
//        });
//
//        binding.etToDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!binding.checkCurrentWorking.isChecked()) {
//                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
//                            new DatePickerDialog.OnDateSetListener() {
//                                @Override
//                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
//                                    Calendar fromCal = Calendar.getInstance();
//                                    fromCal.setTimeInMillis(0);
//                                    fromCal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
//                                    toDate = new SimpleDateFormat("dd-MM-yyyy").format(fromCal.getTime());
//                                    binding.etToDate.setText(toDate);
//                                    calendarTo.setTime(fromCal.getTime());
//                                }
//                            },
//                            calendarTo.get(Calendar.YEAR),
//                            calendarTo.get(Calendar.MONTH),
//                            calendarTo.get(Calendar.DAY_OF_MONTH)
//                    );
//                    datePickerDialog.show(context.getSupportFragmentManager(), "DatePickerDialog");
//                }
//            }
//        });

        binding.checkCurrentWorking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck = !isCheck;
                if (isCheck) {
                    binding.etToDate.setText("Continue");
                    toDate = "";
                } else {
                    Editable text = binding.etToDate.getText();
                    if (text != null) {
                        text.clear();
                    }
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String organization = String.valueOf(binding.etOrganization.getText());
                String role = String.valueOf(binding.etRole.getText());
                String fromDate = String.valueOf(binding.etFromDate.getText());
                String toDate = String.valueOf(binding.etToDate.getText());
                boolean isContinue = binding.checkCurrentWorking.isChecked();

                if (organization.isEmpty()) {
                    binding.etOrganization.setError("Enter Organization");
                    return;
                }
                if (fromDate.isEmpty()) {
                    binding.etFromDate.setError("Enter From Date");
                    return;
                }
                if (toDate.isEmpty()) {
                    binding.etToDate.setError("Enter To Date");
                    return;
                }
                if (role.isEmpty()) {
                    binding.etRole.setError("Enter Role in Organization");
                    return;
                }

                listener.onAddExperience(new ExperienceModel(organization, fromDate, toDate, isContinue, role));
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