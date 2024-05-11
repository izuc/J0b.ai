package ai.j0b.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemSampleBinding;
import ai.j0b.model.SampleModel;
import java.util.ArrayList;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SampleModel> list;
    private OnClick listener;

    public interface OnClick {
        void onClick(SampleModel sampleModel, int position, View itemView);
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

    public SampleAdapter(Context context, ArrayList<SampleModel> list, OnClick listener) {
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
        ItemSampleBinding historyBinding = ItemSampleBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(historyBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SampleModel data = list.get(position);

        if (data.getText() != null) {
            holder.getBinding().tvText.setText(data.getText());
        } else {
            holder.getBinding().tvText.setVisibility(View.GONE);
        }

        if (data.getColor() != null) {
            holder.getBinding().cardColor.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), data.getColor())));
        }

        if (data.getImage() != null) {
            holder.getBinding().ivImage.setImageResource(data.getImage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.getPosition() != null) {
                    getListener().onClick(data, data.getPosition(), holder.itemView);
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSampleBinding binding;

        public ViewHolder(ItemSampleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemSampleBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemSampleBinding binding) {
            this.binding = binding;
        }
    }
}