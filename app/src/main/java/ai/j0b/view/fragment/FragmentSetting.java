package ai.j0b.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ai.j0b.R;
import ai.j0b.databinding.FragmentSettingBinding;
import ai.j0b.view.activity.MainActivity;

public class FragmentSetting extends Fragment {
    private FragmentSettingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        setupListeners();
        return binding.getRoot();
    }

    private void setupListeners() {
        binding.llRateUs.setOnClickListener(v -> openRateUs());
        binding.llHelp.setOnClickListener(v -> sendEmailForHelp());
        binding.llShareWithFriends.setOnClickListener(v -> shareAppWithFriends());
        binding.llTermsOfUse.setOnClickListener(v -> openTermsOfUse());
        binding.llPrivacyPolicy.setOnClickListener(v -> openPrivacyPolicy());
    }

    private void openRateUs() {
        String appPackageName = requireContext().getPackageName(); // Get package name dynamically
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendEmailForHelp() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:niraj.sampsolution@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Need Help");
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareAppWithFriends() {
        String appPackageName = requireContext().getPackageName(); // Get package name dynamically
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=" + appPackageName);
        startActivity(Intent.createChooser(intent, "Share App"));
    }

    private void openTermsOfUse() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).onTermsOfUse();
        }
    }

    private void openPrivacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/myappprivacypolicy123/home"));
        startActivity(intent);
    }
}
