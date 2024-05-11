package ai.j0b.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ai.j0b.model.AcademicModel;
import ai.j0b.model.ExperienceModel;
import ai.j0b.model.ReferencesModel;
import ai.j0b.model.ResumeData;

public final class Constant {
    private static final String FILENAME = "PREFERENCES_FILE";
    public static final Constant INSTANCE = new Constant();

    // Request codes
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int GALLERY_REQUEST_CODE = 102;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 103;
    public static final int REQUEST_ID_CAMERA_PERMISSIONS = 104;
    public static final int REQUEST_ID_GALLERY_PERMISSIONS = 105;

    // Keys for shared preferences
    private static final String PROFILE_IMAGE = "profileImage";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String EMAIL = "email";
    private static final String ADDRESS = "address";
    private static final String FIELD = "field";
    private static final String ABOUT_ME = "aboutMe";
    private static final String ACADEMIC = "academic";
    private static final String EXPERIENCE = "experience";
    private static final String REFERENCES = "references";
    private static final String SKILLS = "skills";
    private static final String LANGUAGE = "language";
    private static final String HOBBIES = "hobbies";
    private static final String MY_RESUME = "myResume";

    private Constant() {
    }

    // Getters for keys
    public String getProfileImageKey() {
        return PROFILE_IMAGE;
    }

    public String getNameKey() {
        return NAME;
    }

    public String getNumberKey() {
        return NUMBER;
    }

    public String getEmailKey() {
        return EMAIL;
    }

    public String getAddressKey() {
        return ADDRESS;
    }

    public String getFieldKey() {
        return FIELD;
    }

    public String getAboutMeKey() {
        return ABOUT_ME;
    }

    public String getAcademicKey() {
        return ACADEMIC;
    }

    public String getExperienceKey() {
        return EXPERIENCE;
    }

    public String getReferencesKey() {
        return REFERENCES;
    }

    public String getSkillsKey() {
        return SKILLS;
    }

    public String getLanguageKey() {
        return LANGUAGE;
    }

    public String getHobbiesKey() {
        return HOBBIES;
    }

    public String getMyResumeKey() {
        return MY_RESUME;
    }


