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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ai.j0b.R;
import ai.j0b.databinding.FragmentHobbiesBinding;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.adapter.TextAdapter;
import ai.j0b.view.dialog.AlertDialog;
import ai.j0b.view.dialog.TextDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentHobbies extends Fragment implements TextAdapter.OnClick {
    private ArrayList<String> hobbiesList = new ArrayList<>();
    private FragmentHobbiesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHobbiesBinding.inflate(inflater, container, false);
        setupUI();
        loadHobbies();
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupUI() {
        binding.recycleView.setAdapter(new TextAdapter(requireActivity(), hobbiesList, getString(R.string.hobbies), this));
        binding.recycleView.setNestedScrollingEnabled(false);
    }

    private void loadHobbies() {
        String jsonHobbies = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getHobbiesKey());
        if (jsonHobbies != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> loadedHobbies = new Gson().fromJson(jsonHobbies, type);
            hobbiesList.addAll(loadedHobbies);
            onRecycleViewRefresh();
        }
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        binding.btnAdd.setOnClickListener(v -> showDialog(null));
    }

    private void showDialog(@Nullable String hobby) {
        TextDialog dialog = new TextDialog((AppCompatActivity) requireActivity(), hobby, getString(R.string.hobbies), newText -> {
            if (newText == null || newText.trim().isEmpty()) return;

            if (hobby == null) {
                hobbiesList.add(newText.trim());
            } else {
                int index = hobbiesList.indexOf(hobby);
                if (index != -1) {
                    hobbiesList.set(index, newText.trim());
                }
            }

            saveHobbies();
            Toast.makeText(requireActivity(), R.string.data_saved, Toast.LENGTH_SHORT).show();
            onRecycleViewRefresh();
        });

        dialog.show();
    }

    private void saveHobbies() {
        String json = new Gson().toJson(hobbiesList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getHobbiesKey(), json);
    }

    public void onRecycleViewRefresh() {
        if (binding.recycleView.getAdapter() != null) {
            binding.recycleView.getAdapter().notifyDataSetChanged();
        }

        binding.noDataFound.noDataFound.setVisibility(hobbiesList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recycleView.setVisibility(hobbiesList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDeleteClick(String hobby) {
        new AlertDialog((AppCompatActivity) requireActivity(), () -> {
            hobbiesList.remove(hobby);
            saveHobbies();
            onRecycleViewRefresh();
        }).show();
    }

    @Override
    public void onUpdateClick(String hobby) {
        showDialog(hobby);
    }
}
