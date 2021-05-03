package com.agyohora.mobileperitc.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.google.android.material.textfield.TextInputLayout;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ChangeUserPasswordByService extends AppCompatActivity {

    TextInputLayout enter_new_password_layout, re_enter_new_user_password_layout;
    EditText new_password, re_enter_new_user_password;
    AppPreferencesHelper appPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_user_password_by_admin);
        enter_new_password_layout = findViewById(R.id.enter_new_password_layout);
        re_enter_new_user_password_layout = findViewById(R.id.re_enter_new_user_password_layout);

        new_password = findViewById(R.id.new_password);
        new_password.addTextChangedListener(passwordWatcher);
        re_enter_new_user_password = findViewById(R.id.re_enter_new_user_password);
        re_enter_new_user_password.addTextChangedListener(enter_new_passwordWatcher);

        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
    }

    public void changeUserPasswordByAdminSettings(View v) {
        String enterUserPass = new_password.getText().toString();
        String reEnterUserPass = re_enter_new_user_password.getText().toString();
        if (v.getId() == R.id.submit_user_password_button) {
            if (enterUserPass.length() < 4) {
                enter_new_password_layout.setError("Password should be minimum 4 digits!");
            } else if (reEnterUserPass.length() < 4) {
                re_enter_new_user_password_layout.setError("Password should be minimum 4 digits!");
            } else if (!enterUserPass.equals(reEnterUserPass)) {
                enter_new_password_layout.setError("Password doesn't match");
                re_enter_new_user_password_layout.setError("Password doesn't match");
            } else {
                appPreferencesHelper.setUserPassword(enterUserPass);
                Toast.makeText(this, "Password changed successfully! Now User can login using the new password.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private boolean isPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() == 4;
    }

    private boolean isItOldPassword() {
        String pwd = appPreferencesHelper.getUserPassword();
        String enteredPwd = new_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isItNewPassword() {
        String pwd = new_password.getText().toString();
        String enteredPwd = re_enter_new_user_password.getText().toString();
        return pwd.equals(enteredPwd);
    }


    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_new_password_layout.setErrorEnabled(false);
            enter_new_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isItOldPassword()) {
                enter_new_password_layout.setError("Password is equal to old password!");
                re_enter_new_user_password.setEnabled(false);
            } else if (isPasswordEmpty(new_password)) {
                enter_new_password_layout.setError(null);
                re_enter_new_user_password.setEnabled(false);
            } else if (new_password.getText().toString().length() < 4) {
                enter_new_password_layout.setError("Password length should be 4 digits");
                re_enter_new_user_password.setEnabled(false);
            } else {
                enter_new_password_layout.setError(null);
                re_enter_new_user_password.setEnabled(true);
            }
        }
    };

    TextWatcher enter_new_passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_new_user_password_layout.setErrorEnabled(false);
            re_enter_new_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isItNewPassword()) {
                enter_new_password_layout.setError("New Password doesn't match");
                re_enter_new_user_password_layout.setError("New Password doesn't match");
            } else if (!isPasswordSizeQualifies(re_enter_new_user_password)) {
                re_enter_new_user_password_layout.setError("Password length should be 4 digits");
            } else if (isPasswordEmpty(re_enter_new_user_password)) {
                re_enter_new_user_password_layout.setErrorEnabled(false);
                re_enter_new_user_password_layout.setError(null);
            } else {
                enter_new_password_layout.setError(null);
                re_enter_new_user_password_layout.setError(null);
            }
        }
    };
}
