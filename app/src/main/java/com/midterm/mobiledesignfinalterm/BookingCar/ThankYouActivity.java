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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
            startActivity(intent);
            finish();
        });
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
                        // Setup PDF document with A4 page size
                        PdfWriter writer = new PdfWriter(outputStream);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf, PageSize.A4);
                        document.setMargins(36, 36, 36, 36); // 0.5 inch margins

                        // Create header section
                        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                                .setWidth(UnitValue.createPercentValue(100));

                        // Company info in left column
                        Cell companyCell = new Cell()
                                .add(new Paragraph("Car Booking App").setBold().setFontSize(16))
                                .add(new Paragraph("Your Premium Transportation Service"))
                                .add(new Paragraph("www.carbookingapp.com"))
                                .add(new Paragraph("support@carbookingapp.com"))
                                .setBorder(null);

                        // Current date in right column - aligned right
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        String currentDate = dateFormatter.format(new Date());
                        Cell dateCell = new Cell()
                                .add(new Paragraph("Date: " + currentDate).setTextAlignment(TextAlignment.RIGHT))
                                .setBorder(null);

                        headerTable.addCell(companyCell);
                        headerTable.addCell(dateCell);
                        document.add(headerTable);

                        // Add divider line
                        document.add(new Paragraph("")
                                .setHeight(1)
                                .setBorder(new SolidBorder(greenPrimaryColor, 1))
                                .setMarginBottom(15)
                        );

                        // Title with styling
                        document.add(new Paragraph("BOOKING CONFIRMATION")
                                .setFontSize(20)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontColor(greenPrimaryColor)
                                .setMarginBottom(20));

                        // Extract booking ID from the text view
                        String bookingIdText = tvBookingId.getText().toString();
                        String bookingId = bookingIdText.replace("Booking ID: ", "").trim();

                        // Add booking ID with highlight
                        document.add(new Paragraph("Booking Reference: " + bookingId)
                                .setBold()
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontSize(14)
                                .setMarginBottom(20));

                        // Customer Information Section
                        document.add(new Paragraph("CUSTOMER DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(greenPrimaryColor)
                                .setMarginBottom(10));

                        // Parse user details from text view
                        String userDetailsText = tvUserDetails.getText().toString();
                        String[] userLines = userDetailsText.split("\n");

                        Table userTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(15);

                        // Add user details to table with better formatting
                        for (String line : userLines) {
                            String[] parts = line.split(":", 2);
                            if (parts.length == 2) {
                                userTable.addCell(new Cell().add(new Paragraph(parts[0].trim() + ":").setBold()));
                                userTable.addCell(new Cell().add(new Paragraph(parts[1].trim())));
                            }
                        }
                        document.add(userTable);

                        // Trip Information Section
                        document.add(new Paragraph("TRIP DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(greenPrimaryColor)
                                .setMarginBottom(10));

                        Table tripTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(15);

                        // Add pickup details
                        String pickupText = tvPickupDetails.getText().toString();
                        tripTable.addCell(new Cell().add(new Paragraph("Pickup:").setBold()));
                        tripTable.addCell(new Cell().add(new Paragraph(pickupText.replace("Pickup: ", ""))));

                        // Add dropoff details
                        String dropoffText = tvDropoffDetails.getText().toString();
                        tripTable.addCell(new Cell().add(new Paragraph("Drop-off:").setBold()));
                        tripTable.addCell(new Cell().add(new Paragraph(dropoffText.replace("Drop-off: ", ""))));

                        document.add(tripTable);

                        // Payment Information Section
                        document.add(new Paragraph("PAYMENT DETAILS")
                                .setFontSize(14)
                                .setBold()
                                .setFontColor(greenPrimaryColor)
                                .setMarginBottom(10));

                        Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                .setWidth(UnitValue.createPercentValue(100))
                                .setMarginBottom(20);

                        // Add payment method
                        String paymentText = tvPaymentDetails.getText().toString();
                        paymentTable.addCell(new Cell().add(new Paragraph("Payment Method:").setBold()));
                        paymentTable.addCell(new Cell().add(new Paragraph(paymentText.replace("Payment: ", ""))));

                        // Add total amount with highlighting
                        paymentTable.addCell(new Cell().add(new Paragraph("Total Amount:").setBold()));
                        paymentTable.addCell(new Cell()
                                .add(new Paragraph(tvTotalAmount.getText().toString())
                                        .setBold()
                                        .setFontSize(14)
                                        .setFontColor(greenPrimaryColor)));

                        document.add(paymentTable);

                        // Footer with thank you message
                        document.add(new Paragraph("")
                                .setHeight(1)
                                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                                .setMarginTop(20)
                                .setMarginBottom(15)
                        );

                        document.add(new Paragraph("Thank you for choosing our Car Booking service!")
                                .setFontSize(12)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setItalic());

                        document.add(new Paragraph("This is an electronically generated document and requires no signature.")
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
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