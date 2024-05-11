package ai.j0b.view.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.print.PrintHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ai.j0b.R;
import ai.j0b.XMLtoPDF.PdfGenerator;
import ai.j0b.XMLtoPDF.PdfGeneratorListener;
import ai.j0b.XMLtoPDF.model.FailureResponse;
import ai.j0b.XMLtoPDF.model.SuccessResponse;
import ai.j0b.databinding.FragmentResumeDownloadBinding;
import ai.j0b.model.AcademicModel;
import ai.j0b.model.ExperienceModel;
import ai.j0b.utils.Constant;
import ai.j0b.view.fragment.resume.FragmentResume1;
import ai.j0b.view.fragment.resume.FragmentResume2;
import ai.j0b.view.fragment.resume.FragmentResume3;
import ai.j0b.view.fragment.resume.FragmentResume4;
import ai.j0b.view.fragment.resume.FragmentResume5;
import ai.j0b.view.fragment.resume.FragmentResume6;
import ai.j0b.view.fragment.resume.FragmentResume7;
import ai.j0b.view.fragment.resume.FragmentResume8;
import ai.j0b.view.fragment.resume.FragmentResume9;
import ai.j0b.view.fragment.resume.adapter.EducationAdapter1;
import ai.j0b.view.fragment.resume.adapter.ExperienceAdapter1;
import ai.j0b.view.fragment.resume.adapter.TextAdapter1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FragmentResumeDownload extends Fragment {
    private static final String TAG = "FragmentResumeDownload";
    private FragmentResumeDownloadBinding binding;
    private final int position;

    private Fragment frag;

    public FragmentResumeDownload(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResumeDownloadBinding.inflate(inflater, container, false);
        setFragment(getFragmentForPosition(position));
        setupListeners();
        return binding.getRoot();
    }

    private Fragment getFragmentForPosition(int position) {
        Fragment frag;
        switch (position) {
            case 0:
                frag = new FragmentResume1();
                break;
            case 1:
                frag = new FragmentResume2();
                break;
            case 2:
                frag = new FragmentResume3();
                break;
            case 3:
                frag = new FragmentResume4();
                break;
            case 4:
                frag = new FragmentResume5();
                break;
            case 5:
                frag = new FragmentResume6();
                break;
            case 6:
                frag = new FragmentResume7();
                break;
            case 7:
                frag = new FragmentResume8();
                break;
            case 8:
                frag = new FragmentResume9();
                break;
            // Add more cases if there are more than 9 resume fragments
            default:
                frag = new FragmentResume1(); // Default case, adjust as necessary
                break;
        }
        return frag;
    }


    private void setupListeners() {
        binding.ivBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.relDownload.setOnClickListener(v -> downloadResume());
    }

    private void downloadResume() {
        downloadSet();

        String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Resume_" + timestamp;

        // Assuming 'binding' is the binding for the current layout of FragmentResumeDownload
        View content = this.frag.onCreateView(LayoutInflater.from(requireContext()), null, null);
        if (content != null) {

            PdfGenerator.getBuilder()
                    .setContext(requireActivity())
                    .fromViewSource()
                    .fromView(content) // Use the measured content
                    .setPageSize(PdfGenerator.PageSize.A4) // Set the page size to A4 dimensions in points
                    .setFileName(fileName)
                    .setFolderNameOrPath(downloadsPath)
                    .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.NONE)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            Toast.makeText(requireActivity(), "Failed to generate PDF: " + failureResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void showLog(String log) {
                            Log.d(TAG, log);
                        }

                        @Override
                        public void onStartPDFGeneration() {
                            // Handle start of PDF generation
                        }

                        @Override
                        public void onFinishPDFGeneration() {
                            // Handle finish of PDF generation
                        }

                        @Override
                        public void onSuccess(SuccessResponse response) {
                            Toast.makeText(requireActivity(), "PDF generated successfully: " + response.getPath(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setFragment(Fragment frag) {
        this.frag = frag;
        getChildFragmentManager().beginTransaction().replace(R.id.container_resume, frag).commit();
    }

    private void downloadSet() {
        List<Integer> resumeList = loadResumeList();
        resumeList.add(position);
        saveResumeList(new ArrayList<>(new HashSet<>(resumeList))); // Remove duplicates
    }

    private List<Integer> loadResumeList() {
        String json = Constant.INSTANCE.getData(requireActivity(), Constant.INSTANCE.getMyResumeKey());
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<List<Integer>>() {}.getType());
        }
        return new ArrayList<>();
    }

    private void saveResumeList(List<Integer> resumeList) {
        String json = new Gson().toJson(resumeList);
        Constant.INSTANCE.saveData(requireActivity(), Constant.INSTANCE.getMyResumeKey(), json);
    }
}
