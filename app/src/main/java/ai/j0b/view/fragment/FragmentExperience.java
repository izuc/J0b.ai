package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ai.j0b.databinding.FragmentExperienceBinding;
import ai.j0b.model.ExperienceModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.adapter.ExperienceAdapter;
import ai.j0b.view.dialog.AlertDialog;
import ai.j0b.view.dialog.ExperienceDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentExperience extends Fragment implements ExperienceAdapter.OnClick {
    private final ArrayList<ExperienceModel> experienceList = new ArrayList<>();
    private FragmentExperienceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExperienceBinding.inflate(inflater, container, false);
        setupUI();
        loadData();
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupUI() {
        binding.recycleView.setAdapter(new ExperienceAdapter(requireActivity(), experienceList, this));
        binding.recycleView.setNestedScrollingEnabled(false);
    }

    private void loadData() {
        String jsonExperience = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getExperienceKey());
        if (jsonExperience != null) {
            Type type = new TypeToken<ArrayList<ExperienceModel>>() {}.getType();
            List<ExperienceModel> list = new Gson().fromJson(jsonExperience, type);
            experienceList.addAll(list);
            onRecycleViewRefresh();
        }
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        binding.btnAdd.setOnClickListener(v -> showExperienceDialog(null));
    }

    private void showExperienceDialog(@Nullable ExperienceModel experienceModel) {
        ExperienceDialog dialog = new ExperienceDialog((AppCompatActivity) requireActivity(), experienceModel, newData -> {
            if (newData == null) return;

            if (experienceModel == null) {
                experienceList.add(newData);
            } else {
                int index = experienceList.indexOf(experienceModel);
                if (index != -1) {
                    experienceList.set(index, newData);
                }
            }

            saveExperienceList();
            Toast.makeText(requireActivity(), "Data Saved!", Toast.LENGTH_SHORT).show();
            onRecycleViewRefresh();
        });

        dialog.show();
    }

    private void saveExperienceList() {
        String json = new Gson().toJson(experienceList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getExperienceKey(), json);
    }

    @Override
    public void onDeleteClick(ExperienceModel data) {
        new AlertDialog((AppCompatActivity) requireActivity(), () -> {
            experienceList.remove(data);
            saveExperienceList();
            onRecycleViewRefresh();
        }).show();
    }

    @Override
    public void onUpdateClick(ExperienceModel data) {
        showExperienceDialog(data);
    }

    public void onRecycleViewRefresh() {
        if (binding.recycleView.getAdapter() != null) {
            binding.recycleView.getAdapter().notifyDataSetChanged();
        }

        if (experienceList.isEmpty()) {
            binding.recycleView.setVisibility(View.GONE);
            binding.noDataFound.noDataFound.setVisibility(View.VISIBLE);
        } else {
            binding.recycleView.setVisibility(View.VISIBLE);
            binding.noDataFound.noDataFound.setVisibility(View.GONE);
        }
    }
}
