package com.midterm.mobiledesignfinalterm.UserDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.midterm.mobiledesignfinalterm.R;

public class ProfileFragment extends Fragment {
    private EditText editUsername;
    private EditText editPhone;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button btnCancel;
    private Button btnSave;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editUsername = view.findViewById(R.id.edit_username_fragment);
        editPhone = view.findViewById(R.id.edit_phone_fragment);
        editPassword = view.findViewById(R.id.edit_password_fragment);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password_fragment);
        btnSave = view.findViewById(R.id.btn_save_fragment);

        // Set up click listeners
        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> cancelEdit());
    }

    private void saveProfile() {
        // Validate inputs
        String username = editUsername.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (username.isEmpty()) {
            editUsername.setError("Username cannot be empty");
            return;
        }

        if (phone.isEmpty()) {
            editPhone.setError("Phone number cannot be empty");
            return;
        }

        // Check if password fields are filled and match
        if (!password.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError("Passwords do not match");
                return;
            }
        }

        // TODO: Save profile information to database or shared preferences
        // For now, just show a success message
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void cancelEdit() {
        // Clear all fields or revert to original values
        editUsername.setText("");
        editPhone.setText("");
        editPassword.setText("");
        editConfirmPassword.setText("");

        // Optionally navigate back or dismiss dialog
        // For example:
        // getParentFragmentManager().popBackStack();
    }
}
