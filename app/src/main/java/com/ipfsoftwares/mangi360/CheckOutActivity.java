package com.ipfsoftwares.mangi360;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CheckOutActivity extends AppCompatActivity {
    public static String EXTRA_CHECK_OUT_PRODUCTS = "EXTRA_CHECK_OUT_PRODUCTS";
    private ImageView mBarcodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        Bundle extras = getIntent().getExtras();
        mBarcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);

        showBarCode(extras.getString(EXTRA_CHECK_OUT_PRODUCTS));
    }

    private void showBarCode(String checkoutDetails) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(checkoutDetails, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            mBarcodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
