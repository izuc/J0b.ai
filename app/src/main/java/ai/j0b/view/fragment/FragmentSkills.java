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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ai.j0b.databinding.FragmentSkillsBinding;
import ai.j0b.utils.Constant;
import ai.j0b.view.adapter.TextAdapter;
import ai.j0b.view.dialog.TextDialog;

import java.util.ArrayList;
import ai.j0b.R;

public class FragmentSkills extends Fragment implements TextAdapter.OnClick {
    private ArrayList<String> arrList = new ArrayList<>();
    private FragmentSkillsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSkillsBinding.inflate(inflater, container, false);
        initSkillsList();
        setupListeners();
        return binding.getRoot();
    }

    private void initSkillsList() {
        String skillsJson = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getSkillsKey());
        if (skillsJson != null) {
            arrList = new Gson().fromJson(skillsJson, new TypeToken<ArrayList<String>>() {}.getType());
            refreshSkillsList();
        }
    }

    private void setupListeners() {
        binding.ivBack.setOnClickListener(view -> requireActivity().onBackPressed());
        binding.btnAdd.setOnClickListener(view -> addNewSkill());
    }

    private void addNewSkill() {
        new TextDialog((AppCompatActivity) requireContext(), null, getString(R.string.skills), text -> {
            arrList.add(text.trim());
            saveSkillsList();
            refreshSkillsList();
        }).show();
    }

    private void refreshSkillsList() {
        TextAdapter adapter = new TextAdapter(requireContext(), arrList, getString(R.string.skills), this);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleView.setAdapter(adapter);
        binding.noDataFound.getRoot().setVisibility(arrList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void saveSkillsList() {
        String skillsJson = new Gson().toJson(arrList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getSkillsKey(), skillsJson);
        Toast.makeText(requireActivity(), "Data Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(String skill) {
        arrList.remove(skill);
        saveSkillsList();
        refreshSkillsList();
    }

    @Override
    public void onUpdateClick(final String oldSkill) {
        TextDialog dialog = new TextDialog((AppCompatActivity) requireContext(), oldSkill, getString(R.string.skills), newSkill -> {
            // Find the index of the old skill and replace it with the new skill
            int skillIndex = arrList.indexOf(oldSkill);
            if (skillIndex != -1) {
                arrList.set(skillIndex, newSkill.trim());
                saveSkillsList();
                refreshSkillsList();
            }
        });
        dialog.show();
    }
}
