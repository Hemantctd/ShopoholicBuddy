package com.shopoholicbuddy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopoholicbuddy.R;
import com.shopoholicbuddy.customviews.CustomTextView;
import com.shopoholicbuddy.models.orderresponse.Result;
import com.shopoholicbuddy.utils.AppUtils;
import com.shopoholicbuddy.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class created by Sachin on 25-Apr-18.
 */
public class QRCodeActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_order_details)
    CustomTextView tvOrderDetails;
    @BindView(R.id.tv_order_number)
    CustomTextView tvOrderNumber;
    @BindView(R.id.tv_order_id)
    CustomTextView tvOrderId;
    @BindView(R.id.iv_order_product_image)
    ImageView ivOrderProductImage;
    @BindView(R.id.tv_order_product)
    CustomTextView tvOrderProduct;
    @BindView(R.id.tv_order_product_name)
    CustomTextView tvOrderProductName;
    @BindView(R.id.tv_order_date)
    CustomTextView tvOrderDate;
    @BindView(R.id.tv_order_product_date)
    CustomTextView tvOrderProductDate;
    private String qrContent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);
        initVariables();
        getProductDetails();
        setQRCode();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.QR_CODE);
        AppUtils.getInstance().openNewActivity(this, intent);
    }

    /**
     * method to initialize variables
     *
     */
    private void initVariables() {
        setSupportActionBar(layoutToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            tvTitle.setText(getString(R.string.order_placed));
        }
    }

    /**
     * method to get the product details from intent
     */
    private void getProductDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Result orderResponse = (Result) getIntent().getExtras().getSerializable(Constants.IntentConstant.ORDER_DETAILS);
            if (orderResponse != null) {
                qrContent = orderResponse.getId();
                tvOrderId.setText(orderResponse.getOrderNumber());
                tvOrderProductName.setText(orderResponse.getDealName());
                tvOrderProductDate.setText(AppUtils.getInstance().formatDate(orderResponse.getOrderDate(), Constants.AppConstant.SERVICE_DATE_FORMAT,
                        Constants.AppConstant.DATE_FORMAT));
                AppUtils.getInstance().setImages(this, orderResponse.getDealImage(), ivOrderProductImage, 0, R.drawable.ic_placeholder);
            }
        }
    }

    /**
     * method to set the qr code
     */
    private void setQRCode() {
// this is a small sample use of the QRCodeEncoder class from zxing
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, (int)getResources().getDimension(R.dimen._150sdp),
                    (int)getResources().getDimension(R.dimen._150sdp));
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivQrCode.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
