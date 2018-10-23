package com.jayant.www.qrscanner.billing;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;


public class BillingManager implements PurchasesUpdatedListener {

    private BillingClient mBillingClient;
    private Activity mActivity;

    public BillingManager(Activity activity) {
        mActivity = activity;
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                } else {
                    Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }
    private static final String TAG = "BillingManager";

    public BillingManager(Context context) {
    }

    public void startPurchaseFlow(String skuId, String billingType) {
        // TODO: Implement launch billing flow here
    }

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode, List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated() response: " + responseCode);
    }
}
