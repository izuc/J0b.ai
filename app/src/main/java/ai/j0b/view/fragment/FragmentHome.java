package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ai.j0b.R;
import ai.j0b.databinding.FragmentHomeBinding;
import ai.j0b.model.SampleModel;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.adapter.SampleAdapter;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setupUI();
        return binding.getRoot();
    }

    private void setupUI() {
        ArrayList<SampleModel> sampleList = new ArrayList<>();
        sampleList.add(new SampleModel(0, R.drawable.sample_1, "Resume 1", R.color.card1));
        sampleList.add(new SampleModel(1, R.drawable.sample_2, "Resume 2", R.color.card2));
        sampleList.add(new SampleModel(2, R.drawable.sample_3, "Resume 3", R.color.card3));
        sampleList.add(new SampleModel(3, R.drawable.sample_4, "Resume 4", R.color.card4));
        sampleList.add(new SampleModel(4, R.drawable.sample_5, "Resume 5", R.color.card5));
        sampleList.add(new SampleModel(5, R.drawable.sample_6, "Resume 6", R.color.card1));
        sampleList.add(new SampleModel(6, R.drawable.sample_7, "Resume 7", R.color.card2));
        sampleList.add(new SampleModel(7, R.drawable.sample_8, "Resume 8", R.color.card3));
        sampleList.add(new SampleModel(8, R.drawable.sample_9, "Resume 9", R.color.card4));

        binding.recycleView.setAdapter(new SampleAdapter(requireActivity(), sampleList, this::onSampleItemClick));
    }

    private void onSampleItemClick(SampleModel data, int position, View itemView) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onResumeDownloadClick(position);
        }
    }
}
