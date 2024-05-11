package ai.j0b.view.fragment.resume.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ai.j0b.R;
import ai.j0b.databinding.ItemReferences1Binding;
import ai.j0b.model.ReferencesModel;

import java.util.List;

public class ReferencesAdapter1 extends RecyclerView.Adapter<ReferencesAdapter1.ViewHolder> {
    private final Context context;
    private final List<ReferencesModel> referencesList;
    private final String color;

    public ReferencesAdapter1(Context context, List<ReferencesModel> referencesList, String color) {
        this.context = context;
        this.referencesList = referencesList;
        this.color = color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReferences1Binding binding = ItemReferences1Binding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReferencesModel reference = referencesList.get(position);
        holder.bind(reference, color, context);
    }

    @Override
    public int getItemCount() {
        return referencesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReferences1Binding binding;

        public ViewHolder(ItemReferences1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ReferencesModel reference, String color, Context context) {
            binding.tvNameReferences.setText(reference.getName());
            binding.tvDesignationReferences.setText(reference.getDesignation());
            binding.tvOrganizationReferences.setText(reference.getOrganization());

            // Construct the contact info string conditionally based on email and phone number availability
            String contactInfo = "";
            if (reference.getEmail() != null && !reference.getEmail().isEmpty()) {
                contactInfo += reference.getEmail();
            }
            if (reference.getNumber() != null && !reference.getNumber().isEmpty()) {
                // Add a separator if both email and phone number are present
                if (!contactInfo.isEmpty()) {
                    contactInfo += " / ";
                }
                contactInfo += reference.getNumber();
            }
            binding.tvContactReferences.setText(contactInfo);

            // Color handling remains the same
            int colorResId = getColorResourceId(color, context);
            if (colorResId != 0) { // 0 is an invalid resource ID.
                int resolvedColor = ContextCompat.getColor(context, colorResId);
                binding.tvNameReferences.setTextColor(resolvedColor);
                binding.tvDesignationReferences.setTextColor(resolvedColor);
                binding.tvOrganizationReferences.setTextColor(resolvedColor);
                binding.tvContactReferences.setTextColor(resolvedColor);
                binding.ivPointReferences.setImageTintList(ColorStateList.valueOf(resolvedColor));
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
