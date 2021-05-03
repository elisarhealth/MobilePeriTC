package com.agyohora.mobileperitc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.android.material.textfield.TextInputLayout;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ChangeTemporaryAdminPassword extends AppCompatActivity {

    EditText enter_admin_password, re_enter_admin_password;
    TextInputLayout enter_admin_password_layout, re_enter_password_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_admin_passoword);
        enter_admin_password = findViewById(R.id.enter_admin_password);
        re_enter_admin_password = findViewById(R.id.re_enter_admin_password);
        enter_admin_password_layout = findViewById(R.id.enter_admin_password_layout);
        re_enter_password_layout = findViewById(R.id.re_enter_password_layout);
    }

    public void adminSignUpActivityCallBack(final View v) {
        if (v.getId() == R.id.submit_admin_password_button) {
            String password = enter_admin_password.getText().toString();
            String re_password = re_enter_admin_password.getText().toString();
            if (password.length() < 8) {
                enter_admin_password_layout.setError("Password should be minimum 8 characters!");
            } else if (re_password.length() < 8) {
                re_enter_password_layout.setError("Password should be minimum 8 characters!");
            } else if (!password.equals(re_password)) {
                enter_admin_password_layout.setError("Password doesn't match");
                re_enter_password_layout.setError("Password doesn't match");
            } else if (password.equals(CommonUtils.getHotSpotId())) {
                Toast.makeText(this, "You cannot use the temporary password!", Toast.LENGTH_SHORT).show();
            } else {
                AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                appPreferencesHelper.setAdminPassword(password);
                Toast.makeText(this, "Password created successfully! Now login using the password you created.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
