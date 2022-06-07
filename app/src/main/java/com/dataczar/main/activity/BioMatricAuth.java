/*
 * Copyright (c)  to Samrt Sense . Ai on 2022.
 */

package com.dataczar.main.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.dataczar.R;

import java.util.concurrent.Executor;

public class BioMatricAuth  extends AppCompatActivity {
    TextView fingerprint_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_layout_dialog);
        fingerprint_text = findViewById(R.id.fingerprint_error);
        
        BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL))
        {
            // this means we can use biometric sensor
            case BiometricManager.BIOMETRIC_SUCCESS:
                fingerprint_text.setText("You can use the fingerprint sensor to login");
                fingerprint_text.setTextColor(Color.parseColor("#fafafa"));
                break;

            // this means that the device doesn't have fingerprint sensor
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                fingerprint_text.setText("This device doesnot have a fingerprint sensor");
                fingerprint_text.setVisibility(View.GONE);
                break;

            // this means that biometric sensor is not available
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                fingerprint_text.setText("The biometric sensor is currently unavailable");
                fingerprint_text.setVisibility(View.GONE);
                break;
            // this means that the device doesn't contain your fingerprint
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                fingerprint_text.setText("Your device doesn't have fingerprint saved,please check your security settings");
                fingerprint_text.setVisibility(View.GONE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + biometricManager.canAuthenticate());
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        // this will give us result of AUTHENTICATION
        final BiometricPrompt biometricPrompt = new BiometricPrompt(BioMatricAuth.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                fingerprint_text.setText("Login Successful");
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Login to Dataczar")
                .setDescription("Verify Identity \\nTouch your finger on sensor to login").setNegativeButtonText("Cancel").build();


        fingerprint_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }

}
