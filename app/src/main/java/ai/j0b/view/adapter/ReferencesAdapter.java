package ai.j0b.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemReferencesBinding; // Make sure to have a corresponding layout
import ai.j0b.model.ReferencesModel;
import java.util.ArrayList;

public class ReferencesAdapter extends RecyclerView.Adapter<ReferencesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ReferencesModel> list;
    private OnClick listener;

    public interface OnClick {
        void onDeleteClick(ReferencesModel referenceModel);
        void onUpdateClick(ReferencesModel referenceModel);
    }

    public ReferencesAdapter(Context context, ArrayList<ReferencesModel> list, OnClick listener) {
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
        ItemReferencesBinding binding = ItemReferencesBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ReferencesModel data = list.get(position);
        holder.binding.tvName.setText(data.getName());
        holder.binding.tvEmail.setText(data.getEmail());
        holder.binding.tvPhone.setText(data.getNumber());
        holder.binding.tvOrganization.setText(data.getOrganization());
        holder.binding.tvDesignation.setText(data.getDesignation());

        holder.binding.ivDeleteReferences.setOnClickListener(view -> listener.onDeleteClick(data));
        holder.itemView.setOnClickListener(view -> listener.onUpdateClick(data));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemReferencesBinding binding;

        public ViewHolder(ItemReferencesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}