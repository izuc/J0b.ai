package ai.j0b.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.j0b.model.AcademicModel;
import ai.j0b.model.ExperienceModel;
import ai.j0b.model.ReferencesModel;
import ai.j0b.view.fragment.resume.adapter.EducationAdapter1;
import ai.j0b.view.fragment.resume.adapter.ExperienceAdapter1;
import ai.j0b.view.fragment.resume.adapter.ReferencesAdapter1;
import ai.j0b.view.fragment.resume.adapter.TextAdapter1;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class ResumeBuilder {
    private static ViewGroup container;
    private static Context context;

    private static String apiKey;

    private static String apiVersion;

    private static String model;

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    private static Map<String, Integer> viewIdMap = new HashMap<>();

    private static OnResumeGenerationListener listener;

    public static void init(Context context, OnResumeGenerationListener listener, String apiKey, String apiVersion, String model) {
        ResumeBuilder.context = context;
        ResumeBuilder.listener = listener;
        ResumeBuilder.apiKey = apiKey;
        ResumeBuilder.apiVersion = apiVersion;
        ResumeBuilder.model = model;
    }

//    public static void generateFragment(FrameLayout container, String prompt) {
//        Log.d(TAG, "generateFragment() called with prompt: " + prompt);
//
//        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        ResumeFragment resumeFragment = ResumeFragment.newInstance(prompt);
//        fragmentTransaction.replace(container.getId(), resumeFragment);
//        fragmentTransaction.commit();
//    }

    public interface OnXmlGeneratedListener {
        void onXmlGenerated(String xml);
    }

    public static void generateResumeView(FrameLayout frame, String prompt, OnResumeGenerationListener listener, OnXmlGeneratedListener xmlListener) {
        Log.d(TAG, "generateResumeView() called with prompt: " + prompt);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
//                // Prepare the prompt for Claude to generate the personal info section
//                String personalInfoPrompt = prompt + " Please generate an XML layout for the personal information section of a resume view. " +
//                        "Include an ImageView for the profile image with ID '@+id/profileImageView' and TextViews for name (ID: '@+id/nameTextView'), " +
//                        "profession (ID: '@+id/professionTextView'), email (ID: '@+id/emailTextView'), phone (ID: '@+id/phoneTextView'), " +
//                        "location (ID: '@+id/locationTextView'), and summary (ID: '@+id/summaryTextView'). " +
//                        "Allow customization of colors and content. Use the following keys to retrieve data from SharedPreferences: " +
//                        "Profile Image: " + Constant.INSTANCE.getProfileImageKey() + ", " +
//                        "Name: " + Constant.INSTANCE.getNameKey() + ", " +
//                        "Profession: " + Constant.INSTANCE.getFieldKey() + ", " +
//                        "Email: " + Constant.INSTANCE.getEmailKey() + ", " +
//                        "Phone: " + Constant.INSTANCE.getNumberKey() + ", " +
//                        "Location: " + Constant.INSTANCE.getAddressKey() + ", " +
//                        "Summary: " + Constant.INSTANCE.getAboutMeKey() + ". " +
//                        "Return only the XML layout without any additional explanations.";
//
//                // Call the AnthropicAPI to generate the personal info section XML
//                String personalInfoXml = stripNonXmlContent(generateXml(personalInfoPrompt));
//
//                // Prepare the prompt for Claude to generate the work experience section
//                String workExperiencePrompt = prompt + " Please generate an XML layout for the work experience section of a resume view. " +
//                        "Include a RecyclerView with ID '@+id/workExperienceRecyclerView' to display work experience items using the ExperienceAdapter. " +
//                        "Allow customization of colors and content. Use the following key to retrieve data from SharedPreferences: " +
//                        "Work Experience: " + Constant.INSTANCE.getExperienceKey() + ". " +
//                        "Return only the XML layout without any additional explanations.";
//
//                // Call the AnthropicAPI to generate the work experience section XML
//                String workExperienceXml = stripNonXmlContent(generateXml(workExperiencePrompt));
//
//                // Prepare the prompt for Claude to generate the education section
//                String educationPrompt = prompt + " Please generate an XML layout for the education section of a resume view. " +
//                        "Include a RecyclerView with ID '@+id/educationRecyclerView' to display education items using the AcademicAdapter. " +
//                        "Allow customization of colors and content. Use the following key to retrieve data from SharedPreferences: " +
//                        "Education: " + Constant.INSTANCE.getAcademicKey() + ". " +
//                        "Return only the XML layout without any additional explanations.";
//
//                // Call the AnthropicAPI to generate the education section XML
//                String educationXml = stripNonXmlContent(generateXml(educationPrompt));
//
//                // Prepare the prompt for Claude to generate the skills section
//                String skillsPrompt = prompt + " Please generate an XML layout for the skills section of a resume view. " +
//                        "Include a RecyclerView with ID '@+id/skillsRecyclerView' to display skill items using the SkillsAdapter. " +
//                        "Allow customization of colors and content. Use the following key to retrieve data from SharedPreferences: " +
//                        "Skills: " + Constant.INSTANCE.getSkillsKey() + ". " +
//                        "Return only the XML layout without any additional explanations.";
//
//                // Call the AnthropicAPI to generate the skills section XML
//                String skillsXml = stripNonXmlContent(generateXml(skillsPrompt));
//
//                // Prepare the prompt for Claude to generate the references section
//                String referencesPrompt = prompt + " Please generate an XML layout for the references section of a resume view. " +
//                        "Include a RecyclerView with ID '@+id/referencesRecyclerView' to display reference items using the ReferencesAdapter. " +
//                        "Allow customization of colors and content. Use the following key to retrieve data from SharedPreferences: " +
//                        "References: " + Constant.INSTANCE.getReferencesKey() + ". " +
//                        "Return only the XML layout without any additional explanations.";
//
//                // Call the AnthropicAPI to generate the references section XML
//                String referencesXml = stripNonXmlContent(generateXml(referencesPrompt));
//
//                // Combine the generated XML sections
//                String combinedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
//                        "    android:layout_width=\"match_parent\"\n" +
//                        "    android:layout_height=\"wrap_content\"\n" +
//                        "    android:orientation=\"vertical\">\n" +
//                        personalInfoXml + "\n" +
//                        workExperienceXml + "\n" +
//                        educationXml + "\n" +
//                        skillsXml + "\n" +
//                        referencesXml + "\n" +
//                        "</LinearLayout>";


                String combinedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\">\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\"\n" +
                        "\tandroid:padding=\"16dp\"\n" +
                        "\tandroid:background=\"@color/blue\"><ImageView\n" +
                        "\t\tandroid:id=\"@+id/profileImageView\"\n" +
                        "\t\tandroid:layout_width=\"120dp\"\n" +
                        "\t\tandroid:layout_height=\"120dp\"\n" +
                        "\t\tandroid:layout_gravity=\"center_horizontal\"\n" +
                        "\t\tandroid:src=\"@drawable/profile_placeholder\"\n" +
                        "\t\tandroid:contentDescription=\"Profile Image\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/nameTextView\"\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:layout_gravity=\"center_horizontal\"\n" +
                        "\t\tandroid:text=\"@string/name\"\n" +
                        "\t\tandroid:textSize=\"24sp\"\n" +
                        "\t\tandroid:textStyle=\"bold\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"16dp\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/professionTextView\"\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:layout_gravity=\"center_horizontal\"\n" +
                        "\t\tandroid:text=\"@string/profession\"\n" +
                        "\t\tandroid:textSize=\"18sp\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"8dp\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/emailTextView\"\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/email\"\n" +
                        "\t\tandroid:textSize=\"16sp\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"16dp\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/phoneTextView\"\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/phone\"\n" +
                        "\t\tandroid:textSize=\"16sp\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"8dp\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/locationTextView\"\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/location\"\n" +
                        "\t\tandroid:textSize=\"16sp\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"8dp\" /><TextView\n" +
                        "\t\tandroid:id=\"@+id/summaryTextView\"\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/summary\"\n" +
                        "\t\tandroid:textSize=\"16sp\"\n" +
                        "\t\tandroid:textColor=\"@android:color/white\"\n" +
                        "\t\tandroid:layout_marginTop=\"16dp\" /></LinearLayout>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\"\n" +
                        "\tandroid:padding=\"16dp\"><TextView\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"Work Experience\"\n" +
                        "\t\tandroid:textSize=\"24sp\"\n" +
                        "\t\tandroid:textStyle=\"bold\"\n" +
                        "\t\tandroid:textColor=\"@color/colorPrimary\"\n" +
                        "\t\tandroid:layout_marginBottom=\"16dp\"/><androidx.recyclerview.widget.RecyclerView\n" +
                        "\t\tandroid:id=\"@+id/workExperienceRecyclerView\"\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:scrollbars=\"vertical\"\n" +
                        "\t\tandroid:overScrollMode=\"never\"/></LinearLayout>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\"\n" +
                        "\tandroid:background=\"@color/sectionBackground\"><TextView\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/education_heading\"\n" +
                        "\t\tandroid:textSize=\"@dimen/sectionHeadingTextSize\"\n" +
                        "\t\tandroid:textColor=\"@color/sectionHeadingTextColor\"\n" +
                        "\t\tandroid:textStyle=\"bold\"\n" +
                        "\t\tandroid:padding=\"@dimen/sectionHeadingPadding\"/><View\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"@dimen/sectionDividerHeight\"\n" +
                        "\t\tandroid:background=\"@color/sectionDividerColor\"/><androidx.recyclerview.widget.RecyclerView\n" +
                        "\t\tandroid:id=\"@+id/educationRecyclerView\"\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:padding=\"@dimen/sectionContentPadding\"/></LinearLayout>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\"\n" +
                        "\tandroid:padding=\"16dp\"><TextView\n" +
                        "\t\tandroid:layout_width=\"wrap_content\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/skills_heading\"\n" +
                        "\t\tandroid:textSize=\"20sp\"\n" +
                        "\t\tandroid:textStyle=\"bold\"\n" +
                        "\t\tandroid:textColor=\"@color/skills_heading_color\" /><View\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"1dp\"\n" +
                        "\t\tandroid:background=\"@color/skills_divider_color\"\n" +
                        "\t\tandroid:layout_marginTop=\"8dp\"\n" +
                        "\t\tandroid:layout_marginBottom=\"16dp\" /><androidx.recyclerview.widget.RecyclerView\n" +
                        "\t\tandroid:id=\"@+id/skillsRecyclerView\"\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:scrollbars=\"none\" /></LinearLayout>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "\tandroid:layout_width=\"match_parent\"\n" +
                        "\tandroid:layout_height=\"wrap_content\"\n" +
                        "\tandroid:orientation=\"vertical\"\n" +
                        "\tandroid:padding=\"16dp\"><TextView\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:text=\"@string/references_heading\"\n" +
                        "\t\tandroid:textSize=\"20sp\"\n" +
                        "\t\tandroid:textStyle=\"bold\"\n" +
                        "\t\tandroid:textColor=\"@color/heading_color\"\n" +
                        "\t\tandroid:layout_marginBottom=\"8dp\"/><androidx.recyclerview.widget.RecyclerView\n" +
                        "\t\tandroid:id=\"@+id/referencesRecyclerView\"\n" +
                        "\t\tandroid:layout_width=\"match_parent\"\n" +
                        "\t\tandroid:layout_height=\"wrap_content\"\n" +
                        "\t\tandroid:overScrollMode=\"never\"\n" +
                        "\t\tandroid:clipToPadding=\"false\"/></LinearLayout>\n" +
                        "</LinearLayout>";

                handler.post(() -> {
                    inflateView(frame, combinedXml, listener, xmlListener);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error generating resume: " + e.getMessage());
                handler.post(() -> {
                    if (listener != null) {
                        listener.onResumeGenerationError("Failed to generate resume");
                    }
                });
            }
        });
    }

    private static void setupUI(ViewGroup rootView) {
        Log.d(TAG, "setupUI() called");
        // Find views in the rootView using their IDs
        Log.d(TAG, "testing values = " + viewIdMap.toString());
        //testing values = {summaryTextView=8, profileImageView=2, nameTextView=3, locationTextView=7, workExperienceRecyclerView=9, referencesRecyclerView=12, professionTextView=4, emailTextView=5, phoneTextView=6, skillsRecyclerView=11, educationRecyclerView=10}
        //2024-04-06 18:59:00.856  2660-2660  ContentValues           ai.j0b                               D  testing value = -1

        Log.d(TAG, "testing value = " + getId("@+id/profileImageView"));
        ImageView profileImageView = rootView.findViewWithTag(getId("@+id/profileImageView"));
        TextView nameTextView = rootView.findViewWithTag(getId("@+id/nameTextView"));
        TextView professionTextView = rootView.findViewWithTag(getId("@+id/professionTextView"));
        TextView emailTextView = rootView.findViewWithTag(getId("@+id/emailTextView"));
        TextView phoneTextView = rootView.findViewWithTag(getId("@+id/phoneTextView"));
        TextView locationTextView = rootView.findViewWithTag(getId("@+id/locationTextView"));
        TextView summaryTextView = rootView.findViewWithTag(getId("@+id/summaryTextView"));
        RecyclerView workExperienceRecyclerView = rootView.findViewWithTag(getId("@+id/workExperienceRecyclerView"));
        RecyclerView educationRecyclerView = rootView.findViewWithTag(getId("@+id/educationRecyclerView"));
        RecyclerView skillsRecyclerView = rootView.findViewWithTag(getId("@+id/skillsRecyclerView"));
        RecyclerView referencesRecyclerView = rootView.findViewWithTag(getId("@+id/referencesRecyclerView"));

        // Load data into views using the loadData() and loadListData() methods
        loadData(Constant.INSTANCE.getProfileImageKey(), data -> {
            if (profileImageView != null) {
                Glide.with(context).load((String) data).into(profileImageView);
            } else {
                Log.e(TAG, "profileImageView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getNameKey(), data -> {
            if (nameTextView != null) {
                nameTextView.setText((String) data);
            } else {
                Log.e(TAG, "nameTextView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getFieldKey(), data -> {
            if (professionTextView != null) {
                professionTextView.setText((String) data);
            } else {
                Log.e(TAG, "professionTextView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getEmailKey(), data -> {
            if (emailTextView != null) {
                emailTextView.setText((String) data);
            } else {
                Log.e(TAG, "emailTextView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getNumberKey(), data -> {
            if (phoneTextView != null) {
                phoneTextView.setText((String) data);
            } else {
                Log.e(TAG, "phoneTextView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getAddressKey(), data -> {
            if (locationTextView != null) {
                locationTextView.setText((String) data);
            } else {
                Log.e(TAG, "locationTextView not found in the layout");
            }
        });

        loadData(Constant.INSTANCE.getAboutMeKey(), data -> {
            if (summaryTextView != null) {
                summaryTextView.setText((String) data);
            } else {
                Log.e(TAG, "summaryTextView not found in the layout");
            }
        });

        // Set up RecyclerViews using the loadListData() method
        loadListData(Constant.INSTANCE.getExperienceKey(), new TypeToken<ArrayList<ExperienceModel>>() {}, list -> {
            if (workExperienceRecyclerView != null) {
                workExperienceRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                workExperienceRecyclerView.setAdapter(new ExperienceAdapter1(context, list, "color1"));
            } else {
                Log.e(TAG, "workExperienceRecyclerView not found in the layout");
            }
        });

        loadListData(Constant.INSTANCE.getAcademicKey(), new TypeToken<ArrayList<AcademicModel>>() {}, list -> {
            if (educationRecyclerView != null) {
                educationRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                educationRecyclerView.setAdapter(new EducationAdapter1(context, list));
            } else {
                Log.e(TAG, "educationRecyclerView not found in the layout");
            }
        });

        loadListData(Constant.INSTANCE.getSkillsKey(), new TypeToken<ArrayList<String>>() {}, list -> {
            if (skillsRecyclerView != null) {
                skillsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                skillsRecyclerView.setAdapter(new TextAdapter1(context, list));
            } else {
                Log.e(TAG, "skillsRecyclerView not found in the layout");
            }
        });

        loadListData(Constant.INSTANCE.getReferencesKey(), new TypeToken<ArrayList<ReferencesModel>>() {}, list -> {
            if (referencesRecyclerView != null) {
                referencesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                referencesRecyclerView.setAdapter(new ReferencesAdapter1(context, list, "color1"));
            } else {
                Log.e(TAG, "referencesRecyclerView not found in the layout");
            }
        });
    }

    private static <T> void loadData(String key, Callback<T> callback) {
        String data = Constant.INSTANCE.getData(context, key);
        if (data != null) {
            callback.onDataLoaded((T) data);
        }
    }

    private static <T> void loadListData(String key, TypeToken<ArrayList<T>> typeToken, Callback<ArrayList<T>> callback) {
        String data = Constant.INSTANCE.getData(context, key);
        if (data != null) {
            ArrayList<T> list = new Gson().fromJson(data, typeToken.getType());
            callback.onDataLoaded(list);
        }
    }

//    private static ViewGroup inflateAndAddView(String xml, ViewGroup frame) {
//        try {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(new StringReader(xml));
//            int eventType = parser.getEventType();
//
//            ViewGroup tempView = null;
//            ViewGroup currentParent = null;
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        String tagName = parser.getName();
//                        View view = createViewFromTag(tagName, parser);
//
//                        if (view != null) {
//                            if (tempView == null) {
//                                tempView = (ViewGroup) view;
//                                currentParent = tempView;
//                            } else {
//                                currentParent.addView(view);
//                            }
//
//                            if (view instanceof ViewGroup) {
//                                currentParent = (ViewGroup) view;
//                            }
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        if (currentParent != tempView && currentParent.getParent() instanceof ViewGroup) {
//                            currentParent = (ViewGroup) currentParent.getParent();
//                        }
//                        break;
//                }
//                eventType = parser.next();
//            }
//
//            logViewHierarchy(tempView, 0);
//
//            return tempView;
//        } catch (XmlPullParserException | IOException e) {
//            Log.e(TAG, "Error parsing XML layout", e);
//        }
//
//        return null;
//    }

    private static void inflateView(FrameLayout frame, String combinedXml, OnResumeGenerationListener listener, OnXmlGeneratedListener xmlListener) {
        Log.d(TAG, "inflateView() called with XML: " + combinedXml);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        final ViewGroup[] rootLayout = {null};

        executor.execute(() -> {
            try {
                // Parse the generated XML using XmlPullParser
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(new StringReader(combinedXml));
                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();
                        try {
                            View view = createViewFromTag(tagName, parser);
                            if (view != null) {
                                if (rootLayout[0] == null) {
                                    rootLayout[0] = (ViewGroup) view;
                                } else {
                                    ((ViewGroup) rootLayout[0]).addView(view);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating view from tag: " + tagName, e);
                        }
                    }
                    eventType = parser.next();
                }

                handler.post(() -> {
                    // Add the root layout to the FrameLayout container
                    frame.removeAllViews();
                    //setupUI(rootLayout[0]);
                    frame.addView(rootLayout[0]);

                    if (listener != null) {
                        listener.onResumeGenerationComplete();
                    }

                    if (xmlListener != null) {
                        String resumeXml = generateXmlWithData(combinedXml);
                        Log.d(TAG, "Resume XML: " + resumeXml);
                        xmlListener.onXmlGenerated(resumeXml);
                    }

                    logViewHierarchy(rootLayout[0], 0);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error generating resume", e);
                handler.post(() -> {
                    if (listener != null) {
                        listener.onResumeGenerationError("Failed to generate resume");
                    }
                });
            }
        });
    }

//    private static String generateXmlWithData(String combinedXml) {
//        StringWriter writer = new StringWriter();
//
//        try {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(new StringReader(combinedXml));
//
//            XmlSerializer serializer = Xml.newSerializer();
//            serializer.setOutput(writer);
//            serializer.startDocument("UTF-8", true);
//
//            int eventType = parser.getEventType();
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        String tagName = parser.getName();
//                        serializer.startTag(null, tagName);
//
//                        if (tagName.equals("TextView")) {
//                            String viewId = parser.getAttributeValue(null, "android:id");
//                            if (viewId != null) {
//                                String dataKey = getTextViewDataKey(viewId);
//                                if (dataKey != null) {
//                                    String data = Constant.INSTANCE.getData(context, dataKey);
//                                    serializer.attribute(null, "android:text", data);
//                                }
//                            }
//                        } else if (tagName.equals("androidx.recyclerview.widget.RecyclerView")) {
//                            String recyclerViewId = parser.getAttributeValue(null, "android:id");
//                            if (recyclerViewId != null) {
//                                String dataKey = getRecyclerViewDataKey(recyclerViewId);
//                                if (dataKey != null) {
//                                    String dataJson = Constant.INSTANCE.getData(context, dataKey);
//                                    serializer.attribute(null, "app:data", dataJson);
//                                }
//                            }
//                        }
//
//                        for (int i = 0; i < parser.getAttributeCount(); i++) {
//                            String attrName = parser.getAttributeName(i);
//                            String attrValue = parser.getAttributeValue(i);
//                            serializer.attribute(null, attrName, attrValue);
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        serializer.endTag(null, parser.getName());
//                        break;
//
//                    case XmlPullParser.TEXT:
//                        serializer.text(parser.getText());
//                        break;
//                }
//
//                eventType = parser.next();
//            }
//
//            serializer.endDocument();
//
//            return writer.toString();
//        } catch (Exception e) {
//            Log.e(TAG, "Error generating resume XML with data", e);
//            return null;
//        }
//    }

    private static String generateXmlWithData(String combinedXml) {
        try {
            // Initialize Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(combinedXml)));

            // Update TextView elements with data
            NodeList textViews = document.getElementsByTagName("TextView");
            for (int i = 0; i < textViews.getLength(); i++) {
                Element textView = (Element) textViews.item(i);
                String id = textView.getAttribute("android:id");
                String dataKey = getTextViewDataKey(id);
                if (dataKey != null) {
                    String data = Constant.INSTANCE.getData(context, dataKey);
                    textView.setAttribute("android:text", data);
                }
            }

//            // Update ImageView for the profile picture
//            NodeList imageViews = document.getElementsByTagName("ImageView");
//            for (int i = 0; i < imageViews.getLength(); i++) {
//                Element imageView = (Element) imageViews.item(i);
//                String id = imageView.getAttribute("android:id");
//                if ("@+id/profileImageView".equals(id)) {
//                    String imageData = Constant.INSTANCE.getData(context, Constant.INSTANCE.getProfileImageKey());
//                    if (imageData != null && !imageData.isEmpty()) {
//                        // Assuming imageData is in the correct format: "data:image/jpg;base64,..."
//                        imageView.setAttribute("android:src", imageData);
//                    }
//                }
//            }
            NodeList imageViews = document.getElementsByTagName("ImageView");
            for (int i = 0; i < imageViews.getLength(); i++) {
                Node node = imageViews.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) node;

                    // Check if this ImageView is your profile image view
                    String id = elem.getAttribute("android:id");
                    if ("@+id/profileImageView".equals(id)) {
                        // Retrieve the base64 image string
                        String imageData = Constant.INSTANCE.getData(context, Constant.INSTANCE.getProfileImageKey());

                        if (imageData != null && !imageData.isEmpty()) {
                            // Ensure it starts with the correct prefix
                            if (!imageData.startsWith("data:image/jpg;base64")) {
                                imageData = "data:image/jpg;base64," + imageData;
                            }

                            // Update the 'android:src' attribute
                            elem.setAttribute("android:src", imageData);
                        }
                    }
                }
            }

            // Update RecyclerView elements with JSON data in 'app:data' attribute
            NodeList recyclerViews = document.getElementsByTagName("androidx.recyclerview.widget.RecyclerView");
            for (int i = 0; i < recyclerViews.getLength(); i++) {
                Element recyclerView = (Element) recyclerViews.item(i);
                String id = recyclerView.getAttribute("android:id");
                String dataKey = getRecyclerViewDataKey(id);
                if (dataKey != null) {
                    String dataJson = Constant.INSTANCE.getData(context, dataKey);
                    recyclerView.setAttribute("app:data", dataJson);
                }
            }

            // Convert the updated Document back to a string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            return writer.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error generating XML with data: " + e.getMessage(), e);
            return null;
        }
    }

    private static String getTextViewDataKey(String viewId) {
        if (viewId.equals("@+id/nameTextView")) {
            return Constant.INSTANCE.getNameKey();
        } else if (viewId.equals("@+id/professionTextView")) {
            return Constant.INSTANCE.getFieldKey();
        } else if (viewId.equals("@+id/emailTextView")) {
            return Constant.INSTANCE.getEmailKey();
        } else if (viewId.equals("@+id/phoneTextView")) {
            return Constant.INSTANCE.getNumberKey();
        } else if (viewId.equals("@+id/locationTextView")) {
            return Constant.INSTANCE.getAddressKey();
        } else if (viewId.equals("@+id/summaryTextView")) {
            return Constant.INSTANCE.getAboutMeKey();
        }
        return null;
    }

    // Helper method to get the data key based on RecyclerView ID
    private static String getRecyclerViewDataKey(String recyclerViewId) {
        switch (recyclerViewId) {
            case "@+id/workExperienceRecyclerView":
                return Constant.INSTANCE.getExperienceKey();
            case "@+id/educationRecyclerView":
                return Constant.INSTANCE.getAcademicKey();
            case "@+id/skillsRecyclerView":
                return Constant.INSTANCE.getSkillsKey();
            case "@+id/referencesRecyclerView":
                return Constant.INSTANCE.getReferencesKey();
            // Add more cases for other RecyclerViews as needed
            default:
                return null; // Or handle unknown IDs as needed (e.g., throw an exception)
        }
    }

//    private static void inflateAndAddView(String xml, ViewGroup frame, OnResumeGenerationListener listener) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//            try {
//                XmlPullParser parser = Xml.newPullParser();
//                parser.setInput(new StringReader(xml));
//                int eventType = parser.getEventType();
//
//                ViewGroup tempView = null;
//                ViewGroup currentParent = null;
//
//                while (eventType != XmlPullParser.END_DOCUMENT) {
//                    switch (eventType) {
//                        case XmlPullParser.START_TAG:
//                            String tagName = parser.getName();
//                            View view = createViewFromTag(tagName, parser);
//
//                            if (view != null) {
//                                if (tempView == null) {
//                                    tempView = (ViewGroup) view;
//                                    currentParent = tempView;
//                                } else {
//                                    currentParent.addView(view);
//                                }
//
//                                if (view instanceof ViewGroup) {
//                                    currentParent = (ViewGroup) view;
//                                }
//                            }
//                            break;
//
//                        case XmlPullParser.END_TAG:
//                            if (currentParent != tempView && currentParent.getParent() instanceof ViewGroup) {
//                                currentParent = (ViewGroup) currentParent.getParent();
//                            }
//                            break;
//                    }
//                    eventType = parser.next();
//                }
//
//                ViewGroup finalTempView = tempView;
//                handler.post(() -> {
//                    if (finalTempView != null) {
//                        logViewHierarchy(finalTempView, 0);
//
//                        // Add the inflated view to the frame
//                        frame.removeAllViews();
//                        frame.addView(finalTempView);
//
//                        frame.removeAllViews();
//                        listener.onResumeGenerationComplete();
//                        setupUI(finalTempView);
//                    } else {
//                        Log.e(TAG, "Failed to inflate view from XML");
//                    }
//                });
//            } catch (XmlPullParserException | IOException e) {
//                Log.e(TAG, "Error parsing XML layout", e);
//                handler.post(() -> {
//                    // Handle the error on the main thread
//                    // You can show an error message or take appropriate action
//                });
//            }
//        });
//    }

    private static String stripNonXmlContent(String input) {
        // Remove any content before the first XML tag
        int firstTagIndex = input.indexOf("<");
        if (firstTagIndex != -1) {
            input = input.substring(firstTagIndex);
        }

        // Remove any content after the last XML tag
        int lastTagIndex = input.lastIndexOf(">");
        if (lastTagIndex != -1) {
            input = input.substring(0, lastTagIndex + 1);
        }

        // Remove any remaining non-XML content using regular expressions
        String strippedXml = input.replaceAll("(?s)(?<=>)\\s*\\n*(?!<).*?(?=<)", "");

        // Remove the XML declaration from the stripped XML
        strippedXml = strippedXml.replaceAll("<\\?xml.*?\\?>", "").trim();

        return strippedXml;
    }

    private static String generateXml(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);

        int maxTokens = 4096; // Adjust the value based on your requirements

        AnthropicAPI anthropicAPI = new AnthropicAPI(apiKey, apiVersion, model);
        String generatedXml = anthropicAPI.generateText(messages, maxTokens);

        return generatedXml;
    }

    private static void logViewHierarchy(View view, int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("--"); // Indentation for hierarchy level
        }

        sb.append(view.getClass().getSimpleName());
//        if (view.getId() != View.NO_ID) {
//            try {
//                String idName = view.getResources().getResourceEntryName(view.getId());
//                sb.append(" (ID: ").append(idName).append(")");
//            } catch (Resources.NotFoundException e) {
//                // Handle views with invalid IDs
//                sb.append(" (Invalid ID: ").append(view.getId()).append(")");
//            }
//        }

        Log.d(TAG, sb.toString());

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                logViewHierarchy(vg.getChildAt(i), depth + 1); // Recurse for children
            }
        }
    }

    private static View createViewFromTag(String tagName, XmlPullParser parser) {
        View view = null;
        if (tagName != null) {
            Log.e(TAG, tagName);
            switch (tagName) {
                case "LinearLayout":
                    view = new LinearLayout(context);
                    setViewAttributes(view, parser);
                    setLinearLayoutAttributes((LinearLayout) view, parser);
                    break;
                case "TextView":
                    view = new TextView(context);
                    setViewAttributes(view, parser);
                    setTextViewAttributes((TextView) view, parser);
                    break;
                case "ImageView":
                    view = new ImageView(context);
                    setViewAttributes(view, parser);
                    setImageViewAttributes((ImageView) view, parser);
                    break;
                case "androidx.recyclerview.widget.RecyclerView":
                    view = new RecyclerView(context);
                    setViewAttributes(view, parser);
                    setRecyclerViewAttributes((RecyclerView) view, parser);
                    break;
//                case "View":
//                    view = new View(context);
//                    setViewAttributes(view, parser);
//                    break;
                //Add more cases for other view types
                default:
                    // Handle unknown or unsupported tags
                    Log.w(TAG, "Unknown or unsupported tag: " + tagName);
                    try {
                        skipSubTree(parser);
                    } catch (Exception e) {
                        Log.e(TAG, "Error skipping subtree for unknown tag: " + e.getMessage());
                    }
                    break;
            }
        } else {
            Log.e(TAG, "Tag name is null");
        }
        return view;
    }

//    private static View createViewFromTag(String tagName, XmlPullParser parser) {
//        View view = null;
//        if (tagName != null) {
//            switch (tagName) {
//                case "LinearLayout":
//                    view = new LinearLayout(context);
//                    setViewAttributes(view, parser);
//                    setLinearLayoutAttributes((LinearLayout) view, parser);
//                    break;
//                case "TextView":
//                    view = new TextView(context);
//                    setViewAttributes(view, parser);
//                    setTextViewAttributes((TextView) view, parser);
//                    break;
//                case "ImageView":
//                    view = new ImageView(context);
//                    setViewAttributes(view, parser);
//                    setImageViewAttributes((ImageView) view, parser);
//                    break;
//                case "androidx.recyclerview.widget.RecyclerView":
//                    view = new RecyclerView(context);
//                    setViewAttributes(view, parser);
//                    setRecyclerViewAttributes((RecyclerView) view, parser);
//                    break;
//                // Add more cases for other view types
//                default:
//                    // Handle unknown or unsupported tags
//                    Log.w(TAG, "Unknown or unsupported tag: " + tagName);
//                    try {
//                        skipSubTree(parser);
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error skipping subtree for unknown tag: " + e.getMessage());
//                    }
//                    break;
//            }
//        } else {
//            Log.e(TAG, "Tag name is null");
//        }
//        return view;
//    }

    private static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private static void setViewAttributes(View view, XmlPullParser parser) {
        if (parser != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            String namespace = "http://schemas.android.com/apk/res/android";
            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attrName = parser.getAttributeName(i);
                String attrValue = parser.getAttributeValue(namespace, attrName);
                switch (attrName) {
                    case "layout_width":
                        //layoutParams.width = getDimension(attrValue);
                        break;
                    case "layout_height":
                        //layoutParams.height = getDimension(attrValue);
                        break;
                    case "background":
                        view.setBackgroundResource(getResId(attrValue, "drawable"));
                        break;
                    case "padding":
                        //int padding = getDimension(attrValue);
                        //view.setPadding(padding, padding, padding, padding);
                        break;
                    case "id":
                        int id = getId(attrValue);
                        if (id != 0) { // Only set the ID if it's valid
                            view.setId(id);
                        }
                        break;
                    // Handle more relevant view attributes for a resume layout
                }
            }

            //view.setLayoutParams(layoutParams);
        } else {
            Log.e(TAG, "Parser is null");
        }
    }


//    private static void setViewAttributes(View view, XmlPullParser parser) {
//        if (parser != null) {
//            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            for (int i = 0; i < parser.getAttributeCount(); i++) {
//                String attrName = parser.getAttributeName(i);
//                String attrValue = parser.getAttributeValue(i);
//                switch (attrName) {
//                    case "android:id":
//                        //view.setId(getResId(attrValue, "id"));
//                        int id = getId(attrValue);
//                        if (id != 0) { // Only set the ID if it's valid
//                            view.setId(id);
//                        }
//                        break;
//                    case "android:layout_width":
//                        layoutParams.width = getDimension(attrValue);
//                        break;
//                    case "android:layout_height":
//                        layoutParams.height = getDimension(attrValue);
//                        break;
//                    case "android:background":
//                        view.setBackgroundResource(getResId(attrValue, "drawable"));
//                        break;
//                    case "android:padding":
//                        int padding = getDimension(attrValue);
//                        view.setPadding(padding, padding, padding, padding);
//                        break;
//                    // Handle more relevant view attributes for a resume layout
//                }
//            }
//
//            view.setLayoutParams(layoutParams);
//        } else {
//            Log.e(TAG, "Parser is null");
//        }
//    }

    private static int getId(String idString) {
        if (idString.startsWith("@+id/") || idString.startsWith("@id/")) {
            String idName = idString.substring(idString.indexOf("/") + 1);

            // Check if the ID already exists in the map
            if (!viewIdMap.containsKey(idName)) {
                // If it doesn't exist, generate a new ID and store it
                int newId = View.generateViewId();
                viewIdMap.put(idName, newId);
            }

            // Return the ID from the map
            return viewIdMap.get(idName);
        }
        return View.NO_ID; // Use View.NO_ID for invalid IDs
    }

//    private static void setTextViewAttributes(TextView textView, XmlPullParser parser) {
//        String namespace = "http://schemas.android.com/apk/res/android";
//        for (int i = 0; i < parser.getAttributeCount(); i++) {
//            String attrName = parser.getAttributeName(i);
//            String attrValue = parser.getAttributeValue(namespace, attrName);
//            switch (attrName) {
//                case "text":
//                    textView.setText(attrValue);
//                    break;
//                case "textSize":
//                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(attrValue));
//                    break;
//                case "textColor":
//                    textView.setTextColor(getColor(attrValue));
//                    break;
//                // Handle more relevant TextView attributes for a resume layout
//            }
//        }
//    }

    private static void setTextViewAttributes(TextView textView, XmlPullParser parser) {
        String namespace = "http://schemas.android.com/apk/res/android";
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(namespace, attrName);
            switch (attrName) {
                case "text":
                    textView.setText(attrValue);
                    break;
                case "textSize":
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(attrValue));
                    break;
                case "textColor":
                    textView.setTextColor(getColor(attrValue));
                    break;
                // Handle more relevant TextView attributes for a resume layout
            }
        }
        String id = parser.getAttributeValue(namespace, "id");
        if (id != null) {
            int textViewId = getId(id);

            // Set the text based on the TextView ID
            if (textViewId != View.NO_ID) {
                if (textViewId == getId("@+id/nameTextView")) {
                    String name = Constant.INSTANCE.getData(context, Constant.INSTANCE.getNameKey());
                    textView.setText(name);
                    Log.w(TAG, "TextView ID nameTextView");
                } else if (textViewId == getId("@+id/professionTextView")) {
                    String profession = Constant.INSTANCE.getData(context, Constant.INSTANCE.getFieldKey());
                    textView.setText(profession);
                    Log.w(TAG, "TextView ID professionTextView");
                } else if (textViewId == getId("@+id/emailTextView")) {
                    String email = Constant.INSTANCE.getData(context, Constant.INSTANCE.getEmailKey());
                    textView.setText(email);
                    Log.w(TAG, "TextView ID emailTextView");
                } else if (textViewId == getId("@+id/phoneTextView")) {
                    String phone = Constant.INSTANCE.getData(context, Constant.INSTANCE.getNumberKey());
                    textView.setText(phone);
                    Log.w(TAG, "TextView ID phoneTextView");
                } else if (textViewId == getId("@+id/locationTextView")) {
                    String location = Constant.INSTANCE.getData(context, Constant.INSTANCE.getAddressKey());
                    textView.setText(location);
                    Log.w(TAG, "TextView ID locationTextView");
                } else if (textViewId == getId("@+id/summaryTextView")) {
                    String summary = Constant.INSTANCE.getData(context, Constant.INSTANCE.getAboutMeKey());
                    textView.setText(summary);
                    Log.w(TAG, "TextView ID summaryTextView");
                }
            } else {
                Log.w(TAG, "TextView ID NO ID");
            }
        } else {
            Log.w(TAG, "TextView ID is null");
            Log.w(TAG, parser.toString());
        }
    }

    private static void setImageViewAttributes(ImageView imageView, XmlPullParser parser) {
        String namespace = "http://schemas.android.com/apk/res/android";
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(namespace, attrName);
            switch (attrName) {
                case "src":
                    imageView.setImageResource(getResId(attrValue, "drawable"));
                    break;
                case "scaleType":
                    imageView.setScaleType(getScaleType(attrValue));
                    break;
                // Handle more relevant ImageView attributes for a resume layout
            }
        }
    }

    private static void setLinearLayoutAttributes(LinearLayout linearLayout, XmlPullParser parser) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attrName = parser.getAttributeName(i);
            String attrValue = parser.getAttributeValue(i);
            switch (attrName) {
                case "android:orientation":
                    linearLayout.setOrientation(getOrientation(attrValue));
                    break;
                // Handle more relevant LinearLayout attributes for a resume layout
            }
        }
    }

    private static void setRecyclerViewAttributes(RecyclerView recyclerView, XmlPullParser parser) {
        if (parser != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            String namespace = "http://schemas.android.com/apk/res/android";
            ///String recyclerViewId = getId(parser.getAttributeValue(namespace, "id"));

            int recyclerViewId = getId(parser.getAttributeValue(namespace, "id"));

            if (recyclerViewId == getId("@+id/workExperienceRecyclerView")) {
                ArrayList<ExperienceModel> experienceList = getExperienceList();
                ExperienceAdapter1 experienceAdapter = new ExperienceAdapter1(context, experienceList, "color1");
                recyclerView.setAdapter(experienceAdapter);
                Log.w(TAG, "RecyclerView ID workExperienceRecyclerView");
            } else if (recyclerViewId == getId("@+id/educationRecyclerView")) {
                ArrayList<AcademicModel> educationList = getEducationList();
                EducationAdapter1 educationAdapter = new EducationAdapter1(context, educationList);
                recyclerView.setAdapter(educationAdapter);
                Log.w(TAG, "RecyclerView ID educationRecyclerView");
            } else if (recyclerViewId == getId("@+id/skillsRecyclerView")) {
                ArrayList<String> skillsList = getSkillsList();
                TextAdapter1 skillsAdapter = new TextAdapter1(context, skillsList);
                recyclerView.setAdapter(skillsAdapter);
                Log.w(TAG, "RecyclerView ID skillsRecyclerView");
            } else if (recyclerViewId == getId("@+id/referencesRecyclerView")) {
                // Set up the adapter for references RecyclerView
                ArrayList<ReferencesModel> referencesList = getReferencesList();
                ReferencesAdapter1 referencesAdapter = new ReferencesAdapter1(context, referencesList, "color1");
                recyclerView.setAdapter(referencesAdapter);
                Log.w(TAG, "RecyclerView ID referencesRecyclerView");
            }

            //if (recyclerViewId != null) {
//                switch (recyclerViewId) {
//                    case "@+id/workExperienceRecyclerView":
//                        // Set up the adapter for work experience RecyclerView
//                        ArrayList<ExperienceModel> experienceList = getExperienceList();
//                        ExperienceAdapter1 experienceAdapter = new ExperienceAdapter1(context, experienceList, "color1");
//                        recyclerView.setAdapter(experienceAdapter);
//                        break;
//                    case "@+id/educationRecyclerView":
//                        // Set up the adapter for education RecyclerView
//                        ArrayList<AcademicModel> educationList = getEducationList();
//                        EducationAdapter1 educationAdapter = new EducationAdapter1(context, educationList);
//                        recyclerView.setAdapter(educationAdapter);
//                        break;
//                    case "@+id/skillsRecyclerView":
//                        // Set up the adapter for skills RecyclerView
//                        ArrayList<String> skillsList = getSkillsList();
//                        TextAdapter1 skillsAdapter = new TextAdapter1(context, skillsList);
//                        recyclerView.setAdapter(skillsAdapter);
//                        break;
//                    case "@+id/referencesRecyclerView":
//                        // Set up the adapter for references RecyclerView
//                        ArrayList<ReferencesModel> referencesList = getReferencesList();
//                        ReferencesAdapter1 referencesAdapter = new ReferencesAdapter1(context, referencesList, "color1");
//                        recyclerView.setAdapter(referencesAdapter);
//                        break;
//                }
           // } else {
                //Log.w(TAG, "RecyclerView ID attribute is null");
           // }
        } else {
            Log.e(TAG, "Parser is null");
        }
    }

    private static ArrayList<ExperienceModel> getExperienceList() {
        // Retrieve and return the list of work experiences from SharedPreferences or any other data source
        // Example:
        String experienceJson = Constant.INSTANCE.getData(context, Constant.INSTANCE.getExperienceKey());
        Type type = new TypeToken<ArrayList<ExperienceModel>>() {}.getType();
        return new Gson().fromJson(experienceJson, type);
    }

    private static ArrayList<AcademicModel> getEducationList() {
        // Retrieve and return the list of education items from SharedPreferences or any other data source
        // Example:
        String educationJson = Constant.INSTANCE.getData(context, Constant.INSTANCE.getAcademicKey());
        Type type = new TypeToken<ArrayList<AcademicModel>>() {}.getType();
        return new Gson().fromJson(educationJson, type);
    }

    private static ArrayList<String> getSkillsList() {
        // Retrieve and return the list of skills from SharedPreferences or any other data source
        // Example:
        String skillsJson = Constant.INSTANCE.getData(context, Constant.INSTANCE.getSkillsKey());
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(skillsJson, type);
    }

    private static ArrayList<ReferencesModel> getReferencesList() {
        // Retrieve and return the list of references from SharedPreferences or any other data source
        // Example:
        String referencesJson = Constant.INSTANCE.getData(context, Constant.INSTANCE.getReferencesKey());
        Type type = new TypeToken<ArrayList<ReferencesModel>>() {}.getType();
        return new Gson().fromJson(referencesJson, type);
    }

    private static void inflateChildren(XmlPullParser parser, ViewGroup parent) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth != 0) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                depth++;
                String tagName = parser.getName();
                // Recursively inflate child views and add them to the parent ViewGroup
                View childView = inflateView(tagName, parser);
                if (childView != null) {
                    parent.addView(childView);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                depth--;
            }
        }
    }

    private static View inflateView(String tagName, XmlPullParser parser) {
        switch (tagName) {
            case "TextView":
                TextView textView = new TextView(context);
                setViewAttributes(textView, parser);
                setTextViewAttributes(textView, parser);
                return textView;
            case "ImageView":
                ImageView imageView = new ImageView(context);
                setViewAttributes(imageView, parser);
                setImageViewAttributes(imageView, parser);
                return imageView;
            // Add more cases for other view types as needed
            default:
                return null;
        }
    }

    private static int getResId(String resName, String defType) {
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }

    private static int getDimension(String value) {
        if (value.endsWith("dp")) {
            float dp = Float.parseFloat(value.substring(0, value.length() - 2));
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        } else if (value.endsWith("px")) {
            return Integer.parseInt(value.substring(0, value.length() - 2));
        }
        return 0;
    }

    private static int getColor(String value) {
        if (value.startsWith("#")) {
            return Color.parseColor(value);
        } else {
            return Color.parseColor("#FFFFFF");//context.getResources().getColor(getResId(value, "color"));
        }
    }

    private static ImageView.ScaleType getScaleType(String value) {
        switch (value) {
            case "center":
                return ImageView.ScaleType.CENTER;
            case "centerCrop":
                return ImageView.ScaleType.CENTER_CROP;
            case "centerInside":
                return ImageView.ScaleType.CENTER_INSIDE;
            case "fitCenter":
                return ImageView.ScaleType.FIT_CENTER;
            case "fitEnd":
                return ImageView.ScaleType.FIT_END;
            case "fitStart":
                return ImageView.ScaleType.FIT_START;
            case "fitXY":
                return ImageView.ScaleType.FIT_XY;
            case "matrix":
                return ImageView.ScaleType.MATRIX;
            default:
                return ImageView.ScaleType.FIT_CENTER;
        }
    }

    private static int getOrientation(String value) {
        switch (value) {
            case "horizontal":
                return LinearLayout.HORIZONTAL;
            case "vertical":
                return LinearLayout.VERTICAL;
            default:
                return LinearLayout.VERTICAL;
        }
    }

