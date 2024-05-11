package ai.j0b.view.fragment.resume.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemEducation1Binding;
import ai.j0b.model.AcademicModel;
import java.util.List;

public class EducationAdapter1 extends RecyclerView.Adapter<EducationAdapter1.ViewHolder> {
    private final Context context;
    private final List<AcademicModel> academicModelList;

    public EducationAdapter1(Context context, List<AcademicModel> academicModelList) {
        this.context = context;
        this.academicModelList = academicModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEducation1Binding binding = ItemEducation1Binding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AcademicModel academicModel = academicModelList.get(position);
        holder.bind(academicModel);
    }

    @Override
    public int getItemCount() {
        return (academicModelList != null) ? academicModelList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEducation1Binding binding;

        public ViewHolder(ItemEducation1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AcademicModel academicModel) {
            binding.tvDegreeName.setText(academicModel.getName());
            binding.tvUniversityName.setText(academicModel.getInstitute());

            String yearInfo = academicModel.getYear();
            binding.tvYear.setText(yearInfo);
        }
    }
}
