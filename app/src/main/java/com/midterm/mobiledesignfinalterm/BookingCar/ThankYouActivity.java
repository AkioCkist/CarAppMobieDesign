package com.midterm.mobiledesignfinalterm.BookingCar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// Updated imports for iText 7
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.homepage.Homepage;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThankYouActivity extends AppCompatActivity {

    private TextView tvBookingId, tvPickupDetails, tvDropoffDetails, tvUserDetails, tvPaymentDetails, tvTotalAmount;
    private Button btnSaveImage, btnBackToHome;
    private static final int CREATE_FILE = 1;
    // Color for PDF text (using green_primary color)
    private DeviceRgb greenPrimaryColor;

    private String userId, userName, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        // Initialize the green primary color from resources
        int greenColorInt = ContextCompat.getColor(this, R.color.green_primary); // #18F45D
        greenPrimaryColor = new DeviceRgb(
                (greenColorInt >> 16) & 0xFF,
                (greenColorInt >> 8) & 0xFF,
                greenColorInt & 0xFF);

        // Get user info from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");
        userName = intent.getStringExtra("user_name");
        userPhone = intent.getStringExtra("user_phone");

        initViews();
        displayBookingDetails();
        setupClickListeners();
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tv_booking_id);
        tvPickupDetails = findViewById(R.id.tv_pickup_details);
        tvDropoffDetails = findViewById(R.id.tv_dropoff_details);
        tvUserDetails = findViewById(R.id.tv_user_details);
        tvPaymentDetails = findViewById(R.id.tv_payment_details);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnSaveImage = findViewById(R.id.btn_save_image);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
    }

    private void displayBookingDetails() {
        Intent intent = getIntent();

        String bookingId = intent.getStringExtra("booking_id");
        String pickupLocation = intent.getStringExtra("pickup_location");
        String dropoffLocation = intent.getStringExtra("dropoff_location");
        String pickupDate = intent.getStringExtra("pickup_date");
        String pickupTime = intent.getStringExtra("pickup_time");
        String dropoffDate = intent.getStringExtra("dropoff_date");
        String dropoffTime = intent.getStringExtra("dropoff_time");
        String fullName = intent.getStringExtra("full_name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String paymentMethod = intent.getStringExtra("payment_method");
        String totalAmount = intent.getStringExtra("total_amount");

        tvBookingId.setText("Booking ID: " + (bookingId != null ? bookingId : ""));
        tvPickupDetails.setText(String.format("Pickup: %s\n%s at %s",
                pickupLocation, pickupDate, pickupTime));
        tvDropoffDetails.setText(String.format("Drop-off: %s\n%s at %s",
                dropoffLocation, dropoffDate, dropoffTime));
        tvUserDetails.setText(String.format("Name: %s\nEmail: %s\nPhone: %s",
                fullName, email, phone));
        tvPaymentDetails.setText("Payment: " + (paymentMethod != null ? paymentMethod : ""));
        tvTotalAmount.setText(totalAmount != null ? "$" + totalAmount : "$0.00");
    }

    private void setupClickListeners() {
        btnSaveImage.setOnClickListener(v -> saveTicketAsImage());

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, Homepage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // Pass user info to Homepage to keep login
            intent.putExtra("user_id", userId);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_phone", userPhone);
            startActivity(intent);
            finish();
        });
    }

    // Helper method to convert Android color resource to iText DeviceRgb
    private DeviceRgb getColorFromResource(int colorResourceId) {
        int color = ContextCompat.getColor(this, colorResourceId);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        return new DeviceRgb(red, green, blue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        // Get colors from resources
                        DeviceRgb primaryColor = getColorFromResource(R.color.green_primary);
                        DeviceRgb accentColor = getColorFromResource(R.color.accent_green);
                        DeviceRgb secondaryTextColor = getColorFromResource(R.color.text_secondary);
                        DeviceRgb dividerColor = getColorFromResource(R.color.divider_color);
                        DeviceRgb backgroundPrimary = getColorFromResource(R.color.background_primary);
                        DeviceRgb backgroundSecondary = getColorFromResource(R.color.background_secondary);
                        DeviceRgb whiteText = getColorFromResource(R.color.white);

                        // Setup PDF document with A4 page size
                        PdfWriter writer = new PdfWriter(outputStream);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf, PageSize.A4);

                        // Draw background color on the page
                        PdfPage page = pdf.addNewPage();
                        PdfCanvas canvas = new PdfCanvas(page);
                        Rectangle pageSize = page.getPageSize();
                        canvas.setFillColor(backgroundPrimary)
                                .rectangle(pageSize.getLeft(), pageSize.getBottom(), pageSize.getWidth(), pageSize.getHeight())
                                .fill();

                        // Set margins for content
                        document.setMargins(36, 36, 36, 36);

                        // Create header section
                        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                                .setWidth(UnitValue.createPercentValue(100));

                        // Company info in left column with background
                        Cell companyCell = new Cell()
                                .add(new Paragraph("Whale Xe").setBold().setFontSize(16).setFontColor(primaryColor))
                                .add(new Paragraph("More than rentals - We deliver happiness").setFontColor(whiteText))
                                .add(new Paragraph("https://car-app-web-design.vercel.app/").setFontColor(whiteText))
                                .add(new Paragraph("contact@whalexe.com").setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(10)
                                .setBorder(null);

                        // Current date in right column - aligned right with background
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        String currentDate = dateFormatter.format(new Date());
                        Cell dateCell = new Cell()
                                .add(new Paragraph("Date: " + currentDate).setTextAlignment(TextAlignment.RIGHT).setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(10)
                                .setBorder(null);

                        headerTable.addCell(companyCell);
                        headerTable.addCell(dateCell);
                        document.add(headerTable);

                        // Add divider line using app colors
                        document.add(new Paragraph("")
                                .setHeight(1)
                                .setBorder(new SolidBorder(dividerColor, 1))
                                .setMarginBottom(15)
                        );

                        // Title with app colors
                        document.add(new Paragraph("BOOKING CONFIRMATION")
                                .setFontSize(20)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontColor(primaryColor)
                                .setMarginBottom(20));

                        // Extract booking ID from the text view
                        String bookingIdText = tvBookingId.getText().toString();
                        String bookingId = bookingIdText.replace("Booking ID: ", "").trim();

                        // Add booking ID with highlight using accent color and background
                        Table bookingIdTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(20);

                        Cell bookingIdCell = new Cell()
                                .add(new Paragraph("Booking Reference: " + bookingId)
                                        .setBold()
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setFontSize(14)
                                        .setFontColor(accentColor))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(12)
                                .setBorder(null);

                        bookingIdTable.addCell(bookingIdCell);
                        document.add(bookingIdTable);

                        // Customer Information Section
                        document.add(new Paragraph("CUSTOMER DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(primaryColor)
                                .setMarginBottom(10));

                        // Parse user details from text view
                        String userDetailsText = tvUserDetails.getText().toString();
                        String[] userLines = userDetailsText.split("\n");

                        Table userTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(15);

                        // Add user details to table with app colors and backgrounds
                        for (String line : userLines) {
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2) {
                                userTable.addCell(new Cell()
                                        .add(new Paragraph(parts[0].trim() + ":").setBold().setFontColor(whiteText))
                                        .setBackgroundColor(backgroundSecondary)
                                        .setPadding(8));
                                userTable.addCell(new Cell()
                                        .add(new Paragraph(parts[1].trim()).setFontColor(whiteText))
                                        .setBackgroundColor(backgroundSecondary)
                                        .setPadding(8));
                            }
                        }
                        document.add(userTable);

                        // Trip Information Section
                        document.add(new Paragraph("TRIP DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(primaryColor)
                                .setMarginBottom(10));

                        Table tripTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(15);

                        // Add pickup details with background
                        String pickupText = tvPickupDetails.getText().toString();
                        tripTable.addCell(new Cell()
                                .add(new Paragraph("Pickup:").setBold().setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));
                        tripTable.addCell(new Cell()
                                .add(new Paragraph(pickupText.replace("Pickup: ", "")).setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));

                        // Add dropoff details with background
                        String dropoffText = tvDropoffDetails.getText().toString();
                        tripTable.addCell(new Cell()
                                .add(new Paragraph("Drop-off:").setBold().setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));
                        tripTable.addCell(new Cell()
                                .add(new Paragraph(dropoffText.replace("Drop-off: ", "")).setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));

                        document.add(tripTable);

                        // Payment Information Section
                        document.add(new Paragraph("PAYMENT DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(primaryColor)
                                .setMarginBottom(10));

                        Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(20);

                        // Add payment method with background
                        String paymentText = tvPaymentDetails.getText().toString();
                        paymentTable.addCell(new Cell()
                                .add(new Paragraph("Payment Method:").setBold().setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));
                        paymentTable.addCell(new Cell()
                                .add(new Paragraph(paymentText.replace("Payment: ", "")).setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));

                        // Add total amount with highlighting using success green and background
                        DeviceRgb successColor = getColorFromResource(R.color.success_green);
                        paymentTable.addCell(new Cell()
                                .add(new Paragraph("Total Amount:").setBold().setFontColor(whiteText))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));
                        paymentTable.addCell(new Cell()
                                .add(new Paragraph(tvTotalAmount.getText().toString())
                                        .setBold()
                                        .setFontSize(14)
                                        .setFontColor(successColor))
                                .setBackgroundColor(backgroundSecondary)
                                .setPadding(8));

                        document.add(paymentTable);

                        // Footer with thank you message
                        document.add(new Paragraph("")
                                .setHeight(1)
                                .setBorder(new SolidBorder(dividerColor, 1))
                                .setMarginTop(20)
                                .setMarginBottom(15)
                        );

                        document.add(new Paragraph("Thank you for choosing our Car Booking service!")
                                .setFontSize(12)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontColor(accentColor)
                                .setItalic());

                        document.add(new Paragraph("This is an electronically generated document and requires no signature.")
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontColor(whiteText)
                                .setMarginTop(5));

                        document.close();
                        outputStream.close();
                        Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveTicketAsImage() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "ticket.pdf");

        startActivityForResult(intent, CREATE_FILE);
    }
}