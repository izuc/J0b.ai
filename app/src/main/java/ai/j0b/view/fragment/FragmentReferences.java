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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ai.j0b.databinding.FragmentReferencesBinding;
import ai.j0b.model.ReferencesModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.adapter.ReferencesAdapter;
import ai.j0b.view.dialog.AlertDialog;
import ai.j0b.view.dialog.ReferencesDialog; // You need to create a dialog similar to ExperienceDialog for references

public class FragmentReferences extends Fragment implements ReferencesAdapter.OnClick {
    private FragmentReferencesBinding binding;
    private final ArrayList<ReferencesModel> referencesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReferencesBinding.inflate(inflater, container, false);
        setupUI();
        loadData();
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupUI() {
        binding.recyclerViewReferences.setAdapter(new ReferencesAdapter(requireActivity(), referencesList, this));
        binding.recyclerViewReferences.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerViewReferences.setNestedScrollingEnabled(false);
    }

    private void loadData() {
        String jsonReferences = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getReferencesKey());
        if (jsonReferences != null) {
            Type type = new TypeToken<ArrayList<ReferencesModel>>() {}.getType();
            List<ReferencesModel> list = new Gson().fromJson(jsonReferences, type);
            referencesList.addAll(list);
            onRecycleViewRefresh();
        }
    }

    private void setupClickListeners() {
        binding.ivBackReferences.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onBackPressed();
            }
        });

        binding.btnAddReference.setOnClickListener(v -> showReferencesDialog(null));
    }

    private void showReferencesDialog(@Nullable ReferencesModel referencesModel) {
        ReferencesDialog dialog = new ReferencesDialog((AppCompatActivity) requireActivity(), referencesModel, newData -> {
            if (newData == null) return;

            if (referencesModel == null) {
                referencesList.add(newData);
            } else {
                int index = referencesList.indexOf(referencesModel);
                if (index != -1) {
                    referencesList.set(index, newData);
                }
            }

            saveReferencesList();
            Toast.makeText(requireActivity(), "Data Saved!", Toast.LENGTH_SHORT).show();
            onRecycleViewRefresh();
        });

        dialog.show();
    }

    private void saveReferencesList() {
        String json = new Gson().toJson(referencesList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getReferencesKey(), json);
    }

    @Override
    public void onDeleteClick(ReferencesModel data) {
        new AlertDialog((AppCompatActivity) requireActivity(), () -> {
            referencesList.remove(data);
            saveReferencesList();
            onRecycleViewRefresh();
        }).show();
    }

    @Override
    public void onUpdateClick(ReferencesModel data) {
        showReferencesDialog(data);
    }

    public void onRecycleViewRefresh() {
        if (binding.recyclerViewReferences.getAdapter() != null) {
            binding.recyclerViewReferences.getAdapter().notifyDataSetChanged();
        }

        if (referencesList.isEmpty()) {
            binding.recyclerViewReferences.setVisibility(View.GONE);
            binding.noDataFoundReferences.noDataFound.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewReferences.setVisibility(View.VISIBLE);
            binding.noDataFoundReferences.noDataFound.setVisibility(View.GONE);
        }
    }
}