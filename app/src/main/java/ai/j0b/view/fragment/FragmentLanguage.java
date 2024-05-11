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
import ai.j0b.databinding.FragmentLanguageBinding;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.adapter.TextAdapter;
import ai.j0b.view.dialog.AlertDialog;
import ai.j0b.view.dialog.TextDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentLanguage extends Fragment implements TextAdapter.OnClick {
    private ArrayList<String> languageList = new ArrayList<>();
    private FragmentLanguageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        setupUI();
        loadLanguages();
        return binding.getRoot();
    }

    private void setupUI() {
        binding.ivBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        binding.btnAdd.setOnClickListener(v -> showLanguageDialog(null));
        binding.recycleView.setAdapter(new TextAdapter(requireActivity(), languageList, getString(R.string.language), this));
        binding.recycleView.setNestedScrollingEnabled(false);
    }

    private void loadLanguages() {
        String jsonLanguages = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getLanguageKey());
        if (jsonLanguages != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            List<String> loadedLanguages = new Gson().fromJson(jsonLanguages, type);
            languageList.addAll(loadedLanguages);
            onRecycleViewRefresh();
        }
    }

    private void showLanguageDialog(@Nullable String language) {
        TextDialog dialog = new TextDialog((AppCompatActivity) requireActivity(), language, getString(R.string.language), newText -> {
            if (newText == null || newText.trim().isEmpty()) return;

            if (language == null) {
                languageList.add(newText.trim());
            } else {
                int index = languageList.indexOf(language);
                if (index != -1) {
                    languageList.set(index, newText.trim());
                }
            }

            saveLanguages();
            Toast.makeText(requireActivity(), R.string.data_saved, Toast.LENGTH_SHORT).show();
            onRecycleViewRefresh();
        });

        dialog.show();
    }

    private void saveLanguages() {
        String json = new Gson().toJson(languageList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getLanguageKey(), json);
    }

    public void onRecycleViewRefresh() {
        if (binding.recycleView.getAdapter() != null) {
            binding.recycleView.getAdapter().notifyDataSetChanged();
        }

        binding.noDataFound.noDataFound.setVisibility(languageList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.recycleView.setVisibility(languageList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDeleteClick(String language) {
        new AlertDialog((AppCompatActivity) requireActivity(), () -> {
            languageList.remove(language);
            saveLanguages();
            onRecycleViewRefresh();
        }).show();
    }

    @Override
    public void onUpdateClick(String language) {
        showLanguageDialog(language);
    }
}
