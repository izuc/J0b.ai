package ai.j0b.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemAcademicBinding;
import ai.j0b.model.AcademicModel;
import java.util.ArrayList;

public class AcademicAdapter extends RecyclerView.Adapter<AcademicAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AcademicModel> list;
    private OnClick listener;

    public interface OnClick {
        void onDeleteClick(AcademicModel academicModel);
        void onUpdateClick(AcademicModel academicModel);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public OnClick getListener() {
        return listener;
    }

    public void setListener(OnClick listener) {
        this.listener = listener;
    }

    public AcademicAdapter(Context context, ArrayList<AcademicModel> list, OnClick listener) {
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
        ItemAcademicBinding historyBinding = ItemAcademicBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(historyBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AcademicModel data = list.get(position);
        holder.getBinding().tvCourse.setText(String.valueOf(data.getName()));
        holder.getBinding().tvInstitute.setText(String.valueOf(data.getInstitute()));

        if (data.getPercentage() == null) {
            holder.getBinding().tvPercentage.setText(String.valueOf(data.getCgpa()));
            holder.getBinding().tvPercentageTemp.setText("CGPA : ");
        } else {
            holder.getBinding().tvPercentage.setText(data.getPercentage() + "%");
            holder.getBinding().tvPercentageTemp.setText("Percentage : ");
        }

        holder.getBinding().tvYear.setText(String.valueOf(data.getYear()));

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
        private ItemAcademicBinding binding;

        public ViewHolder(ItemAcademicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemAcademicBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemAcademicBinding binding) {
            this.binding = binding;
        }
    }
}