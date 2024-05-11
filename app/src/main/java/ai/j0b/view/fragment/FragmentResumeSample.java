package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.R;
import ai.j0b.databinding.FragmentResumeSampleBinding;
import ai.j0b.model.SampleModel;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.activity.SystemConfiguration;
import ai.j0b.view.adapter.SampleAdapter;
import java.util.ArrayList;
import java.util.Arrays;

public class FragmentResumeSample extends Fragment {

    private FragmentResumeSampleBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResumeSampleBinding.inflate(inflater, container, false);
        SystemConfiguration.setTransparentStatusBar(requireActivity(), SystemConfiguration.IconColor.ICON_LIGHT);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).onBackPressed();
            }
        });

        ArrayList<SampleModel> list = new ArrayList<>(Arrays.asList(
                new SampleModel(0, R.drawable.sample_1, null, R.color.card1),
                new SampleModel(1, R.drawable.sample_2, null, R.color.card2),
                new SampleModel(2, R.drawable.sample_3, null, R.color.card3),
                new SampleModel(3, R.drawable.sample_4, null, R.color.card4),
                new SampleModel(4, R.drawable.sample_5, null, R.color.card5),
                new SampleModel(5, R.drawable.sample_6, null, R.color.card1),
                new SampleModel(6, R.drawable.sample_7, null, R.color.card2),
                new SampleModel(7, R.drawable.sample_8, null, R.color.card3),
                new SampleModel(8, R.drawable.sample_9, null, R.color.card4)
        ));

        binding.recycleView.setAdapter(new SampleAdapter(requireActivity(), list, new SampleAdapter.OnClick() {
            @Override
            public void onClick(SampleModel data, int position, View itemView) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onResumeDownloadClick(position);
                }
            }
        }));

        return binding.getRoot();
    }
}