package ai.j0b.view.fragment.resume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ai.j0b.databinding.FragmentResume5Binding;
import ai.j0b.model.AcademicModel;
import ai.j0b.model.ExperienceModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.fragment.resume.adapter.EducationAdapter1;
import ai.j0b.view.fragment.resume.adapter.ExperienceAdapter1;
import ai.j0b.view.fragment.resume.adapter.TextAdapter1;

import java.util.ArrayList;

public class FragmentResume5 extends Fragment {
    private FragmentResume5Binding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResume5Binding.inflate(inflater, container, false);
        setupUI();
        return binding.getRoot();
    }

    private void setupUI() {
        loadData(Constant.INSTANCE.getNameKey(), data -> binding.tvName.setText((String)data));
        loadData(Constant.INSTANCE.getFieldKey(), data -> binding.tvDesignation.setText((String)data));
        loadData(Constant.INSTANCE.getNumberKey(), data -> binding.tvNumber.setText((String)data));
        loadData(Constant.INSTANCE.getEmailKey(), data -> binding.tvEmail.setText((String)data));
        loadData(Constant.INSTANCE.getAddressKey(), data -> binding.tvAddress.setText((String)data));
        loadData(Constant.INSTANCE.getAboutMeKey(), data -> binding.tvAboutMe.setText((String)data));

        loadListData(Constant.INSTANCE.getAcademicKey(), new TypeToken<ArrayList<AcademicModel>>() {}, list -> binding.recycleEducation.setAdapter(new EducationAdapter1(requireActivity(), list)));
        loadListData(Constant.INSTANCE.getSkillsKey(), new TypeToken<ArrayList<String>>() {}, list -> binding.recycleSkills.setAdapter(new TextAdapter1(requireActivity(), list)));
        loadListData(Constant.INSTANCE.getExperienceKey(), new TypeToken<ArrayList<ExperienceModel>>() {}, list -> binding.recycleExperience.setAdapter(new ExperienceAdapter1(requireActivity(), list, "color1")));
        loadListData(Constant.INSTANCE.getLanguageKey(), new TypeToken<ArrayList<String>>() {}, list -> binding.recycleLanguage.setAdapter(new TextAdapter1(requireActivity(), list)));
    }

    private <T> void loadData(String key, Callback<T> callback) {
        String data = Constant.INSTANCE.getData(requireActivity(), key);
        if (data != null) {
            callback.onDataLoaded((T) data);
        }
    }

    private <T> void loadListData(String key, TypeToken<ArrayList<T>> typeToken, Callback<ArrayList<T>> callback) {
        String data = Constant.INSTANCE.getData(requireActivity(), key);
        if (data != null) {
            ArrayList<T> list = new Gson().fromJson(data, typeToken.getType());
            callback.onDataLoaded(list);
        }
    }

    interface Callback<T> {
        void onDataLoaded(T data);
    }
}
