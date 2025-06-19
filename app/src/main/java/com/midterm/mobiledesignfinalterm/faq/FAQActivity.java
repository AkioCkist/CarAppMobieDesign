package com.midterm.mobiledesignfinalterm.faq;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.midterm.mobiledesignfinalterm.R;

public class FAQActivity extends AppCompatActivity {

    // FAQ Views Arrays for easier management
    private CardView[] faqCards = new CardView[14];
    private TextView[] faqAnswers = new TextView[14];
    private ImageView[] faqArrows = new ImageView[14];
    
    private ImageView backArrow;
    private Button btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Back arrow
        backArrow = findViewById(R.id.back_arrow);
        
        // Contact support button
        btnContactSupport = findViewById(R.id.btn_contact_support);

        // Initialize FAQ cards, answers, and arrows
        for (int i = 0; i < 14; i++) {
            int cardId = getResources().getIdentifier("faq_card_" + (i + 1), "id", getPackageName());
            int answerId = getResources().getIdentifier("faq_answer_" + (i + 1), "id", getPackageName());
            int arrowId = getResources().getIdentifier("faq_arrow_" + (i + 1), "id", getPackageName());
            
            faqCards[i] = findViewById(cardId);
            faqAnswers[i] = findViewById(answerId);
            faqArrows[i] = findViewById(arrowId);
        }
    }

    private void setupClickListeners() {
        // Back arrow click listener
        backArrow.setOnClickListener(v -> onBackPressed());

        // FAQ cards click listeners
        for (int i = 0; i < 14; i++) {
            final int index = i;
            if (faqCards[i] != null) {
                faqCards[i].setOnClickListener(v -> toggleFaqAnswer(faqAnswers[index], faqArrows[index]));
            }
        }

        // Contact support button click listener
        btnContactSupport.setOnClickListener(v -> {
            // Create intent to contact support (phone call)
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+840236373899"));
            
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Phone app not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFaqAnswer(TextView answer, ImageView arrow) {
        if (answer == null || arrow == null) return;
        
        if (answer.getVisibility() == View.GONE) {
            // Show answer
            answer.setVisibility(View.VISIBLE);
            // Rotate arrow to point up
            ObjectAnimator.ofFloat(arrow, "rotation", 0f, 180f).setDuration(200).start();
        } else {
            // Hide answer
            answer.setVisibility(View.GONE);
            // Rotate arrow to point down
            ObjectAnimator.ofFloat(arrow, "rotation", 180f, 0f).setDuration(200).start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Add transition animation
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
