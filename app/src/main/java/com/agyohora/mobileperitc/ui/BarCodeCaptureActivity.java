package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import static com.agyohora.mobileperitc.utils.CommonUtils.switchOffHotSpot;

/**
 * Created by Invent on 23-1-18.
 *
 * @see CaptureActivity
 * Actvity to scan barcode
 */

public class BarCodeCaptureActivity extends CaptureActivity {
    Button serviceLoginButton;

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.qr_reader_layout);
        serviceLoginButton = findViewById(R.id.serviceLogin);

        switchOffHotSpot(this);
        return (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommonUtils.isProductionSetUpFinished(this))
            serviceLoginButton.setVisibility(View.VISIBLE);
        else
            serviceLoginButton.setVisibility(View.GONE);
    }

    public void showDialog(View view) {
        CommonUtils.showContactDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("BarCodeCaptureActivity", " ResultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickCallback(final View v) {
        if (CommonUtils.isNetworkConnected(this)) {
            startActivity(new Intent(this, ServiceLogin.class));
        } else {
            CommonUtils.initiateNetworkOptions(this, this, "not_applicable");
        }
    }
}