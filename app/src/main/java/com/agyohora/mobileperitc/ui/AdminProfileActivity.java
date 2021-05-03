package com.agyohora.mobileperitc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.agyohora.mobileperitc.ui.MainActivity.dialogReference;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class AdminProfileActivity extends AppCompatActivity implements View.OnClickListener {

    AppPreferencesHelper appPreferencesHelper;
    AlertDialog alert;
    TextInputLayout passLayout;
    EditText pass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_profile);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
    }

    public void profileSettings(View view) {
        switch (view.getId()) {
            case R.id.change_admin_password_by_admin:
                startActivityForResult(new Intent(this, ChangeAdminPasswordByAdmin.class), RESULT_FIRST_USER);
                break;
            case R.id.change_user_password_by_admin:
                startActivity(new Intent(this, ChangeUserPasswordByAdmin.class));
                break;
            case R.id.switch_from_admin_to_user:
                if (appPreferencesHelper.getUserPassword() == null) {
                    userNotSignedUp();
                } else {
                    switchProfile();
                }
                break;
            case R.id.logout_admin_profile:
                logoutWarning();
                break;
            case R.id.restore_database:
                restoreDatabase();
                break;
        }
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

    void restoreDatabase() {
        finish();
        Actions.showDataBaseRestore();
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);        alert.show();
    }


    void switchProfile() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_pin_input, null);
        passLayout = view.findViewById(R.id.password_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Switch to User Profile");
        builder.setView(view);
        builder
                .setCancelable(false)
                .setPositiveButton("Switch", (dialog, id) -> {
                    pass = view.findViewById(R.id.password);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);        alert.show();
        Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog f = (AlertDialog) alert;
        EditText p = f.findViewById(R.id.password);
        String password = p.getText().toString();
        if (appPreferencesHelper.getUserPassword().equals(password)) {
            closeKeyboard(p);
            appPreferencesHelper.setRole(2);
            Toast.makeText(this, "Switched to User Profile successfully", Toast.LENGTH_SHORT).show();
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

    void userNotSignedUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Profile Not Signed Up");
        builder.setMessage("User profile has not been set-up. Please logout and complete User profile set-up.")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);        alert.show();
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
