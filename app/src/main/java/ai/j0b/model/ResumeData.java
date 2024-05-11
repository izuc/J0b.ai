package ai.j0b.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import ai.j0b.utils.AnthropicAPI;

public class ResumeData {
    private String name;

    private String profession;
    private String email;
    private String phone;

    private String location;
    private String summary;
    private List<String> skills;
    private List<WorkExperience> workExperience;
    private List<Education> education;
    private List<Reference> references;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<WorkExperience> getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(List<WorkExperience> workExperience) {
        this.workExperience = workExperience;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public static class WorkExperience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private String description;

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Education {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String startDate;
        private String endDate;

        public String getInstitution() {
            return institution;
        }

        public void setInstitution(String institution) {
            this.institution = institution;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getFieldOfStudy() {
            return fieldOfStudy;
        }

        public void setFieldOfStudy(String fieldOfStudy) {
            this.fieldOfStudy = fieldOfStudy;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class Reference {
        private String name;
        private String email;
        private String phone;
        private String organisation;
        private String role;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOrganisation() {
            return organisation;
        }

        public void setOrganisation(String organisation) {
            this.organisation = organisation;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static String extractJsonFromResponse(String response) {
        // Find the start of the JSON data
        int jsonStart = response.indexOf("{");
        int jsonEnd = response.lastIndexOf("}");

        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            String json = response.substring(jsonStart, jsonEnd + 1);

            // Attempt to validate the extracted JSON
            try {
                new JSONObject(json);
                Log.d("JsonExtractor", "Extracted JSON: " + json);
                return json;
            } catch (JSONException e) {
                Log.e("JsonExtractor", "Error parsing JSON: " + e.getMessage());
                return "";
            }
        } else {
            Log.e("JsonExtractor", "Could not find JSON boundaries in response.");
            return "";
        }
    }

    public static String extractResumeData(Context context, String resume) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        //SharedPreferences sharedPreferences = getSharedPreferences("ai.j0b_preferences", Context.MODE_PRIVATE);
        String apiKey = sharedPreferences.getString("api_key", "");
        String apiVersion = "2023-06-01";
        AnthropicAPI anthropicAPI = new AnthropicAPI(apiKey, apiVersion, "claude-3-haiku-20240307"); //claude-3-opus-20240229

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");

        String query = "You are an AI assistant that extracts data from a resume. Please extract the following data points from the resume text provided and return the results in JSON format:\n\n" +
                "- Name\n" +
                "- Profession\n" +
                "- Email\n" +
                "- Phone\n" +
                "- Location\n" +
                "- Summary\n" +
                "- Skills (as an array)\n" +
                "- Work Experience (as an array of objects with 'company', 'position', 'startDate', 'endDate', and 'description')\n" +
                "- Education (as an array of objects with 'institution', 'degree', 'fieldOfStudy', 'startDate', and 'endDate')\n\n" +
                "- References (as an array of objects with 'name', 'email', 'phone', and 'organization')\n\n" +
                "Example output:\n" +
                "```json\n" +
                "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"profession\": \"Software Developer\",\n" +
                "  \"email\": \"johndoe@example.com\",\n" +
                "  \"phone\": \"123-456-7890\",\n" +
                "  \"location\": \"Sydney\",\n" +
                "  \"summary\": \"A skilled software engineer with experience in developing web applications.\",\n" +
                "  \"skills\": [\n" +
                "    \"Java\",\n" +
                "    \"Python\",\n" +
                "    \"JavaScript\",\n" +
                "    \"HTML\",\n" +
                "    \"CSS\"\n" +
                "  ],\n" +
                "  \"workExperience\": [\n" +
                "    {\n" +
                "      \"company\": \"ABC Company\",\n" +
                "      \"position\": \"Software Engineer\",\n" +
                "      \"startDate\": \"2018-01-01\",\n" +
                "      \"endDate\": \"2021-12-31\",\n" +
                "      \"description\": \"Developed and maintained web applications using Java and Spring framework.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"company\": \"XYZ Corporation\",\n" +
                "      \"position\": \"Software Developer\",\n" +
                "      \"startDate\": \"2016-06-01\",\n" +
                "      \"endDate\": \"2017-12-31\",\n" +
                "      \"description\": \"Implemented features for a mobile app using Android SDK.\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"education\": [\n" +
                "    {\n" +
                "      \"institution\": \"University of XYZ\",\n" +
                "      \"degree\": \"Bachelor of Science\",\n" +
                "      \"fieldOfStudy\": \"Computer Science\",\n" +
                "      \"startDate\": \"2012-09-01\",\n" +
                "      \"endDate\": \"2016-05-31\"\n" +
                "    }\n" +
                "  \"references\": [\n" +
                "    {\n" +
                "      \"name\": \"Jane Doe\",\n" +
                "      \"email\": \"jane@example.com\",\n" +
                "      \"phone\": \"123-456-7890\",\n" +
                "      \"organization\": \"ABC Company\",\n" +
                "      \"role\": \"Manager\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "```\n\n" +
                "Resume text:\n" +
                resume;

        message.put("content", query);
        messages.add(message);

        String response = anthropicAPI.generateText(messages, 4096);
        Log.e("ResumeData", response);

        return response;
    }

    public static ResumeData bindJsonToResumeData(String json) {
        ResumeData resumeData = new ResumeData();

        System.out.println(json);

        if (json != null) {
            try {
                JsonElement element = JsonParser.parseString(json);
                if (element != null && element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();

                    // Bind name
                    resumeData.setName(getStringFromJson(jsonObject, "name"));

                    // Bind profession
                    resumeData.setProfession(getStringFromJson(jsonObject, "profession"));

                    // Bind email
                    resumeData.setEmail(getStringFromJson(jsonObject, "email"));

                    // Bind phone
                    resumeData.setPhone(getStringFromJson(jsonObject, "phone"));

                    // Bind location
                    resumeData.setLocation(getStringFromJson(jsonObject, "location"));

                    // Bind summary
                    resumeData.setSummary(getStringFromJson(jsonObject, "summary"));

                    // Bind skills
                    resumeData.setSkills(getListFromJson(jsonObject, "skills"));

                    // Bind work experience
                    resumeData.setWorkExperience(getWorkExperienceListFromJson(jsonObject, "workExperience"));

                    // Bind education
                    resumeData.setEducation(getEducationListFromJson(jsonObject, "education"));

                    // Bind references
                    resumeData.setReferences(getReferenceListFromJson(jsonObject, "references"));
                } else {
                    Log.e("ResumeData", "Error parsing JSON: " + element);
                }
            } catch (IllegalStateException e) {
                Log.e("ResumeData", "Error parsing JSON: " + e.getMessage());
            }
        } else {
            Log.e("ResumeData", "JSON string is null");
        }

        return resumeData;
    }

    private static String getStringFromJson(JsonObject jsonObject, String key) {
        if (jsonObject != null && key != null) {
            // Iterate through all keys in the JSON object
            for (String jsonKey : jsonObject.keySet()) {
                // Check if the current key matches the given key, ignoring case
                if (jsonKey.equalsIgnoreCase(key)) {
                    // If the matched key's value is not a JSON null, return its string value
                    if (!jsonObject.get(jsonKey).isJsonNull()) {
                        return jsonObject.get(jsonKey).getAsString();
                    }
                    break; // Exit the loop as we found the key (ignoring case)
                }
            }
        }
        return null; // Return null if the key wasn't found or if the jsonObject/key is null
    }

    private static List<String> getListFromJson(JsonObject jsonObject, String key) {
        List<String> list = new ArrayList<>();
        if (jsonObject != null && jsonObject.has(key) && jsonObject.get(key).isJsonArray()) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            for (JsonElement element : jsonArray) {
                if (!element.isJsonNull()) {
                    list.add(element.getAsString());
                }
            }
        }
        return list;
    }

    private static List<ResumeData.WorkExperience> getWorkExperienceListFromJson(JsonObject jsonObject, String key) {
        List<ResumeData.WorkExperience> list = new ArrayList<>();
        // Use the getJsonArrayFromJson method to get the JsonArray
        JsonArray jsonArray = getJsonArrayFromJson(jsonObject, key);

        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject workExperienceObject = element.getAsJsonObject();
                    ResumeData.WorkExperience workExperience = new ResumeData.WorkExperience();
                    workExperience.setCompany(getStringFromJson(workExperienceObject, "company"));
                    workExperience.setPosition(getStringFromJson(workExperienceObject, "position"));
                    workExperience.setStartDate(getStringFromJson(workExperienceObject, "startDate"));
                    workExperience.setEndDate(getStringFromJson(workExperienceObject, "endDate"));
                    workExperience.setDescription(getStringFromJson(workExperienceObject, "description"));
                    list.add(workExperience);
                }
            }
        }
        return list;
    }

    private static List<ResumeData.Education> getEducationListFromJson(JsonObject jsonObject, String key) {
        List<ResumeData.Education> list = new ArrayList<>();
        // Use the getJsonArrayFromJson method to get the JsonArray
        JsonArray jsonArray = getJsonArrayFromJson(jsonObject, key);

        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject educationObject = element.getAsJsonObject();
                    ResumeData.Education education = new ResumeData.Education();
                    education.setInstitution(getStringFromJson(educationObject, "institution"));
                    education.setDegree(getStringFromJson(educationObject, "degree"));
                    education.setFieldOfStudy(getStringFromJson(educationObject, "fieldOfStudy"));
                    education.setStartDate(getStringFromJson(educationObject, "startDate"));
                    education.setEndDate(getStringFromJson(educationObject, "endDate"));
                    list.add(education);
                }
            }
        }
        return list;
    }

    private static List<ResumeData.Reference> getReferenceListFromJson(JsonObject jsonObject, String key) {
        List<ResumeData.Reference> list = new ArrayList<>();
        // Use the new method to get the JsonArray
        JsonArray jsonArray = getJsonArrayFromJson(jsonObject, key);

        if (jsonArray != null) {
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject referenceObject = element.getAsJsonObject();
                    ResumeData.Reference reference = new ResumeData.Reference();
                    reference.setName(getStringFromJson(referenceObject, "name"));
                    reference.setEmail(getStringFromJson(referenceObject, "email"));
                    reference.setPhone(getStringFromJson(referenceObject, "phone"));
                    reference.setOrganisation(getStringFromJson(referenceObject, "organization"));
                    reference.setRole(getStringFromJson(referenceObject, "role"));
                    list.add(reference);
                }
            }
        }
        return list;
    }


    private static JsonArray getJsonArrayFromJson(JsonObject jsonObject, String key) {
        if (jsonObject != null && key != null) {
            // Iterate through all keys to find a case-insensitive match
            for (String jsonKey : jsonObject.keySet()) {
                if (jsonKey.equalsIgnoreCase(key)) {
                    // Check if the value associated with the matched key is a JsonArray
                    if (jsonObject.get(jsonKey).isJsonArray()) {
                        return jsonObject.getAsJsonArray(jsonKey);
                    }
                    break; // Matching key found, no need to continue the loop
                }
            }
        }
        return null; // Return null if no matching JsonArray is found
    }
}
