package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class ForgotBarCodeCaptureActivity extends CaptureActivity {
    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.forget_admin_password_qr);
        return (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);

    }

    public void showDialog(View view) {
        CommonUtils.showContactDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("BarCodeCaptureActivity", " ResultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }
}