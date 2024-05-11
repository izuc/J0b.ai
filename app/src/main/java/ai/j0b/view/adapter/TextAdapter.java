package ai.j0b.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.R;
import ai.j0b.databinding.ItemTextBinding;
import java.util.ArrayList;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;
    private OnClick listener;
    private String text;

    public interface OnClick {
        void onDeleteClick(String str);
        void onUpdateClick(String str);
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

    public TextAdapter(Context context, ArrayList<String> list, String text, OnClick listener) {
        this.context = context;
        this.list = list;
        this.text = text;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ItemTextBinding historyBinding = ItemTextBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(historyBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String data = list.get(position);
        holder.getBinding().tvText.setText(data);

        if (text.equals(context.getString(R.string.skills))) {
            holder.getBinding().ivIcon.setImageResource(R.drawable.icon_skills);
        } else if (text.equals(context.getString(R.string.hobbies))) {
            holder.getBinding().ivIcon.setImageResource(R.drawable.icon_hobbies);
        } else if (text.equals(context.getString(R.string.language))) {
            holder.getBinding().ivIcon.setImageResource(R.drawable.icon_language);
        }

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
        private ItemTextBinding binding;

        public ViewHolder(ItemTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemTextBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemTextBinding binding) {
            this.binding = binding;
        }
    }
}