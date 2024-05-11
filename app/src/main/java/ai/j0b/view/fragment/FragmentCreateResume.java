package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ai.j0b.databinding.FragmentCreateResumeBinding;
import ai.j0b.view.activity.MainActivity;

public class FragmentCreateResume extends Fragment {
    private FragmentCreateResumeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateResumeBinding.inflate(inflater, container, false);
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupClickListeners() {
        MainActivity mainActivity = getMainActivity();

        binding.relPersonalInformation.setOnClickListener(view -> mainActivity.onPersonalInformationClick());
        binding.relAcademic.setOnClickListener(view -> mainActivity.onAcademicClick());
        binding.relExperience.setOnClickListener(view -> mainActivity.onExperienceClick());
        binding.relReferences.setOnClickListener(view -> mainActivity.onReferencesClick());
        binding.relSkills.setOnClickListener(view -> mainActivity.onSkillsClick());
        binding.relLanguage.setOnClickListener(view -> mainActivity.onLanguageClick());
        binding.relHobbies.setOnClickListener(view -> mainActivity.onHobbiesClick());
        binding.relResumeSamples.setOnClickListener(view -> mainActivity.onResumeSampleClick());
    }

    private MainActivity getMainActivity() {
        if (getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        } else {
            throw new IllegalStateException("This fragment must be attached to MainActivity.");
        }
    }
}
