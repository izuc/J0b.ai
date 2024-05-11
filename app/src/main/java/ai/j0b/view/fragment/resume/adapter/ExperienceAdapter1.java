package ai.j0b.view.fragment.resume.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.R;
import ai.j0b.databinding.ItemExperience1Binding;
import ai.j0b.model.ExperienceModel;
import java.util.List;

public class ExperienceAdapter1 extends RecyclerView.Adapter<ExperienceAdapter1.ViewHolder> {
    private final Context context;
    private final List<ExperienceModel> experienceModelList;
    private final String color;

    public ExperienceAdapter1(Context context, List<ExperienceModel> experienceModelList, String color) {
        this.context = context;
        this.experienceModelList = experienceModelList;
        this.color = color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExperience1Binding binding = ItemExperience1Binding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExperienceModel experienceModel = experienceModelList.get(position);
        holder.bind(experienceModel, color, context);
    }

    @Override
    public int getItemCount() {
        return (experienceModelList != null) ? experienceModelList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemExperience1Binding binding;

        public ViewHolder(ItemExperience1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ExperienceModel experienceModel, String color, Context context) {
            binding.tvCompanyName.setText(experienceModel.getOrganization());
            binding.tvYearName.setText(String.format("%s to %s", experienceModel.getFromDate(), experienceModel.getToDate()));
            binding.tvPosition.setText(experienceModel.getRole());

            int colorResId = getColorResourceId(color, context);
            if (colorResId != 0) { // 0 is an invalid resource ID.
                int resolvedColor = ContextCompat.getColor(context, colorResId);
                binding.tvCompanyName.setTextColor(resolvedColor);
                binding.tvYearName.setTextColor(resolvedColor);
                binding.tvPosition.setTextColor(resolvedColor);
                binding.ivPoint.setImageTintList(ColorStateList.valueOf(resolvedColor));
            }
        }

        private int getColorResourceId(String color, Context context) {
            switch (color) {
                case "color1":
                    return R.color.r_t_1;
                case "color2":
                    return R.color.r_t_2;
                default:
                    return 0; // Consider a default color or handle this case.
            }
        }
    }
}
