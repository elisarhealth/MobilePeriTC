package com.agyohora.mobileperitc.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class AdminSignUpActivity extends AppCompatActivity {

    EditText enter_admin_password, re_enter_admin_password;
    TextInputLayout enter_admin_password_layout, re_enter_admin_password_layout;
    Button submit_admin_password_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_admin_passoword);
        enter_admin_password = findViewById(R.id.enter_admin_password);
        enter_admin_password.addTextChangedListener(enter_admin_password_Watcher);
        re_enter_admin_password = findViewById(R.id.re_enter_admin_password);
        re_enter_admin_password.addTextChangedListener(re_enter_admin_password_Watcher);
        enter_admin_password_layout = findViewById(R.id.enter_admin_password_layout);
        re_enter_admin_password_layout = findViewById(R.id.re_enter_password_layout);
        submit_admin_password_button = findViewById(R.id.submit_admin_password_button);
    }

    public void adminSignUpActivityCallBack(final View v) {
        if (v.getId() == R.id.submit_admin_password_button) {
            String password = enter_admin_password.getText().toString();
            if (password.equals(CommonUtils.getHotSpotId())) {
                Toast.makeText(this, "You cannot use the temporary password!", Toast.LENGTH_SHORT).show();
            } else {
                AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                appPreferencesHelper.setAdminPassword(password);
                Toast.makeText(this, "Password created successfully! Now login using the password you created.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    TextWatcher enter_admin_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_admin_password_layout.setErrorEnabled(false);
            enter_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isPasswordEmpty(enter_admin_password)) {
                enter_admin_password_layout.setError(null);
                submit_admin_password_button.setVisibility(View.INVISIBLE);
            } else if (enter_admin_password.getText().toString().equals(CommonUtils.getHotSpotId())) {
                enter_admin_password_layout.setError("You cannot use the temporary password!");
                re_enter_admin_password.setEnabled(false);
                submit_admin_password_button.setVisibility(View.INVISIBLE);
            } else if (!isValidPassword(enter_admin_password.getText().toString())) {
                enter_admin_password_layout.setError("Must contain at least one character of 0-9 a-z A-Z [@#$%^&+=]");
                re_enter_admin_password.setEnabled(false);
                submit_admin_password_button.setVisibility(View.INVISIBLE);
            } else if (!isAdminPasswordSizeQualifies(enter_admin_password)) {
                enter_admin_password_layout.setError("Password length should be at least 8");
                re_enter_admin_password.setEnabled(false);
                submit_admin_password_button.setVisibility(View.INVISIBLE);
            } else {
                enter_admin_password_layout.setError(null);
                re_enter_admin_password.setEnabled(true);
            }
        }
    };

    TextWatcher re_enter_admin_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_admin_password_layout.setErrorEnabled(false);
            re_enter_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isItNewPassword()) {
                re_enter_admin_password_layout.setError("New Password doesn't match");
                enter_admin_password_layout.setError("New Password doesn't match");
            } else if (isPasswordEmpty(re_enter_admin_password)) {
                re_enter_admin_password_layout.setError(null);
                enter_admin_password_layout.setError(null);
            } else {
                re_enter_admin_password_layout.setError(null);
                enter_admin_password_layout.setError(null);
                submit_admin_password_button.setVisibility(View.VISIBLE);
            }
        }
    };

    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isItNewPassword() {
        String pwd = enter_admin_password.getText().toString();
        String enteredPwd = re_enter_admin_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    public boolean isValidPassword(String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private boolean isAdminPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() >= 8;
    }
}