    public void saveData(Context context, String key, String data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public boolean emailValidator(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean phoneValidator(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public String createBase64(File file) {
        Bitmap bm = BitmapFactory.decodeFile(file.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return "data:image/jpg;base64," + encodedImage.replaceAll("\\s+", "");
    }

    public Calendar strToCalendar(String date) {
        Calendar dateMaster = Calendar.getInstance();
        String[] dateParts = date.split("-");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Months are 0-based in Calendar
        int day = Integer.parseInt(dateParts[2]);

        dateMaster.set(year, month, day);
        return dateMaster;
    }

    public void saveResumeData(Context context, ResumeData resumeData) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(NAME, resumeData.getName());
        editor.putString(FIELD, resumeData.getProfession());
        editor.putString(EMAIL, resumeData.getEmail());
        editor.putString(NUMBER, resumeData.getPhone());
        editor.putString(ADDRESS, resumeData.getLocation());
        editor.putString(ABOUT_ME, resumeData.getSummary());
        editor.putString(SKILLS, new Gson().toJson(resumeData.getSkills()));

        List<ExperienceModel> experienceList = new ArrayList<>();
        for (ResumeData.WorkExperience workExperience : resumeData.getWorkExperience()) {
            ExperienceModel experienceModel = new ExperienceModel();
            experienceModel.setOrganization(workExperience.getCompany());
            experienceModel.setFromDate(workExperience.getStartDate());
            experienceModel.setToDate(workExperience.getEndDate());
            experienceModel.setContinue(false); // Set isContinue to false since it's not available in WorkExperience
            experienceModel.setRole(workExperience.getPosition());
            experienceList.add(experienceModel);
        }

        editor.putString(EXPERIENCE, new Gson().toJson(experienceList));

        List<AcademicModel> academicList = new ArrayList<>();
        for (ResumeData.Education education : resumeData.getEducation()) {
            AcademicModel academicModel = new AcademicModel();
            academicModel.setName(education.getDegree());
            academicModel.setInstitute(education.getInstitution());
            academicModel.setPercentage(null); // Set percentage to null if not available
            academicModel.setCgpa(null); // Set CGPA to null if not available
            academicModel.setYear(education.getEndDate()); // Use end date as the year
            academicList.add(academicModel);
        }

        // Save the academic data as a JSON string
        editor.putString(ACADEMIC, new Gson().toJson(academicList));

        List<ReferencesModel> referencesList = new ArrayList<>();
        for (ResumeData.Reference reference : resumeData.getReferences()) {
            ReferencesModel referencesModel = new ReferencesModel();
            referencesModel.setName(reference.getName());
            referencesModel.setEmail(reference.getEmail());
            referencesModel.setNumber(reference.getPhone());
            referencesModel.setOrganization(reference.getOrganisation());
            referencesModel.setDesignation(reference.getRole()); // Set designation to null since it's not available in Reference
            referencesList.add(referencesModel);
        }

        // Save the references data as a JSON string
        editor.putString(REFERENCES, new Gson().toJson(referencesList));

        editor.apply();
    }

    public ResumeData getResumeData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);

        ResumeData resumeData = new ResumeData();
        resumeData.setName(sharedPreferences.getString(NAME, null));
        resumeData.setProfession(sharedPreferences.getString(FIELD, null));
        resumeData.setEmail(sharedPreferences.getString(EMAIL, null));
        resumeData.setPhone(sharedPreferences.getString(NUMBER, null));
        resumeData.setLocation(sharedPreferences.getString(ADDRESS, null));
        resumeData.setSummary(sharedPreferences.getString(ABOUT_ME, null));
        resumeData.setSkills(new Gson().fromJson(sharedPreferences.getString(SKILLS, null), new TypeToken<ArrayList<String>>() {}.getType()));

        String experienceJson = sharedPreferences.getString(EXPERIENCE, null);
        if (experienceJson != null) {
            List<ExperienceModel> experienceList = new Gson().fromJson(experienceJson, new TypeToken<ArrayList<ExperienceModel>>() {}.getType());
            resumeData.setWorkExperience(convertToWorkExperienceList(experienceList));
        }

        String academicJson = sharedPreferences.getString(ACADEMIC, null);
        if (academicJson != null) {
            List<AcademicModel> academicList = new Gson().fromJson(academicJson, new TypeToken<ArrayList<AcademicModel>>() {}.getType());
            resumeData.setEducation(convertToEducationList(academicList));
        }

        String referencesJson = sharedPreferences.getString(REFERENCES, null);
        if (referencesJson != null) {
            List<ReferencesModel> referencesList = new Gson().fromJson(referencesJson, new TypeToken<ArrayList<ReferencesModel>>() {}.getType());
            resumeData.setReferences(convertToReferenceList(referencesList));
        }

        return resumeData;
    }

    private List<ResumeData.Education> convertToEducationList(List<AcademicModel> academicList) {
        List<ResumeData.Education> educationList = new ArrayList<>();
        for (AcademicModel academicModel : academicList) {
            ResumeData.Education education = new ResumeData.Education();
            education.setFieldOfStudy(academicModel.getName());
            education.setInstitution(academicModel.getInstitute());
            education.setEndDate(academicModel.getYear());
            educationList.add(education);
        }
        return educationList;
    }


    private List<ResumeData.WorkExperience> convertToWorkExperienceList(List<ExperienceModel> experienceList) {
        List<ResumeData.WorkExperience> workExperienceList = new ArrayList<>();
        for (ExperienceModel experienceModel : experienceList) {
            ResumeData.WorkExperience workExperience = new ResumeData.WorkExperience();
            workExperience.setCompany(experienceModel.getOrganization());
            workExperience.setStartDate(experienceModel.getFromDate());
            workExperience.setEndDate(experienceModel.getToDate());
            workExperience.setDescription(experienceModel.getRole());
            workExperienceList.add(workExperience);
        }
        return workExperienceList;
    }

    private List<ResumeData.Reference> convertToReferenceList(List<ReferencesModel> referencesList) {
        List<ResumeData.Reference> referenceList = new ArrayList<>();
        for (ReferencesModel referencesModel : referencesList) {
            ResumeData.Reference reference = new ResumeData.Reference();
            reference.setName(referencesModel.getName());
            reference.setEmail(referencesModel.getEmail());
            reference.setPhone(referencesModel.getNumber());
            reference.setOrganisation(referencesModel.getOrganization());
            reference.setRole(referencesModel.getDesignation());
            referenceList.add(reference);
        }
        return referenceList;
    }
}
