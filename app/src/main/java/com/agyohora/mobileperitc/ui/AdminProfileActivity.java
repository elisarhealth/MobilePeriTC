package com.agyohora.mobileperitc.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.Constants;
//import com.agyohora.mobileperitc.utils.ZipManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        if (BuildConfig.IN21_011_Saving_Pupil_Images) {
            findViewById(R.id.upload_images).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.upload_images).setVisibility(View.INVISIBLE);
        }
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
            case R.id.upload_images:
                // zipTheFile(imagePaths());
                if (CommonUtils.isNetworkConnected(this)) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        zipThenUpload();
                    } else {
                        signInAnonymously(mAuth);
                    }

                } else {
                    CommonUtils.initiateNetworkOptions(this, this, "applicable");
                }
                break;
        }
    }

    private void signInAnonymously(FirebaseAuth mAuth) {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(AdminProfileActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                Log.e("signInAnonymously", "Success");
                zipThenUpload();
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AdminProfileActivity.this, "Authentication Failed Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
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
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
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
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    void zipThenUpload() {
        File file = new File(Constants.AVA_ZIP_FOLDER);
        if (!file.exists())
            file.mkdirs();
        boolean bool = zipFileAtPath(Constants.AVA_IMG_FOLDER, Constants.AVA_ZIP_FOLDER + "images.zip");
        Log.e("zipThenUpload", " " + bool);
    }

    /*
     *
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     */

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage());
            Toast.makeText(this, "Problem in creating zip file", Toast.LENGTH_LONG).show();
            return false;
        }
        Toast.makeText(this, "Zip file created successfully", Toast.LENGTH_LONG).show();
        uploadZip();
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    void uploadZip() {
        Toast.makeText(this, "Uploading Zip file...", Toast.LENGTH_LONG).show();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        File file = new File(Constants.AVA_ZIP_FOLDER + "images.zip");
        StorageReference mismatchFileRef = storageRef.child(CommonUtils.getZipFilePath());
        Uri uri = Uri.fromFile(file);

        UploadTask uploadTask = mismatchFileRef.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Exception", " " + exception.getMessage());
                Toast.makeText(AdminProfileActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                showRequirementDialog(AdminProfileActivity.this, "Images uploading failed..", false);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("OnSuccess", "of storage " + taskSnapshot.getMetadata().getName());
                Toast.makeText(AdminProfileActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                showRequirementDialog(AdminProfileActivity.this, "Images Uploaded Successfully..", true);
            }
        });
    }

    void showRequirementDialog(Context context, String message, boolean canBeDeleted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Message!");
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, which) -> {
                    if (canBeDeleted)
                        deleteFiles();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void deleteFiles() {
        File imageDirectory = new File(Constants.AVA_IMG_FOLDER);
        for (File tempFile : imageDirectory.listFiles()) {
            boolean imagesDeleted = tempFile.delete();
            Log.e("imagesDeleted", " " + imagesDeleted);
        }
        File zip = new File(Constants.AVA_ZIP_FOLDER);
        for (File tempFile : zip.listFiles()) {
            boolean zipDeleted = tempFile.delete();
            Log.e("imagesDeleted", " " + zipDeleted);
        }
    }
}

