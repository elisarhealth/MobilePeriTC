package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ChangeAdminPasswordByAdmin extends AppCompatActivity {

    TextInputLayout enter_existing_admin_password_layout, enter_new_admin_password_layout, re_enter_new_admin_password_layout;
    EditText enter_existing_admin_password, enter_new_admin_password, re_enter_new_admin_password;
    AppPreferencesHelper appPreferencesHelper;
   // ImageView greenTick;
    Button submit_admin_password_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_admin_password_by_admin);
        enter_existing_admin_password_layout = findViewById(R.id.enter_existing_admin_password_layout);
        enter_new_admin_password_layout = findViewById(R.id.enter_new_admin_password_layout);
        re_enter_new_admin_password_layout = findViewById(R.id.re_enter_new_admin_password_layout);
        submit_admin_password_button = findViewById(R.id.submit_admin_password_button);

        enter_existing_admin_password = findViewById(R.id.enter_existing_admin_password);
        enter_existing_admin_password.addTextChangedListener(passwordWatcher);
        enter_new_admin_password = findViewById(R.id.enter_new_admin_password);
        enter_new_admin_password.addTextChangedListener(enter_new_passwordWatcher);
        re_enter_new_admin_password = findViewById(R.id.re_enter_new_admin_password);
        re_enter_new_admin_password.addTextChangedListener(re_enter_new_passwordWatcher);
        re_enter_new_admin_password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            changeAdminPasswordByAdminSettings(submit_admin_password_button);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });

        //greenTick = findViewById(R.id.greenTick);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
    }

    public void changeAdminPasswordByAdminSettings(View v) {
        if (v.getId() == R.id.submit_admin_password_button) {
            String adminPass = enter_existing_admin_password.getText().toString();
            String enterAdminPass = enter_new_admin_password.getText().toString();
            String reEnterAdminPass = re_enter_new_admin_password.getText().toString();
            if (!appPreferencesHelper.getAdminPassword().equals(adminPass)) {
                enter_existing_admin_password_layout.setError("Current Password you entered is wrong");
            } else if (enterAdminPass.length() < 8) {
                enter_new_admin_password_layout.setError("Password should be minimum 8 characters!");
            } else if (reEnterAdminPass.length() < 8) {
                re_enter_new_admin_password_layout.setError("Password should be minimum 8 characters!");
            } else if (!enterAdminPass.equals(reEnterAdminPass)) {
                enter_new_admin_password_layout.setError("Password doesn't match");
                re_enter_new_admin_password_layout.setError("Password doesn't match");
            } else {
                appPreferencesHelper.setAdminPassword(enterAdminPass);
                appPreferencesHelper.setLoginStatus(false);
                appPreferencesHelper.setRole(0);
                Toast.makeText(this, "Password changed successfully! Now login using the new password.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("keyName", "");
                setResult(200, intent);
                finish();
            }
        }

    }

    private boolean validatePassword() {
        String pwd = appPreferencesHelper.getAdminPassword();
        String enteredPwd = enter_existing_admin_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isItOldPassword() {
        String pwd = appPreferencesHelper.getAdminPassword();
        String enteredPwd = enter_new_admin_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isItNewPassword() {
        String pwd = enter_new_admin_password.getText().toString();
        String enteredPwd = re_enter_new_admin_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() >= 8;
    }

    private void disablePasswordFields() {
        enter_new_admin_password.setEnabled(false);
        re_enter_new_admin_password.setEnabled(false);
    }

    public boolean isValidPassword(String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_existing_admin_password_layout.setErrorEnabled(false);
            enter_existing_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!validatePassword()) {
                enter_existing_admin_password_layout.setError("Incorrect Password! Please Try Again.");
               // greenTick.setVisibility(View.INVISIBLE);
                disablePasswordFields();
                if (isPasswordEmpty(enter_existing_admin_password))
                    enter_existing_admin_password_layout.setError(null);
            } else {
               // greenTick.setVisibility(View.VISIBLE);
                enter_new_admin_password.setEnabled(true);

            }
        }
    };

    TextWatcher enter_new_passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_new_admin_password_layout.setErrorEnabled(false);
            enter_new_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isItOldPassword()) {
                enter_new_admin_password_layout.setError("Password is equal to old password!");
            } else if (isPasswordEmpty(enter_new_admin_password)) {
                enter_new_admin_password.setError(null);
            } else if (!isValidPassword(enter_new_admin_password.getText().toString())) {
                enter_new_admin_password_layout.setError("Must contain at least one character of 0-9 a-z A-Z [@#$%^&+=]");
                re_enter_new_admin_password.setEnabled(false);
            } else if (!isPasswordSizeQualifies(enter_new_admin_password)) {
                enter_new_admin_password_layout.setError("Password length should be at least 8");
                re_enter_new_admin_password.setEnabled(false);
            } else {
                enter_new_admin_password_layout.setError(null);
                re_enter_new_admin_password_layout.setEnabled(true);
            }
        }
    };

    TextWatcher re_enter_new_passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_new_admin_password_layout.setErrorEnabled(false);
            re_enter_new_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isItNewPassword()) {
                re_enter_new_admin_password_layout.setError("New Password doesn't match");
                enter_new_admin_password_layout.setError("New Password doesn't match");
            } else if (isPasswordEmpty(re_enter_new_admin_password)) {
                re_enter_new_admin_password_layout.setError(null);
                enter_new_admin_password_layout.setError(null);
            } else {
                re_enter_new_admin_password_layout.setError(null);
                enter_new_admin_password_layout.setError(null);
            }
        }
    };
}
