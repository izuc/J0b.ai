package ai.j0b.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ai.j0b.R;
import ai.j0b.animation.DepthTransformation;
import ai.j0b.databinding.ActivityIntroBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class IntroActivity extends AppCompatActivity {

    public static final Integer[] layouts = {R.layout.intro1, R.layout.intro2, R.layout.intro3};
    private ActivityIntroBinding binding;
    private MyViewPagerAdapter myViewPagerAdapter;
    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myViewPagerAdapter = new MyViewPagerAdapter(this);
        binding.viewPager.setAdapter(myViewPagerAdapter);
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        binding.viewPager.setPageTransformer(true, new DepthTransformation());
        binding.dotsIndicator.setViewPager(binding.viewPager);

        binding.btnNext.setOnClickListener(v -> {
            int currentItem = binding.dotsIndicator.getPager() != null ? binding.dotsIndicator.getPager().getCurrentItem() : 0;
            if (currentItem == 0) {
                binding.viewPager.setCurrentItem(1);
            } else if (currentItem == 1) {
                binding.viewPager.setCurrentItem(2);
            } else {
                nextScreen();
            }
        });

        binding.tvSkip.setOnClickListener(v -> nextScreen());
    }

    private void nextScreen() {
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    public static class MyViewPagerAdapter extends PagerAdapter {

        private final Context context;
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}