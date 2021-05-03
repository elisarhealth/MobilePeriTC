package com.agyohora.mobileperitc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.agyohora.mobileperitc.store.Store.activeView_number;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.CommonUtils.getHotSpotId;

public class LoginActivity extends AppCompatActivity {
    ImageView profileIcon, greenTick;
    Button signUp, login;
    AppPreferencesHelper appPreferencesHelper;
    RadioGroup userRoles;
    RadioButton selectedRole;
    RadioButton user_switch, admin_switch;
    EditText password, user_password;
    TextInputLayout password_layout, user_password_layout;
    Button forgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        profileIcon = findViewById(R.id.profileIcon);
        greenTick = findViewById(R.id.greenTick);
        signUp = findViewById(R.id.signUp_button);
        login = findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgotPassword);
        user_switch = findViewById(R.id.user_switch);
        admin_switch = findViewById(R.id.admin_switch);
        userRoles = findViewById(R.id.toggleProfile);
        password = findViewById(R.id.password);
        user_password = findViewById(R.id.user_password);
        password.addTextChangedListener(watcher);
        user_password.addTextChangedListener(user_password_watcher);
        password_layout = findViewById(R.id.password_layout);
        user_password_layout = findViewById(R.id.user_password_layout);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        clickCallback(user_switch);
        password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            clickCallback(login);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
        user_password.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE) {
                            clickCallback(login);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isItAdmin()) {
            clickCallback(admin_switch);
        } else {
            clickCallback(user_switch);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER) {
            startBarCodeActivity();
        }
        try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!(result.getContents() == null)) {
                    JSONObject retrievedData = new JSONObject(CommonUtils.decryptData(result.getContents()));
                    if (isQrContainsValidParams(retrievedData)) {
                        String devId = retrievedData.getString("DeviceId").trim();
                        String time = retrievedData.getString("ValidFrom").trim();
                        Log.e("QRSCAN", "Scanned ID " + devId + " id from config " + getHotSpotId());
                        if (devId.equalsIgnoreCase(getHotSpotId())) {
                            if (findDifference(time)) {
                                startActivity(new Intent(this, ResetAdminPassword.class));
                            } else {
                                errorDialog("QR Expired", "The validity of QR expired. Get new QR from service team.");
                            }
                        } else {
                            errorDialog("Mismatch!", "Device Id scanned does not match.");
                        }

                    } else {
                        errorDialog("Invalid QR!", "QR doesn't contains the valid data");
                    }
                } else {
                    errorDialog("Invalid QR!", "The QR code you have scanned is not been sent by us.");
                }
            }

        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage());
            errorDialog("Invalid QR!", "The QR code you have scanned is not been sent by us.");
        }
    }

    private boolean isQrContainsValidParams(JSONObject retrievedData) {
        return retrievedData.has("DeviceId") && retrievedData.has("ValidFrom");
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean validatePassword() {
        if (isItAdmin()) {
            String pwd = appPreferencesHelper.getAdminPassword();
            String enteredPwd = password.getText().toString();
            return pwd.equals(enteredPwd);
        } else {
            String pwd = appPreferencesHelper.getUserPassword();
            String enteredPwd = user_password.getText().toString();
            return pwd.equals(enteredPwd);
        }
    }

    private boolean isPasswordEmpty() {
        if (isItAdmin()) {
            String enteredPwd = password.getText().toString();
            return enteredPwd.isEmpty();
        } else {
            String enteredPwd = user_password.getText().toString();
            return enteredPwd.isEmpty();
        }
    }

    private Bundle createBundle(String path, String message, boolean proceed, JSONObject retrievedData) {
        try {
            Bundle bundle = new Bundle();

            bundle.putString("path", path);
            bundle.putString("message", message);
            bundle.putBoolean("proceed", proceed);
            bundle.putString("retrievedData", retrievedData.toString());
            bundle.putString("DeviceId", retrievedData.getString("DeviceId"));
            bundle.putString("OrganizationId", retrievedData.getString("OrganizationId"));
            bundle.putString("SiteId", retrievedData.getString("SiteId"));
            bundle.putString("TcId", retrievedData.getString("TcId"));
            bundle.putString("TcSwId", retrievedData.getString("TcSwId"));
            bundle.putString("TcSwOs", retrievedData.getString("TcSwOs"));
            bundle.putString("TcHwModelId", retrievedData.getString("TcHwModelId"));
            bundle.putString("HmdSwId", retrievedData.getString("HmdSwId"));
            bundle.putString("HmdId", retrievedData.getString("HmdId"));
            bundle.putString("HmdSwOs", retrievedData.getString("HmdSwOs"));
            bundle.putString("HmdWoWNormativeDataId", retrievedData.getString("HmdWoWNormativeDataId"));
            bundle.putString("HmdFirmwareId", retrievedData.getString("HmdFirmwareId"));
            bundle.putString("HmdFirmwareId", retrievedData.getString("HmdFirmwareId"));
            bundle.putString("HmdPhoneModelId", retrievedData.getString("HmdPhoneModelId"));
            bundle.putString("HmdHwModelId", retrievedData.getString("HmdHwModelId"));
            bundle.putString("HmdPhoneDisplayId", retrievedData.getString("HmdPhoneDisplayId"));
            return bundle;
        } catch (JSONException e) {
            Log.e("CreateBundle", " " + e.getMessage());
            return null;
        }
    }

    private void startBarCodeActivity() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setBeepEnabled(true);
        integrator.setPrompt("Focus the QR code inside this box");
        integrator.setCaptureActivity(ForgotBarCodeCaptureActivity.class);
        integrator.initiateScan();
    }

    static boolean findDifference(String start_date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try {
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(sdf.format(date));


            long difference_In_Time = d2.getTime() - d1.getTime();

            long difference_In_Seconds = (difference_In_Time / 1000) % 60;

            long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

            long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

            long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

            Log.e("findDifference", "Difference " + "between two dates is: ");

            Log.e("findDifference", difference_In_Years
                    + " years, "
                    + difference_In_Days
                    + " days, "
                    + difference_In_Hours
                    + " hours, "
                    + difference_In_Minutes
                    + " minutes, "
                    + difference_In_Seconds
                    + " seconds");
            return difference_In_Days < 2 || (difference_In_Days == 2 && difference_In_Hours == 0 && difference_In_Minutes == 0 && difference_In_Seconds == 0);
        }

        // Catch the Exception
        catch (ParseException e) {
            Log.e("Exception", " " + e.getMessage());
            return false;
        }
    }

    public void clickCallback(final View v) {
        switch (v.getId()) {
            case R.id.user_switch:
                profileIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_white_48));
                greenTick.setVisibility(View.INVISIBLE);
                if (appPreferencesHelper.getUserPassword() != null) {
                    signUp.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    password_layout.setVisibility(View.INVISIBLE);
                    user_password_layout.setVisibility(View.VISIBLE);
                    forgotPassword.setVisibility(View.VISIBLE);
                } else {
                    signUp.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);
                    password_layout.setVisibility(View.INVISIBLE);
                    user_password_layout.setVisibility(View.INVISIBLE);
                    forgotPassword.setVisibility(View.GONE);
                }
                break;
            case R.id.admin_switch:
                greenTick.setVisibility(View.INVISIBLE);
                profileIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_admin_panel_settings_white_48));
                if (appPreferencesHelper.getAdminPassword() != null) {
                    signUp.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    password_layout.setVisibility(View.VISIBLE);
                    user_password_layout.setVisibility(View.INVISIBLE);
                    forgotPassword.setVisibility(View.VISIBLE);
                } else {
                    signUp.setVisibility(View.VISIBLE);
                    login.setVisibility(View.INVISIBLE);
                    password_layout.setVisibility(View.INVISIBLE);
                    user_password_layout.setVisibility(View.INVISIBLE);
                    forgotPassword.setVisibility(View.GONE);
                }
                break;
            case R.id.login_button:
                Log.e("login_button", "called");
                closeKeyboard();
                selectedRole = findViewById(userRoles.getCheckedRadioButtonId());
                if (isItAdmin()) {
                    if (appPreferencesHelper.getAdminPassword() == null) {
                        Toast.makeText(this, "Please Sign Up first", Toast.LENGTH_SHORT).show();
                    } else if (appPreferencesHelper.getAdminPassword().equals(CommonUtils.getHotSpotId())) {
                        changeTempPasswordWarning(true);
                    } else {
                        String pwd = appPreferencesHelper.getAdminPassword();
                        String enteredPwd = password.getText().toString();
                        if (pwd.equals(enteredPwd)) {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                            finish();
                            appPreferencesHelper.setLoginStatus(true);
                            appPreferencesHelper.setRole(1);
                            startMainActivity();
                        } else {
                            password.selectAll();
                            password_layout.setError("Incorrect Password! Please Try Again.");
                        }
                    }
                } else {
                    Log.e("login_button", "called this is user");
                    if (appPreferencesHelper.getUserPassword() == null) {
                        Toast.makeText(this, "Please Sign Up first", Toast.LENGTH_SHORT).show();
                    } else if (appPreferencesHelper.getUserPassword().equals("0000")) {
                        changeTempPasswordWarning(false);
                    } else {
                        String pwd = appPreferencesHelper.getUserPassword();
                        String enteredPwd = user_password.getText().toString();
                        if (pwd.equals(enteredPwd)) {
                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                            finish();
                            appPreferencesHelper.setLoginStatus(true);
                            appPreferencesHelper.setRole(2);
                            startMainActivity();
                        } else {
                            Log.e("login_button", "called error");
                            user_password_layout.setError("Incorrect Password! Please Try Again.");
                        }
                    }
                }
                break;
            case R.id.forgotPassword:
                if (isItAdmin()) {
                    adminForgetPasswordWarning();
                } else {
                    userForgetPasswordWarning();
                }
                break;
            case R.id.serviceLogin:
                if (CommonUtils.isNetworkConnected(this)) {
                    startActivity(new Intent(this, ServiceLogin.class));
                } else {
                    CommonUtils.initiateNetworkOptions(this, this, "not_applicable");
                }
                break;
            case R.id.signUp_button:
                if (isItAdmin())
                    startActivity(new Intent(this, AdminSignUpActivity.class));
                else
                    startActivity(new Intent(this, UserSignUpActivity.class));
                break;
        }
    }

    boolean isItAdmin() {
        selectedRole = findViewById(userRoles.getCheckedRadioButtonId());
        return selectedRole.getText().toString().equals("ADMIN");
    }

    void userForgetPasswordWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Forgot Password - User profile");
        builder.setView(R.layout.user_profile_forget_password_layout)
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void adminForgetPasswordWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Forgot Password - Admin profile");
        builder.setView(R.layout.admin_forget_password_layout)
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, id) -> {
                    startBarCodeActivity();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void startMainActivity() {
        MyApplication.getInstance().set_HMD_CONNECTION_NEED(true);
        activeView_number = R.layout.activity_home_screen;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    void changeTempPasswordWarning(boolean isThisAdmin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Alert");
        builder.setMessage("Please change temporary password")
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, id) -> {
                    if (isThisAdmin)
                        startActivity(new Intent(this, AdminSignUpActivity.class));
                    else
                        startActivity(new Intent(this, UserSignUpActivity.class));
                })
                .setNegativeButton("Cancel", (dialog, id) -> {

                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void errorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, id) -> startBarCodeActivity())
                .setNegativeButton("Cancel", (dialog, id) -> {
                    //do things
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            password_layout.setErrorEnabled(false);
            password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!validatePassword()) {
                password_layout.setError("Incorrect Password! Please Try Again.");
                greenTick.setVisibility(View.INVISIBLE);
                if (isPasswordEmpty())
                    password_layout.setError(null);
            } else {
                greenTick.setVisibility(View.VISIBLE);
            }
        }
    };

    TextWatcher user_password_watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            user_password_layout.setErrorEnabled(false);
            user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!validatePassword()) {
                user_password_layout.setError("Incorrect Password! Please Try Again.");
                greenTick.setVisibility(View.INVISIBLE);
                if (isPasswordEmpty())
                    user_password_layout.setError(null);
            } else {
                greenTick.setVisibility(View.VISIBLE);
            }
        }
    };
}
