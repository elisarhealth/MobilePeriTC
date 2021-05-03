package com.agyohora.mobileperitc.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class UserSignUpActivity extends AppCompatActivity {

    EditText enter_user_password, re_enter_user_password;
    TextInputLayout enter_user_password_layout, re_enter_user_password_layout;
    Button submit_user_password_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_password);
        enter_user_password = findViewById(R.id.enter_user_password);
        enter_user_password.addTextChangedListener(enter_user_password_Watcher);
        re_enter_user_password = findViewById(R.id.re_enter_user_password);
        re_enter_user_password.addTextChangedListener(re_enter_user_password_Watcher);
        enter_user_password_layout = findViewById(R.id.enter_user_password_layout);
        re_enter_user_password_layout = findViewById(R.id.re_enter_user_password_layout);
        submit_user_password_button = findViewById(R.id.submit_user_password_button);
        re_enter_user_password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            userSignUpActivityCallBack(submit_user_password_button);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }

    public void userSignUpActivityCallBack(final View v) {
        if (v.getId() == R.id.submit_user_password_button) {
            String password = enter_user_password.getText().toString();
            if (password.equals("0000")) {
                Toast.makeText(this, "You cannot use the temporary password!", Toast.LENGTH_SHORT).show();
            } else {
                AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                appPreferencesHelper.setUserPassword(password);
                Toast.makeText(this, "Password created successfully! Now login using the password you created.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    TextWatcher enter_user_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_user_password.setEnabled(false);
            submit_user_password_button.setVisibility(View.INVISIBLE);
            re_enter_user_password_layout.setErrorEnabled(false);
            re_enter_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isUserPasswordSizeQualifies(enter_user_password)) {
                enter_user_password_layout.setError("Password should be 4 digits!");
                re_enter_user_password.setEnabled(false);
                if (isPasswordEmpty(enter_user_password))
                    re_enter_user_password_layout.setError(null);
            } else if (enter_user_password.getText().toString().equals("0000")) {
                enter_user_password_layout.setError("You cannot use the temporary password!");
                re_enter_user_password.setEnabled(false);
                submit_user_password_button.setVisibility(View.INVISIBLE);
            } else {
                re_enter_user_password.setEnabled(true);
                enter_user_password_layout.setErrorEnabled(false);
                enter_user_password_layout.setError(null);
                re_enter_user_password_layout.setErrorEnabled(false);
                re_enter_user_password_layout.setError(null);

            }
        }
    };

    TextWatcher re_enter_user_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_user_password_layout.setErrorEnabled(false);
            re_enter_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (isPasswordEmpty(re_enter_user_password)) {
                re_enter_user_password_layout.setError(null);
                submit_user_password_button.setVisibility(View.INVISIBLE);
            } else if (!isUserPasswordMatches(enter_user_password, re_enter_user_password)) {
                submit_user_password_button.setVisibility(View.INVISIBLE);
                enter_user_password_layout.setError("Password doesn't match!");
                re_enter_user_password_layout.setError("Password doesn't match!");
            } else {
                submit_user_password_button.setVisibility(View.VISIBLE);
                enter_user_password_layout.setErrorEnabled(false);
                enter_user_password_layout.setError(null);
                re_enter_user_password_layout.setErrorEnabled(false);
                re_enter_user_password_layout.setError(null);
            }
        }
    };

    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isUserPasswordMatches(EditText enter_user_password, EditText re_enter_user_password) {
        String enteredPwd = enter_user_password.getText().toString();
        String re_enteredPwd = re_enter_user_password.getText().toString();
        return re_enteredPwd.equals(enteredPwd);
    }

    private boolean isUserPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() == 4;
    }

}
