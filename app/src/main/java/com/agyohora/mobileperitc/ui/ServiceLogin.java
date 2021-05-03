package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

import static com.agyohora.mobileperitc.store.Store.activeView_number;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ServiceLogin extends AppCompatActivity {

    AppPreferencesHelper appPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_login);
        FirebaseApp.initializeApp(this);
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");
        provider.addCustomParameter("prompt", "consent");
        provider.addCustomParameter("login_hint", "@elisar.com");
        provider.addCustomParameter("tenant", "1a9a547e-47ed-4b5a-9d08-64153d4231f6");
        activeView_number = R.layout.activity_home_screen;
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        startLogin(provider);
    }

    void startLogin(OAuthProvider.Builder provider) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener(
                        authResult -> {
                            String userName = authResult.getUser().getDisplayName();
                            String email = authResult.getUser().getEmail();
                            Toast.makeText(ServiceLogin.this, "Login Success", Toast.LENGTH_SHORT).show();
                            appPreferencesHelper.setLoginStatus(true);
                            appPreferencesHelper.setRole(3);
                            finish();
                            startMainActivity();
                        })
                .addOnFailureListener(e -> {
                    Log.e("TrackMe", "" + e.getMessage());
                    Log.e("TrackMe", "" + e.getLocalizedMessage());
                    Toast.makeText(ServiceLogin.this, "Unable to Login! Try again.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    void startMainActivity() {
        MyApplication.getInstance().set_HMD_CONNECTION_NEED(true);
        activeView_number = R.layout.activity_home_screen;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
