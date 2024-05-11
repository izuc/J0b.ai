package ai.j0b.view.activity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ai.j0b.R;
import ai.j0b.databinding.ActivityMainBinding;
import ai.j0b.view.fragment.*;

public class MainActivity extends CameraActivity {

    private ActivityMainBinding binding;
    private final String enterAnimation = "enter";
    private final String exitAnimation = "exit";
    private boolean isFirstFragment = true;

    @Override
    public void showPhotos() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.relHomeClick.setOnClickListener(v -> onHomeClicked());
        binding.relResumeMakerClick.setOnClickListener(v -> onResumeMakerClicked());
        binding.relMyResume.setOnClickListener(v -> onMyResumeClicked());
        binding.relSettingClick.setOnClickListener(v -> onSettingClicked());

        binding.relHomeClick.performClick();
    }

    @SuppressLint("WrongConstant")
    private void setFragment(Fragment fragment, String animation) {
        setFrag(fragment);
        if (animation.equals(enterAnimation)) {
            if (isFirstFragment) {
                isFirstFragment = false;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitNow();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitNow();
            }
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commitNow();
        }
    }

    private void defaultView() {
        binding.relBottomMenuBar.setVisibility(View.GONE);
        binding.relHome.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        binding.relResumeMaker.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        binding.relMyResume.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        binding.relSetting.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        binding.ivHome.setImageResource(R.drawable.mm_home);
        binding.ivResumeMaker.setImageResource(R.drawable.mm_resume_maker);
        binding.ivMyResume.setImageResource(R.drawable.mm_my_resume);
        binding.ivSetting.setImageResource(R.drawable.mm_setting);
    }

    private void onHomeClicked() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relHome.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivHome.setImageResource(R.drawable.mm_home_fill);
        setFragment(new FragmentHome(), enterAnimation);
    }

    private void onResumeMakerClicked() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relResumeMaker.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivResumeMaker.setImageResource(R.drawable.mm_resume_maker_fill);
        setFragment(new FragmentCreateResume(), enterAnimation);
    }

    private void onMyResumeClicked() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relMyResume.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivMyResume.setImageResource(R.drawable.mm_my_resume_fill);
        setFragment(new FragmentMyResume(), enterAnimation);
    }

    private void onSettingClicked() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relSetting.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivSetting.setImageResource(R.drawable.mm_setting_fill);
        setFragment(new FragmentSetting(), enterAnimation);
    }

    public void onPersonalInformationClick() {
        defaultView();
        setFragment(new FragmentPersonalInformation(), enterAnimation);
    }

    public void onAcademicClick() {
        defaultView();
        setFragment(new FragmentAcademic(), enterAnimation);
    }

    public void onExperienceClick() {
        defaultView();
        setFragment(new FragmentExperience(), enterAnimation);
    }

    public void onReferencesClick() {
        defaultView();
        setFragment(new FragmentReferences(), enterAnimation);
    }

    public void onSkillsClick() {
        defaultView();
        setFragment(new FragmentSkills(), enterAnimation);
    }

    public void onLanguageClick() {
        defaultView();
        setFragment(new FragmentLanguage(), enterAnimation);
    }

    public void onHobbiesClick() {
        defaultView();
        setFragment(new FragmentHobbies(), enterAnimation);
    }

    public void onResumeSampleClick() {
        defaultView();
        setFragment(new FragmentResumeSample(), enterAnimation);
    }

    public void onResumeDownloadClick(int position) {
        defaultView();
        FragmentResumeDownload fragment = new FragmentResumeDownload(position);
        setFragment(fragment, enterAnimation);
    }

    public void onTermsOfUse() {
        defaultView();
        setFragment(new FragmentTermsOfUse(), enterAnimation);
    }

    private void onBuilderExit() {
        defaultView();
        super.onBackPressed();
    }

    private void onResumeMakerExit() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relResumeMaker.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivResumeMaker.setImageResource(R.drawable.mm_resume_maker_fill);
        setFragment(new FragmentCreateResume(), exitAnimation);
    }

    private void onMyResumeExit() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relMyResume.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivMyResume.setImageResource(R.drawable.mm_my_resume_fill);
        setFragment(new FragmentMyResume(), exitAnimation);
    }

    private void onSettingExit() {
        defaultView();
        binding.relBottomMenuBar.setVisibility(View.VISIBLE);
        binding.relSetting.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.menuColor)));
        binding.ivSetting.setImageResource(R.drawable.mm_setting_fill);
        setFragment(new FragmentSetting(), exitAnimation);
    }

    private void onResumeSampleExit() {
        defaultView();
        setFragment(new FragmentResumeSample(), exitAnimation);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getFrag();
        if (frag instanceof FragmentHome) {
            onBuilderExit();
            super.onBackPressed();
        } else if (frag instanceof FragmentCreateResume) {
            onBuilderExit();
        } else if (frag instanceof FragmentSetting) {
            onBuilderExit();
        } else if (frag instanceof FragmentMyResume) {
            onBuilderExit();
        } else if (frag instanceof FragmentPersonalInformation) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentAcademic) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentExperience) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentReferences) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentSkills) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentLanguage) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentHobbies) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentResumeSample) {
            onResumeMakerExit();
        } else if (frag instanceof FragmentResumeDownload) {
            onResumeSampleExit();
        } else if (frag instanceof FragmentTermsOfUse) {
            onSettingExit();
        }
    }
}