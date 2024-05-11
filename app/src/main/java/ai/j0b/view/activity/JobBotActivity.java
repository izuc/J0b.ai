package ai.j0b.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.view.View;
import android.widget.TextView;
import android.content.pm.PackageManager;

import android.Manifest;

import org.apache.xmlbeans.XmlCursor;
import java.util.Locale;
import org.apache.poi.xwpf.usermodel.BreakType;
import ai.j0b.utils.AnthropicAPI;
import ai.j0b.R;

class CompatibilityResult {
    private final boolean isCompatible;
    private final String reason;

    public CompatibilityResult(boolean isCompatible, String reason) {
        this.isCompatible = isCompatible;
        this.reason = reason;
    }

    public boolean isCompatible() {
        return isCompatible;
    }

    public String getReason() {
        return reason;
    }
}

public class JobBotActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;

    private static final String RESUME_TEMPLATE_PATH_KEY = "resumeTemplatePath";

    private ActivityResultLauncher<String> documentPickerLauncher;

    private String selectedResumeTemplatePath;
    private EditText apiKeyEditText;
    private EditText urlEditText;

    private String titleSelector;
    private String companySelector;
    private String descriptionSelector;
    private String dateSelector;
    private Spinner timespanSpinner;
    private ImageView startStopButton;

    private TextView logTextView;

    private boolean scrapingInProgress = false;

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESUME_TEMPLATE_PATH_KEY, selectedResumeTemplatePath);
    }

    private void writeToLog(String message) {
        try {
            File logFile = new File(getExternalFilesDir(null), "job_bot.log");
            FileWriter writer = new FileWriter(logFile, true);
            writer.append(message).append("\n");
            writer.flush();
            writer.close();

            // Read the log file and update the logTextView on the UI thread
            runOnUiThread(() -> {
                String logContents = readLogFile();
                logTextView.setText(logContents);
            });
        } catch (IOException ignore) {
        }
    }

    private String readLogFile() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            File logFile = new File(getExternalFilesDir(null), "job_bot.log");
            if (logFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    logBuilder.append(line).append("\n");
                }
                reader.close();
            }
        } catch (IOException ignore) {
        }
        return logBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_bot);

        apiKeyEditText = findViewById(R.id.apiKeyEditText);
        urlEditText = findViewById(R.id.urlEditText);
        timespanSpinner = findViewById(R.id.timespanSpinner);
        startStopButton = findViewById(R.id.startStopButton);
        logTextView = findViewById(R.id.logTextView);

        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            // Take persistable URI permissions to retain access across restarts.
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                            // Save the URI string for later use.
                            saveSelectedResumeTemplateUri(uri.toString());

                            // Load the resume from the selected URI and start your specific process.
                            String resume = loadResumeFromUri(uri);
                            startScraping(resume, uri.toString());
                        } catch (SecurityException e) {
                            // Handle any SecurityExceptions, possibly due to lost permissions.
                            Log.e("JobBotActivity", "Failed to take persistable permissions: ", e);
                            Toast.makeText(this, "Error obtaining file permissions.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle the case where no file was selected or the operation was canceled.
                        Toast.makeText(this, "No file selected.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        startStopButton.setOnClickListener(v -> {
            if (startStopButton.getTag().equals("start")) {
                String resumeTemplateUriString = loadSelectedResumeTemplateUri();
                if (resumeTemplateUriString != null) {
                    Uri documentUri = Uri.parse(resumeTemplateUriString);
                    try {
                        // Attempt to use the URI, which might throw a SecurityException if permissions have been revoked
                        String resume = loadResumeText();//loadResumeFromUri(documentUri);
                        if (!resume.isEmpty()) {
                            startScraping(resume, resumeTemplateUriString);
                        } else {
                            Toast.makeText(this, "Resume not loaded", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SecurityException e) {
                        // Permissions have been revoked; prompt the user to select the file again
                        documentPickerLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    }
                } else {
                    // No URI stored; prompt for file selection
                    documentPickerLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                }
            } else {
                stopScraping();
            }
        });

        checkAndRequestPermissions();

        // Load saved configuration and template path
        loadConfig();
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

    private String loadResumeText() {
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("resume", null);
    }

    private String loadResumeFromUri(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "An error occurred while selecting the file", Toast.LENGTH_SHORT).show();
            return "";
        }

        StringBuilder textBuilder = new StringBuilder();

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             XWPFDocument document = new XWPFDocument(inputStream)) {

            // Access the document's XML to find text within shapes
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
        } catch (IOException e) {
            writeToLog("Error reading file: " + e.getMessage());
        }

        return textBuilder.toString().replace("\n", " ").replace("\r", " ").replace("\"", "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                Toast.makeText(this, "Storage permissions are required to save files", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private boolean hasStoragePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void loadConfig() {
        // Load configuration from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        String apiKey = sharedPreferences.getString("api_key", "");
        String url = sharedPreferences.getString("url", "");
        titleSelector = sharedPreferences.getString("title_selector", "");
        companySelector = sharedPreferences.getString("company_selector", "");
        descriptionSelector = sharedPreferences.getString("description_selector", "");
        dateSelector = sharedPreferences.getString("date_selector", "");
        selectedResumeTemplatePath = sharedPreferences.getString(RESUME_TEMPLATE_PATH_KEY, null);

        // Update the UI elements with the loaded values
        apiKeyEditText.setText(apiKey);
        urlEditText.setText(url);
    }

    private InputStream getResumeTemplateInputStream(String uriString) {
        if (uriString != null && hasStoragePermissions()) {
            Uri uri = Uri.parse(uriString);
            try {
                return getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                writeToLog(e.getMessage());
            }
        } else {
            Toast.makeText(this, "Storage permissions are required to save files", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void startScraping(String resume, String resumePath) {
        // Get the values from the UI elements
        String apiKey = apiKeyEditText.getText().toString();
        String url = urlEditText.getText().toString();

        // Hide the config parameters layout
        findViewById(R.id.configParamsLayout).setVisibility(View.GONE);

        // Show the log ScrollView
        findViewById(R.id.logScrollView).setVisibility(View.VISIBLE);

        // Clear the log TextView
        logTextView.setText("");

        // Clear the log file
        File logFile = new File(getExternalFilesDir(null), "job_bot.log");
        if (logFile.exists()) {
            logFile.delete();
        }

        // Start the job scraping process in a background thread
        new Thread(() -> {
            try {
                scrapingInProgress = true;
                scrapeJobs(apiKey, url, titleSelector, companySelector, descriptionSelector, dateSelector, resume, resumePath);
                runOnUiThread(() -> {
                    String logContents = readLogFile();
                    logTextView.setText(logContents);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    writeToLog(e.getMessage());
                    Toast.makeText(JobBotActivity.this, "Error occurred during job scraping: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        startStopButton.setImageResource(R.drawable.btn_stop);
        startStopButton.setTag(R.string.stop);
    }

    private void stopScraping() {
        scrapingInProgress = false;
        // Stop the job scraping process
        // Update the UI accordingly
        startStopButton.setImageResource(R.drawable.btn_start);
        startStopButton.setTag(R.string.start);

        // Hide the log ScrollView
        findViewById(R.id.logScrollView).setVisibility(View.GONE);

        // Show the config parameters layout
        findViewById(R.id.configParamsLayout).setVisibility(View.VISIBLE);

    }

    private boolean isWithinTimespan(String jobListingDate, String selectedTimespan) {
        // Regular expression pattern to match the time format
        String pattern = "(\\d+)([mhd]) ago";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher m = r.matcher(jobListingDate);

        if (m.find()) {
            int value = Integer.parseInt(Objects.requireNonNull(m.group(1)));
            String unit = m.group(2);

            // Get the current date and time
            LocalDateTime currentDate = LocalDateTime.now();

            // Calculate the actual date based on the extracted value and unit
            LocalDateTime actualDate;
            switch (Objects.requireNonNull(unit)) {
                case "m":
                    actualDate = currentDate.minusMinutes(value);
                    break;
                case "h":
                    actualDate = currentDate.minusHours(value);
                    break;
                case "d":
                    actualDate = currentDate.minusDays(value);
                    break;
                default:
                    // Unknown unit, consider the job as not within the timespan
                    return false;
            }

            // Check if the actual date is within the desired timespan
            LocalDateTime startDate;
            switch (selectedTimespan) {
                case "Today":
                    startDate = currentDate.toLocalDate().atStartOfDay();
                    break;
                case "Yesterday":
                    startDate = currentDate.minusDays(1).toLocalDate().atStartOfDay();
                    break;
                case "Within a week":
                    startDate = currentDate.minusDays(7).toLocalDate().atStartOfDay();
                    break;
                case "Within 2 weeks":
                    startDate = currentDate.minusDays(14).toLocalDate().atStartOfDay();
                    break;
                default:
                    return false;
            }
            return actualDate.isAfter(startDate) || actualDate.isEqual(startDate);
        }

        // Job listing date format doesn't match the expected pattern
        return false;
    }

    private void scrapeJobs(String apiKey, String url, String titleSelector, String companySelector,
                            String descriptionSelector, String dateSelector, String resume, String resumePath) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        writeToLog("-------- RESUME LOADED --------");
        writeToLog(resume);
        writeToLog("-------- END RESUME LOADED --------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        String dateTimeSuffix = dateFormat.format(new Date());
        String resumesFolder = "resumes_" + dateTimeSuffix;

        Connection conn = null;
        try {
            // Create an embedded H2 database connection
            conn = DriverManager.getConnection("jdbc:h2:mem:jobs");

            // Create the jobs table if it doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS jobs (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), company VARCHAR(255), description CLOB, url VARCHAR(255), job_listing_date VARCHAR(255), scraped_date DATE, UNIQUE (url))");

            stmt.execute("CREATE TABLE IF NOT EXISTS applications (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "job_id VARCHAR(255)," +
                    "cover_letter_filename VARCHAR(255)," +
                    "cover_letter_path VARCHAR(255)" +
                    ")");

            writeToLog("Starting job scraping...");

            // Loop through pages
            int pageNumber = 1;
            while (scrapingInProgress) {
                // Build the URL for the current page
                String pageUrl = url + "&page=" + pageNumber;

                URL parsedUrl = new URL(pageUrl);
                String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost();
                if (parsedUrl.getPort() != -1) {
                    baseUrl += ":" + parsedUrl.getPort();
                }

                writeToLog("Fetching page URL: " + pageUrl);

                // Send a GET request to the page URL
                Request request = new Request.Builder()
                        .url(pageUrl)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Parse the HTML response using Jsoup
                    assert response.body() != null;
                    Document document = Jsoup.parse(response.body().string());

                    // Find all job elements on the current page
                    Elements jobElements = document.select("article[data-automation='normalJob']");

                    // Check if there are no job elements on the page
                    if (jobElements.isEmpty()) {
                        writeToLog("No more jobs found. Exiting loop.");
                        break; // No more jobs, exit the loop
                    }

                    writeToLog("Processing " + jobElements.size() + " job elements on page " + pageNumber);

                    // Process each job element
                    for (Element jobElement : jobElements) {
                        // Check if the job has a "premiumAdBadge"
                        if (!jobElement.select("span[data-automation='premiumAdBadge']").isEmpty()) {
                            writeToLog("Skipping job with premiumAdBadge");
                            continue;
                        }

                        // Extract the job listing date from the job element
                        String jobListingDate = jobElement.select("span[data-automation='" + dateSelector + "']").text();

                        // Check if the job listing date is within the desired timespan
                        String selectedTimespan = timespanSpinner.getSelectedItem().toString();
                        if (!isWithinTimespan(jobListingDate, selectedTimespan)) {
                            writeToLog("Skipping job outside the desired timespan: " + jobListingDate);
                            continue;
                        }

                        // Find the job link within the job element and process the job
                        String relativeJobUrl = jobElement.select("a[data-automation='job-list-view-job-link']").attr("href");
                        String jobUrl = baseUrl + '/' + relativeJobUrl.substring(1);

                        writeToLog("Processing job URL: " + jobUrl);

                        // Check if the job already exists in the database
                        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM jobs WHERE url = ?");
                        pstmt.setString(1, jobUrl);
                        ResultSet rs = pstmt.executeQuery();
                        rs.next();
                        int jobExists = rs.getInt(1);

                        if (jobExists > 0) {
                            writeToLog("Skipping already existing job: " + jobUrl);
                            continue;
                        }

                        // Send a GET request to the job URL
                        Request jobRequest = new Request.Builder()
                                .url(jobUrl)
                                .build();

                        try (Response jobResponse = client.newCall(jobRequest).execute()) {
                            if (!jobResponse.isSuccessful()) {
                                throw new IOException("Unexpected code " + jobResponse);
                            }

                            // Parse the HTML response using Jsoup
                            assert jobResponse.body() != null;
                            Document jobDocument = Jsoup.parse(jobResponse.body().string());

                            // Extract job details
                            String jobTitle = jobDocument.select("h1[data-automation='" + titleSelector + "']").text();
                            String jobCompany = jobDocument.select("span[data-automation='" + companySelector + "']").text();
                            String jobDescription = jobDocument.select("div[data-automation='" + descriptionSelector + "']").text();

                            // Assuming jobTitle, jobCompany, and jobDescription are your input strings
                            jobTitle = jobTitle.replaceAll("<[^>]+>", "") // Remove HTML tags
                                    .replaceAll("[^\\w\\s,.]", "") // Remove special characters, adjust as needed
                                    .replaceAll("\\r?\\n", " "); // Replace line breaks with space

                            jobCompany = jobCompany.replaceAll("<[^>]+>", "") // Remove HTML tags
                                    .replaceAll("[^\\w\\s,.]", "") // Remove special characters, adjust as needed
                                    .replaceAll("\\r?\\n", " "); // Replace line breaks with space

                            jobDescription = jobDescription.replaceAll("<[^>]+>", "") // Remove HTML tags
                                    .replaceAll("[^\\w\\s,.]", "") // Remove special characters, adjust as needed
                                    .replaceAll("\\r?\\n", " "); // Replace line breaks with space


                            // Insert job details into the database
                            pstmt = conn.prepareStatement(
                                    "INSERT INTO jobs (title, company, description, url, job_listing_date, scraped_date) VALUES (?, ?, ?, ?, ?, ?)",
                                    Statement.RETURN_GENERATED_KEYS);
                            pstmt.setString(1, jobTitle);
                            pstmt.setString(2, jobCompany);
                            pstmt.setString(3, jobDescription);
                            pstmt.setString(4, jobUrl);
                            pstmt.setString(5, jobListingDate);
                            pstmt.setDate(6, java.sql.Date.valueOf(String.valueOf(LocalDate.now())));
                            pstmt.executeUpdate();
                            rs = pstmt.getGeneratedKeys();
                            rs.next();
                            int jobId = rs.getInt(1);

                            writeToLog("Job details inserted into the database: " + jobTitle);

                            // Check resume compatibility using Claude API
                            CompatibilityResult result = checkResumeCompatibility(apiKey, resume, jobDescription);

                            // Generate cover letter using Claude API if it's a match
                            if (result.isCompatible()) {
                                if (generateCoverLetter(apiKey, jobTitle, jobCompany, jobDescription, resume, result.getReason(), resumePath, resumesFolder)) {
                                    // Insert cover letter filename and path into the database

                                    String coverLetterFilename = jobTitle.replaceAll("[^a-zA-Z0-9.-]", "_") + ".docx";
                                    String coverLetterPath = resumesFolder + "/" + jobCompany.replaceAll("[^a-zA-Z0-9.-]", "_") + "/"
                                            + coverLetterFilename;

                                    pstmt = conn.prepareStatement(
                                            "INSERT INTO applications (job_id, cover_letter_filename, cover_letter_path) VALUES (?, ?, ?)");
                                    pstmt.setInt(1, jobId);
                                    pstmt.setString(2, coverLetterFilename);
                                    pstmt.setString(3, coverLetterPath);
                                    pstmt.executeUpdate();

                                    writeToLog("Cover letter generated and saved for job: " + jobTitle);

                                    runOnUiThread(() -> Toast.makeText(JobBotActivity.this, "Cover letter generated and saved", Toast.LENGTH_SHORT).show());

                                    saveApplicationToSpreadsheet(jobId, jobCompany, jobTitle, jobUrl, coverLetterFilename, coverLetterPath, resumesFolder);
                                }
                            } else {
                                writeToLog("Resume not compatible with job: " + jobTitle);
                            }
                        }
                    }

                    pageNumber++; // Increment the page number for the next iteration
                }
            }

            writeToLog("Job scraping completed.");

        } catch (Exception e) {
            writeToLog("Exception occurred during job scraping: " + e.getMessage());
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                writeToLog("Exception occurred while closing database connection: " + e.getMessage());
            }
        }
    }

    private CompatibilityResult checkResumeCompatibility(String apiKey, String resume, String jobDescription) {
        String apiVersion = "2023-06-01";
        AnthropicAPI anthropicAPI = new AnthropicAPI(apiKey, apiVersion, "claude-3-sonnet-20240229");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content",
                "You are an AI assistant that determines if a resume is compatible with a job description. Partial matching is okay, and you should use your discretion to determine if it's an okay match. Resume:\n"
                        + "\"" + resume + "\"\n\nJob Description:\n \"" + jobDescription.replace("\n", " ").replace("\r", " ").replace("\"", "") + "\""
                        + "\n\nIs the resume compatible with the job description? Respond starting with 'Yes' or 'No', with a short reason why.");
        messages.add(message);

        String response = anthropicAPI.generateText(messages, 500);

        writeToLog(response);

        if (response != null) {
            String lowerCaseResponse = response.toLowerCase();
            boolean isCompatible = lowerCaseResponse.startsWith("yes");

            // Extract the reason from the response
            String reason = response.substring(response.indexOf(" ") + 1).trim();

            return new CompatibilityResult(isCompatible, reason.replace("\n", " ").replace("\r", " ").replace("\"", ""));
        } else {
            return new CompatibilityResult(false, "No response from the API.");
        }
    }

    private boolean generateCoverLetter(String apiKey, String jobTitle, String jobCompany, String jobDescription, String resume,
                                        String reason, String resumePath, String resumesFolder) {

        String apiVersion = "2023-06-01";
        AnthropicAPI anthropicAPI = new AnthropicAPI(apiKey, apiVersion, "claude-3-opus-20240229");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content",
                "You are an AI assistant that generates personalized cover letters for job applications. Job Title: "
                        + jobTitle + "\nCompany: " + jobCompany + "\n\nJob Description:\n \"" + jobDescription.replace("\n", " ").replace("\r", " ").replace("\"", "") + "\""
                        + "\n\nResume:\n\"" + resume + "\"\n\nReason for applying: \"" + reason
                        + "\"\n\nPlease produce a custom cover letter tailored to this job application, formatted with line breaks represented by '\\n'."
                        + //
                        " Only the completed cover letter should be returned, with the \\n" + //
                        " characters indicating where each new line begins. \n");
        messages.add(message);

        String response = anthropicAPI.generateText(messages, 1024);

        if (response != null) {
            try {

                if (resumePath != null) {
                    // Load the template document
                    XWPFDocument doc = new XWPFDocument(Objects.requireNonNull(getResumeTemplateInputStream(resumePath)));


                    // Create the company folder if it doesn't exist
                    File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                    String companyFolder = jobCompany.replaceAll("[^a-zA-Z0-9.-]", "_");

                    File folder = new File(externalDir, resumesFolder + "/" + companyFolder);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    // Create a new file for the modified document
                    String fileName = externalDir + "/"+ resumesFolder + "/" + companyFolder + "/" + jobTitle.replaceAll("[^a-zA-Z0-9.-]", "_") + ".docx";

                    // Modify the template document
                    List<XWPFParagraph> firstPageParagraphs = new ArrayList<>();
                    int numParagraphs = doc.getParagraphs().size();
                    for (int i = 0; i < numParagraphs; i++) {
                        XWPFParagraph paragraph = doc.getParagraphs().get(i);
                        firstPageParagraphs.add(paragraph);
                        if (paragraph.getCTP().getPPr() != null && paragraph.getCTP().getPPr().getSectPr() != null) {
                            break; // Break the loop when the first page ends
                        }
                    }

                    boolean placeholderFound = false;
                    int placeholderPos = -1;

                    for (XWPFParagraph paragraph : firstPageParagraphs) {
                        List<XWPFRun> runs = paragraph.getRuns();
                        for (XWPFRun run : runs) {
                            String text;
                            try {
                                text = run.getText(0);
                            } catch (org.apache.xmlbeans.impl.values.XmlValueDisconnectedException e) {
                                // Skip the disconnected run
                                continue;
                            }
                            if (text != null && text.contains("{{COVER_LETTER}}")) {
                                placeholderFound = true;
                                placeholderPos = doc.getPosOfParagraph(paragraph);
                                run.setText("", 0); // Remove the placeholder text
                                break;
                            }
                        }
                        if (placeholderFound) {
                            break;
                        }
                    }

                    if (!placeholderFound) {
                        // Ensure the document has at least one paragraph
                        if (doc.getParagraphs().isEmpty()) {
                            doc.createParagraph();
                        }
                        // Reference to the first paragraph to insert the new paragraphs before it
                        XWPFParagraph firstParagraph = doc.getParagraphs().get(0);
                        XWPFParagraph lastParagraph = firstParagraph;

                        // Split the cover letter content into paragraphs
                        String[] coverLetterParagraphs = response.trim().replace("\\n", "\n").split("\n");

                        // Insert the cover letter paragraphs in reverse order
                        for (int i = 0; i < coverLetterParagraphs.length; i++) {
                            String paragraphText = coverLetterParagraphs[i].trim();
                            if (!paragraphText.isEmpty()) {
                                // Insert a new paragraph at the beginning for each paragraph of the cover letter
                                XWPFParagraph newParagraph = doc.insertNewParagraph(firstParagraph.getCTP().newCursor());
                                XWPFRun run = newParagraph.createRun();
                                run.setText(paragraphText);
                                setRunStyle(run);
                                lastParagraph = newParagraph;
                            }
                        }

                        // Add a page break after inserting all cover letter paragraphs
                        XWPFRun pageBreakRun = lastParagraph.createRun();
                        pageBreakRun.addBreak(BreakType.PAGE);
                    }

                    if (placeholderFound && placeholderPos != -1) {
                        String[] coverLetterParagraphs = response.trim().replace("\\n", "\n").split("\n");

                        // Ensure there's at least one paragraph in the response
                        if (coverLetterParagraphs.length > 0) {
                            XWPFParagraph placeholderParagraph = doc.getParagraphs().get(placeholderPos);

                            // Clear all runs in the placeholder paragraph to remove existing
                            // text/formatting
                            while (!placeholderParagraph.getRuns().isEmpty()) {
                                placeholderParagraph.removeRun(0);
                            }

                            // Add a new run with the salutation (first paragraph of the response) to the
                            // placeholder paragraph
                            XWPFRun salutationRun = placeholderParagraph.createRun();
                            salutationRun.setText(coverLetterParagraphs[0], 0);
                            setRunStyle(salutationRun);

                            // Insert subsequent paragraphs after the placeholder paragraph
                            int insertPosition = doc.getPosOfParagraph(placeholderParagraph) + 1;
                            for (int i = 1; i < coverLetterParagraphs.length; i++) {
                                String paragraphText = coverLetterParagraphs[i].trim();
                                if (!paragraphText.isEmpty()) {
                                    // Insert a new paragraph at the determined position
                                    XWPFParagraph newParagraph = doc.insertNewParagraph(
                                            doc.getParagraphs().get(insertPosition).getCTP().newCursor());
                                    if (newParagraph != null) {
                                        XWPFRun newRun = newParagraph.createRun();
                                        newRun.setText(paragraphText);
                                        setRunStyle(newRun);
                                        insertPosition++; // Increment the insert position for the next paragraph
                                    } else {
                                        writeToLog("Could not insert a new paragraph at the desired location.");
                                    }
                                }
                            }
                        }
                    }

                    // Save the modified document

                    if (hasStoragePermissions()) {
                        FileOutputStream out = new FileOutputStream(fileName);
                        doc.write(out);
                        out.close();
                        doc.close();
                        writeToLog("Cover letter saved as " + fileName);

                        return true;
                    } else {
                        Toast.makeText(this, "Storage permissions are required to save files", Toast.LENGTH_SHORT).show();
                    }

                    return false;
                } else {
                    writeToLog("No resume template selected.");
                    return false;
                }
            } catch (Exception e) {
                writeToLog(e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    private void setRunStyle(XWPFRun run) {
        run.setFontSize(11);
        run.setFontFamily("Arial");
        run.setBold(false);
        run.setItalic(false);
        run.setColor("000000");
    }

    private synchronized void saveApplicationToSpreadsheet(int jobId, String company, String title, String url, String coverLetterFilename, String coverLetterPath, String resumesFolder) {
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(externalDir + "/" + resumesFolder + "/", "job_applications.xlsx");

        try (FileInputStream inputStream = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet("Job Applications");
            if (sheet == null) {
                sheet = workbook.createSheet("Job Applications");
                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Job ID");
                headerRow.createCell(1).setCellValue("Company");
                headerRow.createCell(2).setCellValue("Job Title");
                headerRow.createCell(3).setCellValue("Job URL");
                headerRow.createCell(4).setCellValue("Cover Letter Filename");
                headerRow.createCell(5).setCellValue("Cover Letter Path");
            }

            // Append a new row with application data
            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            row.createCell(0).setCellValue(jobId);
            row.createCell(1).setCellValue(company);
            row.createCell(2).setCellValue(title);
            row.createCell(3).setCellValue(url);
            row.createCell(4).setCellValue(coverLetterFilename);
            row.createCell(5).setCellValue(coverLetterPath);

            // Assume 'sheet' is your XSSFSheet object
            for (int i = 0; i < 6; i++) {
                int maxColumnWidth = 0; // Max width in characters

                // Iterate over rows to find the max content width for column 'i'
                for (Row r : sheet) {
                    Cell cell = r.getCell(i);
                    if (cell != null) {
                        int cellWidth = cell.toString().length();
                        if (cellWidth > maxColumnWidth) {
                            maxColumnWidth = cellWidth;
                        }
                    }
                }

                // Adjust column width if necessary
                if (maxColumnWidth > 0) {
                    // Set the column width (256 is the factor Excel uses)
                    sheet.setColumnWidth(i, Math.min(255*256, 256 * (maxColumnWidth + 2))); // +2 for a little extra space
                }
            }

            // Close the InputStream
            inputStream.close();

            // Write the changes to the file
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            // Close the Workbook to free resources
            workbook.close();
        } catch (FileNotFoundException e) {
            // If the file does not exist, create a new workbook and sheet
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Job Applications");
                if (sheet != null) {
                    // Create header row
                    Row headerRow = sheet.createRow(0);
                    headerRow.createCell(0).setCellValue("Job ID");
                    headerRow.createCell(1).setCellValue("Company");
                    headerRow.createCell(2).setCellValue("Job Title");
                    headerRow.createCell(3).setCellValue("Job URL");
                    headerRow.createCell(4).setCellValue("Cover Letter Filename");
                    headerRow.createCell(5).setCellValue("Cover Letter Path");

                    // Append a new row with application data
                    int newRowNum = sheet.getLastRowNum() + 1;
                    Row row = sheet.createRow(newRowNum);
                    row.createCell(0).setCellValue(jobId);
                    row.createCell(1).setCellValue(company);
                    row.createCell(2).setCellValue(title);
                    row.createCell(3).setCellValue(url);
                    row.createCell(4).setCellValue(coverLetterFilename);
                    row.createCell(5).setCellValue(coverLetterPath);

                    // Assume 'sheet' is your XSSFSheet object
                    for (int i = 0; i < 6; i++) {
                        int maxColumnWidth = 0; // Max width in characters

                        // Iterate over rows to find the max content width for column 'i'
                        for (Row r : sheet) {
                            Cell cell = r.getCell(i);
                            if (cell != null) {
                                int cellWidth = cell.toString().length();
                                if (cellWidth > maxColumnWidth) {
                                    maxColumnWidth = cellWidth;
                                }
                            }
                        }

                        // Adjust column width if necessary
                        if (maxColumnWidth > 0) {
                            // Set the column width (256 is the factor Excel uses)
                            sheet.setColumnWidth(i, Math.min(255 * 256, 256 * (maxColumnWidth + 2))); // +2 for a little extra space
                        }
                    }
                }
                // Write the new workbook to the file
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }
            } catch (IOException ex) {
                writeToLog(ex.getMessage());
            }
        } catch (IOException e) {
            writeToLog(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (scrapingInProgress) {
            stopScraping();
        } else {
            super.onBackPressed();
        }
    }
}