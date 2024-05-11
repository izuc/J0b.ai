package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ai.j0b.R;
import ai.j0b.databinding.FragmentMyResumeBinding;
import ai.j0b.model.SampleModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.activity.SystemConfiguration;
import ai.j0b.view.adapter.SampleAdapter;
import java.util.ArrayList;

public class FragmentMyResume extends Fragment {

    private final ArrayList<Integer> arrList = new ArrayList<>();
    private FragmentMyResumeBinding binding;

    public ArrayList<Integer> getArrList() {
        return arrList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyResumeBinding.inflate(inflater, container, false);
        SystemConfiguration.setTransparentStatusBar(requireActivity(), SystemConfiguration.IconColor.ICON_LIGHT);

        String savedData = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getMyResumeKey());
        if (savedData != null) {
            arrList.addAll(new Gson().fromJson(savedData, new TypeToken<ArrayList<Integer>>() {}.getType()));
        }

        ArrayList<SampleModel> list = new ArrayList<>();
        for (Integer temp : arrList) {
            switch (temp) {
                case 0:
                    list.add(new SampleModel(0, R.drawable.sample_1, null, R.color.card1));
                    break;
                case 1:
                    list.add(new SampleModel(1, R.drawable.sample_2, null, R.color.card2));
                    break;
                case 2:
                    list.add(new SampleModel(2, R.drawable.sample_3, null, R.color.card2));
                    break;
                case 3:
                    list.add(new SampleModel(3, R.drawable.sample_4, null, R.color.card4));
                    break;
                case 4:
                    list.add(new SampleModel(4, R.drawable.sample_5, null, R.color.card5));
                    break;
                case 5:
                    list.add(new SampleModel(5, R.drawable.sample_6, null, R.color.card1));
                    break;
                case 6:
                    list.add(new SampleModel(6, R.drawable.sample_7, null, R.color.card2));
                    break;
                case 7:
                    list.add(new SampleModel(7, R.drawable.sample_8, null, R.color.card3));
                    break;
                case 8:
                    list.add(new SampleModel(8, R.drawable.sample_9, null, R.color.card4));
                    break;
            }
        }

        binding.recycleView.setAdapter(new SampleAdapter(requireActivity(), list, new SampleAdapter.OnClick() {
            @Override
            public void onClick(SampleModel data, int position, View itemView) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onResumeDownloadClick(position);
                }
            }
        }));

        if (arrList.size() != 0) {
            binding.recycleView.setVisibility(View.VISIBLE);
            binding.noDataFound.noDataFound.setVisibility(View.GONE);
        } else {
            binding.recycleView.setVisibility(View.GONE);
            binding.noDataFound.noDataFound.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }
}