package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.agyohora.mobileperitc.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import static com.agyohora.mobileperitc.utils.CommonUtils.switchOffHotSpot;

public class QrScannerActivity extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.qr_reader_syrma_layout);

        switchOffHotSpot(this);
        return (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("BarCodeCaptureActivity", " ResultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

}