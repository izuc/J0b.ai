package ai.j0b.animation;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DepthTransformation implements ViewPager.PageTransformer {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void transformPage(View page, float position) {
        if (position < -1.0f) {
            page.setAlpha(0.0f);
        } else if (position <= 0.0f) {
            page.setAlpha(1.0f);
            page.setTranslationX(0.0f);
            page.setScaleX(1.0f);
            page.setScaleY(1.0f);
        } else if (position <= 1.0f) {
            float scaleFactor = 1.0f - Math.abs(position);
            page.setTranslationX(-position * page.getWidth());
            page.setAlpha(1.0f - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
        } else {
            page.setAlpha(0.0f);
        }
    }

    public static Future<Integer> parse(final String input) {
        return executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    Thread.sleep(1000L);
                    return Integer.parseInt(input);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}