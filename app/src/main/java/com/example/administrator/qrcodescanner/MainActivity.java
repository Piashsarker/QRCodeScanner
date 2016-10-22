package com.example.administrator.qrcodescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Bitmap myQRCode;
    private SurfaceView cameraView ;
    private TextView qrCodeInfo ,defaultQRResult;
    private CameraSource cameraSource;
    private   BarcodeDetector barcodeDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();

        barcodeDetector = new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        defaultQRCodeReader();
        qrCodeReaderUsingCamera();

    }

    private void qrCodeReaderUsingCamera() {

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();


        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    qrCodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {
                            qrCodeInfo.setText( barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

    }

    private void defaultQRCodeReader() {
        //Getting Assets From The Assets Folder And Converting It in Bitmap
        try {
            myQRCode = BitmapFactory.decodeStream(getAssets().open("qrcodeimage.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Frame myFrame = new Frame.Builder()
                .setBitmap(myQRCode)
                .build();

        SparseArray<Barcode> barcodes = barcodeDetector.detect(myFrame);

        if(barcodes.size() != 0) {

           defaultQRResult.setText(barcodes.valueAt(0).displayValue);
            Toast.makeText(this, barcodes.valueAt(0).displayValue, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeView() {

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        qrCodeInfo = (TextView) findViewById(R.id.code_info);
        defaultQRResult = (TextView) findViewById(R.id.txt_default_image_result);
    }
}
