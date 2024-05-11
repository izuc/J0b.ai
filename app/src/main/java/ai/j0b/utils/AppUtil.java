package ai.j0b.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import ai.j0b.R;

public class AppUtil {

    public static void shareApp(Context context) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage += "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moreApps(Context context, String developerID) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + developerID)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to open developer apps", Toast.LENGTH_SHORT).show();
        }
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void privacyPolicy(Context context, String url) {
        String formattedUrl = url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
        }
    }
}