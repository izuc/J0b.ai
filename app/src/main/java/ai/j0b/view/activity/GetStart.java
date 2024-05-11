package ai.j0b.view.activity;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlCursor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ai.j0b.R;

import ai.j0b.model.ResumeData;
import ai.j0b.utils.AnthropicAPI;
import ai.j0b.utils.Constant;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.Manifest;

public class GetStart extends AppCompatActivity {
    private static final String RESUME_TEMPLATE_PATH_KEY = "resumeTemplatePath";
    private static final int WRITE_REQUEST_CODE = 101;
    private ActivityResultLauncher<String[]> resumePickerLauncher;
    private String selectedResumeTemplatePath;
    private TextView resumeLabel;
    private ImageView findJobsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_REQUEST_CODE);
        }

        setContentView(R.layout.activity_get_start);
        // Assume SystemConfiguration.setTransparentStatusBar is a method in your project
        // SystemConfiguration.setTransparentStatusBar(this, SystemConfiguration.IconColor.ICON_LIGHT);

        resumeLabel = findViewById(R.id.resumeLabel);
        findJobsButton = findViewById(R.id.find);

        // Initialize the ActivityResultLauncher with OpenDocument contract
        resumePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // Take persistable URI permission to retain access
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );

                        if (loadResumeFromUri(uri)) {
                            Toast.makeText(this, "Resume loaded", Toast.LENGTH_SHORT).show();
                            processResumeData();
                        }
                    }
                }
        );

        findViewById(R.id.upload).setOnClickListener(v -> openFileChooser());

        findViewById(R.id.start).setOnClickListener(v -> {
            Intent intent = new Intent(GetStart.this, MainActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.settings).setOnClickListener(v -> {

        });

        findViewById(R.id.jobs).setOnClickListener(v -> {
            // Handle jobs click
        });

        findViewById(R.id.find).setOnClickListener(v -> {
            Intent intent = new Intent(GetStart.this, JobBotActivity.class);
            intent.putExtra(RESUME_TEMPLATE_PATH_KEY, selectedResumeTemplatePath);
            startActivity(intent);
        });

        // Load the selected resume template path
        loadAndDisplaySelectedResumeTemplate();
        loadAndSaveConfig();
        loadResumeData();
    }

    private void saveSelectedResumeTemplateUri(String uriString) {
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RESUME_TEMPLATE_PATH_KEY, uriString);

        // Store the file name in shared preferences
        Uri uri = Uri.parse(uriString);
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        if (documentFile != null) {
            String fileName = documentFile.getName();
            editor.putString("resumeFileName", fileName);
        }

        editor.apply();
    }

    private String loadSelectedResumeTemplateUri() {
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(RESUME_TEMPLATE_PATH_KEY, null);
    }

    private void openFileChooser() {
        resumePickerLauncher.launch(new String[]{
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Handle the back press; ensure you call super.onBackPressed() if you remove this override.
    }

    private void updateUIWithFileName(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        if (documentFile != null) {
            String fileName = documentFile.getName();
            resumeLabel.setText(fileName);
            findJobsButton.setEnabled(true);
        } else {
            resumeLabel.setText("Unknown");
            findJobsButton.setEnabled(false);
        }
    }

    private void loadAndDisplaySelectedResumeTemplate() {
        selectedResumeTemplatePath = loadSelectedResumeTemplateUri();
        if (selectedResumeTemplatePath != null) {
            Uri uri = Uri.parse(selectedResumeTemplatePath);
            updateUIWithFileName(uri);
        } else {
            findJobsButton.setEnabled(false);
        }
    }

    private boolean loadResumeFromUri(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "An error occurred while selecting the file", Toast.LENGTH_SHORT).show();
            return false;
        }

        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        if (documentFile != null) {
            String fileName = documentFile.getName();

            StringBuilder textBuilder = new StringBuilder();

            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                String uriString = uri.toString().toLowerCase();
                if (fileName.endsWith(".pdf")) {
                    File tempFile = File.createTempFile("temp", ".pdf");
                    tempFile.deleteOnExit();

                    try (FileOutputStream out = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }

                    PDFBoxResourceLoader.init(getApplicationContext());

                    try (PDDocument document = PDDocument.load(tempFile)) {
                        PDFTextStripper pdfStripper = new PDFTextStripper();
                        String text = pdfStripper.getText(document);
                        textBuilder.append(text);
                    } finally {
                        tempFile.delete();
                    }
                } else if (fileName.endsWith(".docx")) {
                    try (XWPFDocument document = new XWPFDocument(inputStream)) {

                        // Process all types of headers in each section of the document
                        for (XWPFHeader header : document.getHeaderList()) {
                            XmlCursor headerCursor = header._getHdrFtr().newCursor();
                            try {
                                headerCursor.selectPath(".//*[local-name()='t']");
                                while (headerCursor.toNextSelection()) {
                                    textBuilder.append(headerCursor.getTextValue());
                                    textBuilder.append(System.lineSeparator());
                                }
                            } finally {
                                headerCursor.dispose();
                            }
                        }

                        // Process the main document body
                        XmlCursor cursor = document.getDocument().getBody().newCursor();
                        try {
                            cursor.selectPath(".//*[local-name()='t']");
                            while (cursor.toNextSelection()) {
                                textBuilder.append(cursor.getTextValue());
                                textBuilder.append(System.lineSeparator());
                            }
                        } finally {
                            cursor.dispose();
                        }
                    }
                } else {
                    Toast.makeText(this, "Unsupported file format: " + uriString, Toast.LENGTH_SHORT).show();
                }
                if (textBuilder.length() > 0) {
                    saveSelectedResumeTemplateUri(uri.toString());
                    SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String resumeText = textBuilder.toString().replace("\n", " ").replace("\r", " ").replace("\"", "");
                    editor.putString("resume", resumeText);
                    editor.apply(); // Or use commit() if you need the operation to be synchronous
                    resumeLabel.setText(fileName);
                    findJobsButton.setEnabled(true);
                    return true;
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private String loadResumeText() {
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("resume", null);
    }

    private void loadAndSaveConfig() {
        Properties properties = new Properties();
        try {
            // Load the properties from the config file
            InputStream inputStream = getAssets().open("config.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            // Handle the exception, maybe log it
            e.printStackTrace();
        }

        // Retrieve properties
        String apiKey = properties.getProperty("api_key", "");
        String url = properties.getProperty("url", "");
        String titleSelector = properties.getProperty("title_selector", "");
        String companySelector = properties.getProperty("company_selector", "");
        String descriptionSelector = properties.getProperty("description_selector", "");
        String dateSelector = properties.getProperty("date_selector", "");

        // Save the configuration to SharedPreferences
        saveConfigToSharedPreferences(apiKey, url, titleSelector, companySelector, descriptionSelector, dateSelector);
    }

    private void saveConfigToSharedPreferences(String apiKey, String url, String titleSelector, String companySelector, String descriptionSelector, String dateSelector) {
        // Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);

        // Get the SharedPreferences Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Update the value only if it is empty or not set
        if (sharedPreferences.getString("api_key", "").isEmpty()) {
            editor.putString("api_key", apiKey);
        }
        if (sharedPreferences.getString("url", "").isEmpty()) {
            editor.putString("url", url);
        }
        if (sharedPreferences.getString("title_selector", "").isEmpty()) {
            editor.putString("title_selector", titleSelector);
        }
        if (sharedPreferences.getString("company_selector", "").isEmpty()) {
            editor.putString("company_selector", companySelector);
        }
        if (sharedPreferences.getString("description_selector", "").isEmpty()) {
            editor.putString("description_selector", descriptionSelector);
        }
        if (sharedPreferences.getString("date_selector", "").isEmpty()) {
            editor.putString("date_selector", dateSelector);
        }

        // Commit the changes
        editor.apply();
    }

    private void processResumeData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
            if (sharedPreferences.getString("resume_data", "").isEmpty()) {
                Log.d("GetStart", "Resume Data is Empty");
                if (!sharedPreferences.getString("resume", "").isEmpty()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String resume = sharedPreferences.getString("resume", "");
                    String response = ResumeData.extractResumeData(this.getApplicationContext(), resume);
                    editor.putString("resume_data", response);
                    editor.apply();

                    // Run Toast message on the main thread
                    runOnUiThread(() -> Toast.makeText(this, "Resume Data Loaded", Toast.LENGTH_SHORT).show());

                    loadResumeData();
                }
            }
        });
    }

    private void loadResumeData() {
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        String response = sharedPreferences.getString("resume_data", "");
        Log.e("ResumeData", "Loading Data");
        if (!response.isEmpty()) {
            //extractJsonFromResponseAndWriteToFile(response);
            String extractedJson = ResumeData.extractJsonFromResponse(response);
            if (extractedJson != null) {
                Log.e("ResumeData", extractedJson);
                ResumeData resumeData = ResumeData.bindJsonToResumeData(extractedJson);
                if (resumeData != null) {
                    Constant.INSTANCE.saveResumeData(this.getApplicationContext(), resumeData);
                    Log.e("ResumeData", "Loaded Resume Data");
                } else {
                    Log.e("ResumeData", "Resume Data Failed Loading");
                }
            } else {
                Log.e("ResumeData", "Invalid JSON format");
            }
        }
    }




}
