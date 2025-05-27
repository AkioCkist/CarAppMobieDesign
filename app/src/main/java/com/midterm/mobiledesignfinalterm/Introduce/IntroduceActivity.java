package com.midterm.mobiledesignfinalterm.Introduce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.mobiledesignfinalterm.MainActivity;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.authentication.Login;

public class IntroduceActivity extends AppCompatActivity {

    // UI Components
    private ImageView introduceImage;
    private TextView tvTitle, tvSkip;
    private Button btnBack, btnNext, btnSignIn, btnSignUp;

    // Data
    private int currentPosition = 0;
    private final int totalPages = 3;

    // Image resources
    private final int[] imageResources = {
            R.drawable.intro_bg_1, // Thay bằng ảnh thật của bạn
            R.drawable.intro_bg_2, // Thay bằng ảnh thật của bạn
            R.drawable.intro_bg_3  // Thay bằng ảnh thật của bạn
    };

    // Title resources
    private final String[] titleResources = {
            "Find cars\nanywhere",
            "Sell yours and\nget cash",
            "All types of car\ncollections"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_introduce);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        initViews();
        setupButtons();
        updateUI(0);
    }

    private void initViews() {
        introduceImage = findViewById(R.id.introduce_image);
        tvTitle = findViewById(R.id.tv_title);
        tvSkip = findViewById(R.id.tv_skip);
        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }

    private void setupButtons() {
        btnNext.setOnClickListener(v -> {
            if (currentPosition < totalPages - 1) {
                goToNextPage();
            }
        });

        btnBack.setOnClickListener(v -> {
            if (currentPosition > 0) {
                goToPreviousPage();
            }
        });

        btnSignIn.setOnClickListener(v -> {
            // Navigate to MainActivity
            Intent intent = new Intent(IntroduceActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng IntroduceActivity để không quay lại được
        });

        btnSignUp.setOnClickListener(v -> {
            // Navigate to Login Activity (có thể thay bằng Register Activity nếu có)
            Intent intent = new Intent(IntroduceActivity.this, Login.class);
            startActivity(intent);
            finish(); // Đóng IntroduceActivity để không quay lại được
        });

        tvSkip.setOnClickListener(v -> {
            // Skip to MainActivity
            Intent intent = new Intent(IntroduceActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng IntroduceActivity để không quay lại được
        });
    }

    private void goToNextPage() {
        currentPosition++;
        animateToPosition(true);
    }

    private void goToPreviousPage() {
        currentPosition--;
        animateToPosition(false);
    }

    private void animateToPosition(boolean isNext) {
        // Animation cho image và title
        Animation slideOut = isNext ?
                AnimationUtils.loadAnimation(this, R.anim.slide_out_left) :
                AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        Animation slideIn = isNext ?
                AnimationUtils.loadAnimation(this, R.anim.slide_in_right) :
                AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cập nhật nội dung
                introduceImage.setImageResource(imageResources[currentPosition]);
                tvTitle.setText(titleResources[currentPosition]);

                // Animation slide in
                introduceImage.startAnimation(slideIn);
                tvTitle.startAnimation(slideIn);

                // Cập nhật UI
                updateUI(currentPosition);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        introduceImage.startAnimation(slideOut);
        tvTitle.startAnimation(slideOut);
    }

    private void updateUI(int position) {
        if (position == 0) {
            // First page - chỉ hiện Next
            btnBack.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);

        } else if (position == totalPages - 1) {
            // Last page - hiện Sign In và Sign Up
            btnBack.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);

        } else {
            // Middle pages - hiện Back và Next
            btnBack.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        }
    }
}