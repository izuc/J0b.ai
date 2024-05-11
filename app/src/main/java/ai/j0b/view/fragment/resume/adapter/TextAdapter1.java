package ai.j0b.view.fragment.resume.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ai.j0b.databinding.ItemText1Binding;
import java.util.List;

public class TextAdapter1 extends RecyclerView.Adapter<TextAdapter1.ViewHolder> {
    private final Context context;
    private final List<String> textList;

    public TextAdapter1(Context context, List<String> textList) {
        this.context = context;
        this.textList = textList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemText1Binding binding = ItemText1Binding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = textList.get(position);
        holder.bind(text);
    }

    @Override
    public int getItemCount() {
        return (textList != null) ? textList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemText1Binding binding;

        public ViewHolder(ItemText1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String text) {
            binding.tvText.setText(text);
        }
    }
}
