package com.example.pipwave.sdk.pipwaveexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.pipwave.sdk.android.PipwaveConfig;
import com.pipwave.sdk.android.common.utils.RandomString;
import com.pipwave.sdk.android.pipwave.Pipwave;
import com.pipwave.sdk.android.pipwave.PipwaveCheckout;
import com.pipwave.sdk.android.pipwave.PipwaveCheckoutCallback;
import com.pipwave.sdk.android.pipwave.model.AddressInfo;
import com.pipwave.sdk.android.pipwave.model.ApiOverride;
import com.pipwave.sdk.android.pipwave.model.BuyerInfo;
import com.pipwave.sdk.android.pipwave.model.ItemInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.pipwave.sdk.pipwaveexample.Config.API_STAGING_KEY;
import static com.example.pipwave.sdk.pipwaveexample.Config.API_STAGING_SECRET;
import static com.example.pipwave.sdk.pipwaveexample.Config.CANCEL_URL_PIPWAVE;
import static com.example.pipwave.sdk.pipwaveexample.Config.FAILURE_URL_PIPWAVE;
import static com.example.pipwave.sdk.pipwaveexample.Config.SUCCESS_URL_PIPWAVE;
import static com.example.pipwave.sdk.pipwaveexample.HeaderStyles.HEADERS;
import static com.example.pipwave.sdk.pipwaveexample.HeaderStyles.STYLES;


public class InfoActivity extends AppCompatActivity implements PipwaveCheckoutCallback {

    private PipwaveCheckout mPipwaveCheckout;

    TextView txtQty1, txtQty2, txtMarron, txtBlack;
    TextView txtSubtotal, txtShipping, txtTotal;

    String A,B,C,D,E = null;
    String email = null;
    AddressInfo address = null;
    BuyerInfo buyerInfo = null;
    ApiOverride apiOverride = null;

    int mShipping, mTotal = 0;

    List<ItemInfo> itemList2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        PipwaveConfig.setEnvironment(PipwaveConfig.ENVIRONMENT_SANDBOX);
        mPipwaveCheckout = new PipwaveCheckout(API_STAGING_KEY, this);

        txtQty1 = findViewById(R.id.txtQty1);
        txtQty2 = findViewById(R.id.txtQty2);
        txtMarron = findViewById(R.id.txtMarron);
        txtBlack = findViewById(R.id.txtBlack);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtShipping = findViewById(R.id.txtShipping);
        txtTotal = findViewById(R.id.txtTotal);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            A = extras.getString("quantity1");
            B = extras.getString("quantity2");
            C = extras.getString("totalMarron");
            D = extras.getString("totalBlack");
            E = extras.getString("subTotal");

            email = extras.getString("email");

            address = getIntent().getExtras().getParcelable("address");
        }

        txtQty1.setText(A);
        txtQty2.setText(B);
        txtMarron.setText(C);
        txtBlack.setText(D);
        txtSubtotal.setText(E);

        int s = Integer.parseInt(E);
        if(s == 0){
            mShipping = 0;
        }else if(s <= 100){
            mShipping = 10;
        }else{
            mShipping = 5;
        }
        mTotal = mShipping + s;
        txtShipping.setText(String.valueOf(mShipping));
        txtTotal.setText(String.valueOf(mTotal));
    }

    public void purchase(View view) {

        apiOverride();
        itemInfo();
        buyer();

        BigDecimal mAmount = new BigDecimal(mTotal);
        String amount = mAmount.toString();

        RandomString randomString = new RandomString();
        String txn_id = randomString.nextString();

        String currency_code = "MYR";

        BigDecimal mShip = new BigDecimal(mShipping);
        String ship = mShip.toString();

        Pipwave pipwave = new Pipwave(API_STAGING_KEY, API_STAGING_SECRET, txn_id, amount, currency_code, buyerInfo);
        pipwave.setShippingInfo(address);
        pipwave.setBillingInfo(address);
        pipwave.setItemList(itemList2);
        pipwave.setShippingAmount(ship);
        pipwave.setApiOverride(apiOverride);
        pipwave.setStyles(STYLES);
        pipwave.setHeaders(HEADERS);
        mPipwaveCheckout.execute(this, pipwave);
    }

    private void apiOverride() {

        apiOverride = new ApiOverride(CANCEL_URL_PIPWAVE, SUCCESS_URL_PIPWAVE, FAILURE_URL_PIPWAVE);
    }

    private void buyer() {

        String id = "USER007";
        buyerInfo = new BuyerInfo(id, email);
        buyerInfo.setFirst_name("zamri");
        buyerInfo.setLast_name("yusof");
        buyerInfo.setContact_no("0192901157");
    }

    private void itemInfo() {

        List<ItemInfo>itemList = new ArrayList<>();

        String name1 = "T-Shirt Marron";
        BigDecimal mItem1Amount = new BigDecimal("15");
        String Item1Amount = mItem1Amount.toString();
        BigDecimal mQty1 = new BigDecimal(Integer.parseInt(A));
        String qty1 = mQty1.toString();
        String sku1 = "CODEITEM1";
        String category1 = "T-Shirt";

        ItemInfo item1 = new ItemInfo(name1,Item1Amount,qty1,category1,sku1);
        item1.setDescription("Price RM15");
        itemList.add(item1);

        String name2 = "T-Shirt Black";
        BigDecimal mItem2Amount = new BigDecimal("10");
        String Item2Amount = mItem2Amount.toString();
        BigDecimal mQty2 = new BigDecimal(Integer.parseInt(B));
        String qty2 = mQty2.toString();
        String sku2 = "CODEITEM2";
        String category2 = "T-Shirt";

        ItemInfo item2 = new ItemInfo(name2,Item2Amount,qty2,category2,sku2);
        item2.setDescription("Price RM10");
        itemList.add(item2);

        itemList2 = itemList;

    }

    @Override
    public void onCheckoutSuccess() {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_LONG).show();
        Intent i  = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onCheckoutCanceled() {
        Toast.makeText(this, "Payment Canceled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckoutFailure(String message) {
        Toast.makeText(this, "Payment Fail : " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPipwaveCheckout.onActivityResult(requestCode, resultCode, data);
    }
}
