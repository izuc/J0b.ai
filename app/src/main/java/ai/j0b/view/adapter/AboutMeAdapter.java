package ai.j0b.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.ItemAboutMeBinding;
import java.util.ArrayList;

public class AboutMeAdapter extends RecyclerView.Adapter<AboutMeAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private OnClick listener;

    public interface OnClick {
        void onCloseClick(String str);
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

    public AboutMeAdapter(Context context, ArrayList<String> list, OnClick listener) {
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
        ItemAboutMeBinding historyBinding = ItemAboutMeBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(historyBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String data = list.get(position);
        holder.getBinding().tvText.setText(data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCloseClick(data);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAboutMeBinding binding;

        public ViewHolder(ItemAboutMeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemAboutMeBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemAboutMeBinding binding) {
            this.binding = binding;
        }
    }
}