//    public static class ResumeFragment extends Fragment {
//        private View rootView;
//        private String prompt;
//
//        public ResumeFragment() {
//            // Required empty public constructor
//        }
//
//        public static ResumeFragment newInstance(String prompt) {
//            ResumeFragment fragment = new ResumeFragment();
//            Bundle args = new Bundle();
//            args.putString("prompt", prompt);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public void onCreate(@Nullable Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            if (getArguments() != null) {
//                prompt = getArguments().getString("prompt");
//            }
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            Log.d(TAG, "ResumeFragment onCreateView() called");
//            rootView = inflater.inflate(R.layout.fragment_resume_popup, container, false);
//
//            ResumeBuilder.generateResumeView((FrameLayout) rootView, prompt, listener);
//
//            return rootView;
//        }
//    }

    public static class XmlUtils {
        public static int parseLayout(Context context, String xml) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(xml));

                return inflateLayout(context, parser);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static int inflateLayout(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {
            int layoutId = 0;
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("layout")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            if (attributeName.equals("id")) {
                                String layoutIdStr = parser.getAttributeValue(i);
                                layoutId = context.getResources().getIdentifier(layoutIdStr, "layout", context.getPackageName());
                                break;
                            }
                        }
                    }
                }
                eventType = parser.next();
            }

            return layoutId;
        }
    }

    interface Callback<T> {
        void onDataLoaded(T data);
    }

    public interface OnResumeGenerationListener {
        void onResumeGenerationComplete();
        void onResumeGenerationError(String errorMessage);
    }

    public interface OnResumePopupDismissListener {
        void onResumePopupDismiss();
    }
}