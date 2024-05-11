package ai.j0b.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemExperienceBinding;
import ai.j0b.model.ExperienceModel;
import java.util.ArrayList;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ExperienceModel> list;
    private OnClick listener;

    public interface OnClick {
        void onDeleteClick(ExperienceModel experienceModel);
        void onUpdateClick(ExperienceModel experienceModel);
    }

    public ExperienceAdapter(Context context, ArrayList<ExperienceModel> list, OnClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ItemExperienceBinding historyBinding = ItemExperienceBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(historyBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ExperienceModel data = list.get(position);
        holder.getBinding().tvOrganization.setText(String.valueOf(data.getOrganization()));
        holder.getBinding().tvExpDate.setText(data.getFromDate() + " to " + data.getToDate());
        holder.getBinding().tvRole.setText(String.valueOf(data.getRole()));

        holder.getBinding().ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteClick(data);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onUpdateClick(data);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemExperienceBinding binding;

        public ViewHolder(ItemExperienceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemExperienceBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemExperienceBinding binding) {
            this.binding = binding;
        }
    }
}