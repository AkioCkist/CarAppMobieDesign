package com.midterm.mobiledesignfinalterm.aboutUs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.midterm.mobiledesignfinalterm.R;

public class AboutUs extends AppCompatActivity {

    private ImageView imageViewBack;
    private CardView logoCard;
    private TextView brandName, tagline1, tagline2;
    private CardView missionCard, visionCard, statsCard, contactCard;
    private LinearLayout heroSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Initialize views
        initializeViews();

        // Set up click listeners
        setupClickListeners();

        // Apply animations
        applyAnimations();
    }

    private void initializeViews() {
        imageViewBack = findViewById(R.id.imageViewBack);
        logoCard = findViewById(R.id.logoCard);
        brandName = findViewById(R.id.brandName);
        tagline1 = findViewById(R.id.tagline1);
        tagline2 = findViewById(R.id.tagline2);
        heroSection = findViewById(R.id.heroSection);
        missionCard = findViewById(R.id.missionCard);
        visionCard = findViewById(R.id.visionCard);
        statsCard = findViewById(R.id.statsCard);
        contactCard = findViewById(R.id.contactCard);
    }

    private void setupClickListeners() {
        // Back button click listener
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up click listeners for contact information
        setupContactClickListeners();
    }

    private void applyAnimations() {
        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        // Apply animations with delays
        fadeIn.setDuration(1000);
        slideUp.setDuration(800);
        slideIn.setDuration(800);
        pulse.setDuration(1000);

        // Hero section animations
        heroSection.startAnimation(fadeIn);
        logoCard.startAnimation(pulse);

        // Content card animations with delays
        missionCard.setAlpha(0f);
        visionCard.setAlpha(0f);
        statsCard.setAlpha(0f);
        contactCard.setAlpha(0f);

        missionCard.postDelayed(() -> {
            missionCard.setAlpha(1f);
            missionCard.startAnimation(slideIn);
        }, 200);

        visionCard.postDelayed(() -> {
            visionCard.setAlpha(1f);
            visionCard.startAnimation(slideIn);
        }, 400);

        statsCard.postDelayed(() -> {
            statsCard.setAlpha(1f);
            statsCard.startAnimation(slideIn);
        }, 600);

        contactCard.postDelayed(() -> {
            contactCard.setAlpha(1f);
            contactCard.startAnimation(slideIn);
        }, 800);

        // Text animations
        brandName.startAnimation(slideUp);

        tagline1.postDelayed(() -> {
            tagline1.startAnimation(fadeIn);
        }, 400);

        tagline2.postDelayed(() -> {
            tagline2.startAnimation(fadeIn);
        }, 800);
    }

    private void setupContactClickListeners() {
        // Phone number click listener
        View phoneSection = findViewById(R.id.phoneSection);
        if (phoneSection != null) {
            phoneSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePhoneCall("+840236373839");
                }
            });
        }

        // Email click listener
        View emailSection = findViewById(R.id.emailSection);
        if (emailSection != null) {
            emailSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail("contact@whalexe.com");
                }
            });
        }

        // Address click listener
        View addressSection = findViewById(R.id.addressSection);
        if (addressSection != null) {
            addressSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMap("156A Le Loi, Hai Chau, Da Nang, Vietnam");
                }
            });
        }
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail(String emailAddress) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + emailAddress));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about Whale Xe");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Whale Xe team,\n\nI would like to inquire about...");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap(String address) {
        try {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // If Google Maps is not installed, try with any map app
                mapIntent.setPackage(null);
                startActivity(mapIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "No map app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Custom animation
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    // Method to navigate to this activity from other activities
    public static void startActivity(android.content.Context context) {
        Intent intent = new Intent(context, AboutUs.class);
        context.startActivity(intent);
        // Custom animation if called from another activity
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).overridePendingTransition(
                    R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}

