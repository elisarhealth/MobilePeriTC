package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

import static com.agyohora.mobileperitc.ui.MainActivity.dialogReference;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ServiceProfileActivity extends AppCompatActivity implements View.OnClickListener {

    AppPreferencesHelper appPreferencesHelper;
    AlertDialog alert;
    TextInputLayout passLayout;
    EditText pass;
    private FirebaseAuth firebaseAuth;
    Button change_admin_password_by_service, change_user_password_by_service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_service_profile);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        change_admin_password_by_service = findViewById(R.id.change_admin_password_by_service);
        change_user_password_by_service = findViewById(R.id.change_user_password_by_service);

        if (!CommonUtils.isUserSetUpFinished(this)) {
            change_admin_password_by_service.setVisibility(View.INVISIBLE);
            change_user_password_by_service.setVisibility(View.INVISIBLE);
        } else {
            change_admin_password_by_service.setVisibility(View.VISIBLE);
            change_user_password_by_service.setVisibility(View.VISIBLE);
        }
    }

    public void profileSettings(View view) {
        switch (view.getId()) {
            case R.id.change_admin_password_by_service:
                startActivity(new Intent(this, ChangeAdminPasswordByService.class));
                break;
            case R.id.change_user_password_by_service:
                startActivity(new Intent(this, ChangeUserPasswordByAdmin.class));
                break;
            case R.id.logout_service_profile:
                if (CommonUtils.isNetworkConnected(this)) {
                    logoutWarning();
                } else {
                    Toast.makeText(this, "Check internet connection and try again!", Toast.LENGTH_SHORT).show();
                    CommonUtils.initiateNetworkOptions(this, this, "not_applicable");
                }
                break;
        }
    }

    void logoutWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Are you sure?");
        builder.setMessage("This will log you out of the current session")
                .setCancelable(false)
                .setPositiveButton("Logout", (dialog, id) -> {
                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
                    provider.addCustomParameter("prompt", "consent");
                    provider.addCustomParameter("login_hint", "@elisar.com");
                    provider.addCustomParameter("tenant", "1a9a547e-47ed-4b5a-9d08-64153d4231f6");
                    startLogOut(provider);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER) {
            dialogReference.finish();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    void startLogOut(OAuthProvider.Builder provider) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(
                        authResult -> {
                            Toast.makeText(ServiceProfileActivity.this, "Error in Signing Out.", Toast.LENGTH_SHORT).show();
                        })
                .addOnFailureListener(e -> {
                    Log.e("TrackMe", "" + e.getMessage());
                    Log.e("TrackMe", "" + e.getLocalizedMessage());
                    Toast.makeText(ServiceProfileActivity.this, "Successfully Signed Out.", Toast.LENGTH_SHORT).show();
                    signOutComplete();
                    startActivity(new Intent(this, LoginActivity.class));
                });
    }


    private void signOutComplete() {
        FirebaseAuth.getInstance().signOut();
        appPreferencesHelper.setLoginStatus(false);
        appPreferencesHelper.setRole(0);
        dialogReference.finish();
        finish();
    }


    void switchProfile() {
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_password_input, null);
        passLayout = view.findViewById(R.id.password_layout);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Switch to User Profile Profile");
        builder.setView(view);
        builder
                .setCancelable(false)
                .setPositiveButton("Switch", (dialog, id) -> {
                    pass = view.findViewById(R.id.password);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
        Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog f = (AlertDialog) alert;
        EditText p = f.findViewById(R.id.password);
        String password = p.getText().toString();
        if (appPreferencesHelper.getUserPassword().equals(password)) {
            appPreferencesHelper.setRole(2);
            Toast.makeText(this, "Switched to User Profile successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            passLayout.setError("Password mismatch!!");
            Toast.makeText(this, "Password mismatch!!", Toast.LENGTH_SHORT).show();
        }
    }
}

