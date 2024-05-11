package ai.j0b.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import ai.j0b.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Looper looper = Looper.myLooper();
        if (looper != null) {
            new Handler(looper).postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishAffinity();
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                }
            }, 1000L);
        }
    }
}