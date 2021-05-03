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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.google.android.material.textfield.TextInputLayout;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ChangeUserPasswordByUser extends AppCompatActivity {

    TextInputLayout enter_existing_user_password_layout, enter_new_user_password_layout, re_enter_new_user_password_layout;
    EditText enter_existing_user_password, enter_new_user_password, re_enter_new_user_password;
    AppPreferencesHelper appPreferencesHelper;
    //ImageView greenTick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_user_password_by_user);

       // greenTick = findViewById(R.id.greenTick);

        enter_existing_user_password_layout = findViewById(R.id.enter_existing_user_password_layout);
        enter_new_user_password_layout = findViewById(R.id.enter_new_user_password_layout);
        re_enter_new_user_password_layout = findViewById(R.id.re_enter_new_user_password_layout);

        enter_existing_user_password = findViewById(R.id.enter_existing_user_password);
        enter_existing_user_password.addTextChangedListener(passwordWatcher);
        enter_new_user_password = findViewById(R.id.enter_new_user_password);
        enter_new_user_password.addTextChangedListener(enter_new_passwordWatcher);
        re_enter_new_user_password = findViewById(R.id.re_enter_new_user_password);
        re_enter_new_user_password.addTextChangedListener(re_enter_new_passwordWatcher);

        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);

        re_enter_new_user_password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            changeUserPasswordByUserSettings(re_enter_new_user_password);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });

    }

    public void changeUserPasswordByUserSettings(View v) {
        String userPass = enter_existing_user_password.getText().toString();
        String enterUserPass = enter_new_user_password.getText().toString();
        String reEnterUserPass = re_enter_new_user_password.getText().toString();
        if (!appPreferencesHelper.getUserPassword().equals(userPass)) {
            enter_existing_user_password_layout.setError("Current Password you entered is wrong");
        } else if (enterUserPass.length() < 4) {
            enter_new_user_password_layout.setError("Password should be minimum 4 digits!");
        } else if (reEnterUserPass.length() < 4) {
            re_enter_new_user_password_layout.setError("Password should be minimum 4 digits!");
        } else if (!enterUserPass.equals(reEnterUserPass)) {
            enter_new_user_password_layout.setError("Password doesn't match");
            re_enter_new_user_password_layout.setError("Password doesn't match");
        } else {
            appPreferencesHelper.setUserPassword(enterUserPass);
            appPreferencesHelper.setLoginStatus(false);
            appPreferencesHelper.setRole(0);
            Toast.makeText(this, "Password changed successfully! Now login using the new password.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("keyName", "");
            setResult(200, intent);
            finish();
        }
    }

    private boolean validatePassword() {
        String pwd = appPreferencesHelper.getUserPassword();
        String enteredPwd = enter_existing_user_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isItOldPassword() {
        String pwd = appPreferencesHelper.getUserPassword();
        String enteredPwd = enter_new_user_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isItNewPassword() {
        String pwd = enter_new_user_password.getText().toString();
        String enteredPwd = re_enter_new_user_password.getText().toString();
        return pwd.equals(enteredPwd);
    }


    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() == 4;
    }

    private void disablePasswordFields() {
        enter_new_user_password.setEnabled(false);
        re_enter_new_user_password.setEnabled(false);
    }

    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_existing_user_password_layout.setErrorEnabled(false);
            enter_existing_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!validatePassword()) {
                enter_existing_user_password_layout.setError("Incorrect Password! Please Try Again.");
                //greenTick.setVisibility(View.INVISIBLE);
                disablePasswordFields();
                if (isPasswordEmpty(enter_existing_user_password))
                    enter_existing_user_password_layout.setError(null);
            } else {
                //greenTick.setVisibility(View.VISIBLE);
                enter_new_user_password.setEnabled(true);

            }
        }
    };

    TextWatcher enter_new_passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_new_user_password_layout.setErrorEnabled(false);
            enter_new_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isItOldPassword()) {
                enter_new_user_password_layout.setError("Password is equal to old password!");
               // greenTick.setVisibility(View.INVISIBLE);
            } else if (isPasswordEmpty(enter_new_user_password)) {
                enter_new_user_password.setError(null);
            } else if (!isPasswordSizeQualifies(enter_new_user_password)) {
                enter_new_user_password_layout.setError("Password length should be four digits");
                re_enter_new_user_password.setEnabled(false);
            } else {
                enter_existing_user_password_layout.setError(null);
                re_enter_new_user_password.setEnabled(true);
            }
        }
    };

    TextWatcher re_enter_new_passwordWatcher = new TextWatcher() {
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
                re_enter_new_user_password_layout.setError("New Password doesn't match");
                enter_new_user_password_layout.setError("New Password doesn't match");
            } else if (isPasswordEmpty(re_enter_new_user_password)) {
                re_enter_new_user_password_layout.setError(null);
                enter_new_user_password_layout.setError(null);
            } else {
                re_enter_new_user_password_layout.setError(null);
                enter_new_user_password_layout.setError(null);
            }
        }
    };
}
