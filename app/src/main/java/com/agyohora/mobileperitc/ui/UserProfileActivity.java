package com.agyohora.mobileperitc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.google.android.material.textfield.TextInputLayout;

import static com.agyohora.mobileperitc.ui.MainActivity.dialogReference;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    AppPreferencesHelper appPreferencesHelper;
    AlertDialog alert;
    TextInputLayout passLayout;
    EditText pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            dialogReference.finish();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void profileSettings(View view) {
        switch (view.getId()) {
            case R.id.change_user_password_by_user:
                startActivityForResult(new Intent(this, ChangeUserPasswordByUser.class), RESULT_FIRST_USER);
                break;
            case R.id.switch_from_user_to_admin:
                if (appPreferencesHelper.getAdminPassword() == null) {
                    adminNotSignedUp();
                } else {
                    switchProfile();
                }
                break;
            case R.id.logout_user_profile:
                logoutWarning();
                break;
        }
    }

    void logoutWarning() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Are you sure?");
        builder.setMessage("This will log you out of the current session")
                .setCancelable(false)
                .setPositiveButton("Logout", (dialog, id) -> {
                    appPreferencesHelper.setLoginStatus(false);
                    appPreferencesHelper.setRole(0);
                    dialogReference.finish();
                    finish();
                    startActivity(new Intent(this, LoginActivity.class));
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void switchProfile() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_password_input, null);
        passLayout = view.findViewById(R.id.password_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Switch to Admin Profile");
        builder.setView(view);
        builder.setCancelable(false)
                .setPositiveButton("Switch", (dialog, id) -> {
                    pass = view.findViewById(R.id.password);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    pass = view.findViewById(R.id.password);
                    closeKeyboard(pass);
                });
        alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
        Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.e("saved pass", " " + appPreferencesHelper.getAdminPassword());
        AlertDialog f = (AlertDialog) alert;
        EditText p = f.findViewById(R.id.password);
        String password = p.getText().toString();

        Log.e("input pass1", " " + password);
        if (appPreferencesHelper.getAdminPassword().equals(password)) {
            closeKeyboard(p);
            appPreferencesHelper.setRole(1);
            Toast.makeText(this, "Switched to Admin Profile successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            passLayout.setError("Password mismatch!!");
            Toast.makeText(this, "Password mismatch!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeKeyboard(EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText
                    .getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("closeKeyboard", " " + e.getMessage());
        }
    }

    void adminNotSignedUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Profile Not Signed Up");
        builder.setMessage("Admin profile has not been set-up. Please logout and complete Admin profile set-up.")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }
}

