package com.midterm.mobiledesignfinalterm.CarListing;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.midterm.mobiledesignfinalterm.R;

public class FilterDialog extends Dialog {

    public interface FilterDialogListener {
        void onFiltersApplied(String brand, String vehicleType, String fuelType, Integer seats);
    }

    private FilterDialogListener listener;
    private String selectedBrand = null;
    private String selectedVehicleType = null;
    private String selectedFuelType = null;
    private Integer selectedSeats = null;

    private ChipGroup chipGroupBrand;
    private ChipGroup chipGroupVehicleType;
    private ChipGroup chipGroupFuelType;
    private ChipGroup chipGroupSeats;

    private Button btnReset;
    private Button btnApply;

    public FilterDialog(Context context) {
        super(context);
    }

    public void setListener(FilterDialogListener listener) {
        this.listener = listener;
    }

    public void setInitialFilters(String brand, String vehicleType, String fuelType, Integer seats) {
        this.selectedBrand = brand;
        this.selectedVehicleType = vehicleType;
        this.selectedFuelType = fuelType;
        this.selectedSeats = seats;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filter_carlisting);

        // Khởi tạo views
        chipGroupBrand = findViewById(R.id.chipGroupBrand);
        chipGroupVehicleType = findViewById(R.id.chipGroupVehicleType);
        chipGroupFuelType = findViewById(R.id.chipGroupFuelType);
        chipGroupSeats = findViewById(R.id.chipGroupSeats);
        btnReset = findViewById(R.id.btnReset);
        btnApply = findViewById(R.id.btnApply);

        // Thiết lập trạng thái ban đầu nếu đã chọn trước đó
        applyInitialState();

        // Thiết lập listeners cho chip groups
        setupChipListeners();

        // Thiết lập listeners cho buttons
        setupButtonListeners();
    }

    private void applyInitialState() {
        // Brand selection
        if (selectedBrand != null) {
            for (int i = 0; i < chipGroupBrand.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupBrand.getChildAt(i);
                if (chip.getText().toString().equalsIgnoreCase(selectedBrand)) {
                    chip.setChecked(true);
                    break;
                }
            }
        } else {
            // Mặc định chọn "All"
            ((Chip) chipGroupBrand.getChildAt(0)).setChecked(true);
        }

        // Vehicle type selection
        if (selectedVehicleType != null) {
            for (int i = 0; i < chipGroupVehicleType.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupVehicleType.getChildAt(i);
                if (chip.getText().toString().equalsIgnoreCase(selectedVehicleType)) {
                    chip.setChecked(true);
                    break;
                }
            }
        } else {
            // Mặc định chọn "All"
            ((Chip) chipGroupVehicleType.getChildAt(0)).setChecked(true);
        }

        // Fuel type selection
        if (selectedFuelType != null) {
            for (int i = 0; i < chipGroupFuelType.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupFuelType.getChildAt(i);
                if (chip.getText().toString().equalsIgnoreCase(selectedFuelType)) {
                    chip.setChecked(true);
                    break;
                }
            }
        } else {
            // Mặc định chọn "All"
            ((Chip) chipGroupFuelType.getChildAt(0)).setChecked(true);
        }

        // Seats selection
        if (selectedSeats != null) {
            for (int i = 0; i < chipGroupSeats.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupSeats.getChildAt(i);
                String chipText = chip.getText().toString();

                if (chipText.contains(selectedSeats.toString())) {
                    chip.setChecked(true);
                    break;
                }
            }
        } else {
            // Mặc định chọn "All"
            ((Chip) chipGroupSeats.getChildAt(0)).setChecked(true);
        }
    }

    private void setupChipListeners() {
        // Brand selection
        chipGroupBrand.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipBrandAll) {
                selectedBrand = null;
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    selectedBrand = chip.getText().toString();
                }
            }
        });

        // Vehicle type selection
        chipGroupVehicleType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipTypeAll) {
                selectedVehicleType = null;
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    selectedVehicleType = chip.getText().toString().toLowerCase();
                }
            }
        });

        // Fuel type selection
        chipGroupFuelType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipFuelAll) {
                selectedFuelType = null;
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    selectedFuelType = chip.getText().toString().toLowerCase();
                }
            }
        });

        // Seats selection
        chipGroupSeats.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSeatsAll) {
                selectedSeats = null;
            } else if (checkedId == R.id.chipSeats2) {
                selectedSeats = 2;
            } else if (checkedId == R.id.chipSeats4) {
                selectedSeats = 4;
            } else if (checkedId == R.id.chipSeats5) {
                selectedSeats = 5;
            } else if (checkedId == R.id.chipSeats7) {
                selectedSeats = 7;
            }
        });
    }

    private void setupButtonListeners() {
        btnReset.setOnClickListener(v -> resetFilters());

        btnApply.setOnClickListener(v -> {
            if (listener != null) {
                // Gọi callback đến activity với các filter đã chọn
                listener.onFiltersApplied(selectedBrand, selectedVehicleType, selectedFuelType, selectedSeats);
            }
            dismiss();
        });
    }

    private void resetFilters() {
        selectedBrand = null;
        selectedVehicleType = null;
        selectedFuelType = null;
        selectedSeats = null;

        // Reset chips về trạng thái mặc định
        ((Chip) chipGroupBrand.getChildAt(0)).setChecked(true);
        ((Chip) chipGroupVehicleType.getChildAt(0)).setChecked(true);
        ((Chip) chipGroupFuelType.getChildAt(0)).setChecked(true);
        ((Chip) chipGroupSeats.getChildAt(0)).setChecked(true);
    }
}
