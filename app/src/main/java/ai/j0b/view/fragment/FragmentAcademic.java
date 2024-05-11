package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;

import ai.j0b.databinding.DataNotFoundBinding;
import ai.j0b.databinding.FragmentAcademicBinding;
import ai.j0b.model.AcademicModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.activity.MainActivity;
import ai.j0b.view.activity.SystemConfiguration;
import ai.j0b.view.adapter.AcademicAdapter;
import ai.j0b.view.dialog.AcademicDialog;
import ai.j0b.view.dialog.AlertDialog;

public class FragmentAcademic extends Fragment implements AcademicAdapter.OnClick {
    private final ArrayList<AcademicModel> arrList = new ArrayList<>();
    private FragmentAcademicBinding binding;

    public ArrayList<AcademicModel> getArrList() {
        return arrList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAcademicBinding.inflate(inflater, container, false);
        SystemConfiguration.setTransparentStatusBar(requireActivity(), SystemConfiguration.IconColor.ICON_LIGHT);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).onBackPressed();
            }
        });

        String savedData = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getAcademicKey());
        if (savedData != null) {
            arrList.addAll(new Gson().fromJson(savedData, new TypeToken<ArrayList<AcademicModel>>() {}.getType()));
        }

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AcademicDialog dialog = new AcademicDialog((AppCompatActivity) requireActivity(), null, new AcademicDialog.OnAboutMeSelect() {
                    @Override
                    public void onAddAcademic(AcademicModel data1) {
                        arrList.add(data1);
                        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getAcademicKey(), new Gson().toJson(arrList));
                        Toast.makeText(requireActivity(), "Data Saved !", Toast.LENGTH_SHORT).show();
                        onRecycleViewRefresh();
                    }
                });
                dialog.show();
            }
        });

        binding.recycleView.setAdapter(new AcademicAdapter(requireActivity(), arrList, this));
        binding.recycleView.setNestedScrollingEnabled(false);
        onRecycleViewRefresh();

        return binding.getRoot();
    }

    public void onRecycleViewRefresh() {
        if (binding != null && binding.recycleView != null && binding.recycleView.getAdapter() != null) {
            binding.recycleView.getAdapter().notifyDataSetChanged();
        }

        if (arrList.size() == 0) {
            if (binding != null && binding.recycleView != null) {
                binding.recycleView.setVisibility(View.GONE);
            }
            if (binding != null && binding.noDataFound != null && binding.noDataFound.noDataFound != null) {
                binding.noDataFound.noDataFound.setVisibility(View.VISIBLE);
            }
        } else {
            if (binding != null && binding.recycleView != null) {
                binding.recycleView.setVisibility(View.VISIBLE);
            }
            if (binding != null && binding.noDataFound != null && binding.noDataFound.noDataFound != null) {
                binding.noDataFound.noDataFound.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDeleteClick(final AcademicModel data) {
        new AlertDialog((AppCompatActivity) requireActivity(), new AlertDialog.OnClick() {
            @Override
            public void onYesClick() {
                arrList.remove(data);
                Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getAcademicKey(), new Gson().toJson(arrList));
                onRecycleViewRefresh();
            }
        }).show();
    }

    @Override
    public void onUpdateClick(final AcademicModel data) {
        AcademicDialog dialog = new AcademicDialog((AppCompatActivity) requireActivity(), data, new AcademicDialog.OnAboutMeSelect() {
            @Override
            public void onAddAcademic(AcademicModel data1) {
                for (AcademicModel temp : arrList) {
                    if (data.equals(temp)) {
                        temp.setPercentage(data1.getPercentage() != null ? data1.getPercentage().trim() : null);
                        temp.setName(data1.getName() != null ? data1.getName().trim() : null);
                        temp.setCgpa(data1.getCgpa() != null ? data1.getCgpa().trim() : null);
                        temp.setYear(data1.getYear() != null ? data1.getYear().trim() : null);
                        temp.setInstitute(data1.getInstitute() != null ? data1.getInstitute().trim() : null);
                    }
                }
                Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getAcademicKey(), new Gson().toJson(arrList));
                Toast.makeText(requireActivity(), "Data Saved !", Toast.LENGTH_SHORT).show();
                onRecycleViewRefresh();
            }
        });
        dialog.show();
    }
}