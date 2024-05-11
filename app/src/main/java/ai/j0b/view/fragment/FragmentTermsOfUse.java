package ai.j0b.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ai.j0b.databinding.FragmentTermsOfUseBinding;

public class FragmentTermsOfUse extends Fragment {
    private FragmentTermsOfUseBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTermsOfUseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
