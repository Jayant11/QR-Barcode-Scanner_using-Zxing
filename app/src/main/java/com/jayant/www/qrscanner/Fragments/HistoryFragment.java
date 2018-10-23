package com.jayant.www.qrscanner.Fragments;

//   Created by cypher on 25/9/2018

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.jayant.www.qrscanner.Activities.MainActivity;
import com.jayant.www.qrscanner.R;
import com.jayant.www.qrscanner.purchaseUtils.IabHelper;
import com.jayant.www.qrscanner.purchaseUtils.IabResult;
import com.jayant.www.qrscanner.purchaseUtils.Inventory;
import com.jayant.www.qrscanner.purchaseUtils.Purchase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.jayant.www.qrscanner.purchaseUtils.IabHelper.BILLING_RESPONSE_RESULT_OK;
import static com.jayant.www.qrscanner.purchaseUtils.IabHelper.RESPONSE_BUY_INTENT;
import static com.jayant.www.qrscanner.purchaseUtils.IabHelper.RESPONSE_CODE;

public class HistoryFragment extends Fragment {

    int count = 0;

    SwipeRefreshLayout swipeRefreshLayout;

    ListView listView;
    String[] history;

    InputStream inputStreamCounter;
    BufferedReader bufferedReaderCounter;

    InputStream inputStreamLoader;
    BufferedReader bufferedReaderLoader;

    ArrayAdapter<String> adapter;

    private static final String TAG = "com.jayant.billing :  ";
    public static IabHelper mHelper;
    public static IInAppBillingService mService;
    static final String ITEM_SKU = "android.jayant.qrscanner_history_purchase";

    static Button buy;
    String deviceId;

    // newInstance constructor for creating fragment with arguments
    public static HistoryFragment newInstance() {
        HistoryFragment historyFragment = new HistoryFragment();
        return historyFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupBilling();

    }

    private void setupBilling() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsOx97GxH5q3+gN9M5Un9CgD5wSXigUEhRnK0et29vQ3p3HssXuW5fT5tBaJdwE8HO7AhTX6bOID+2TY94lthLhtYLKS5aK0TV3nR+KLTb+NulrUDICzuE9McnnVNlNh6HZO/zpnDRXMgaksqPHg2LobVuyZBNt7oNxn/QT89HXEpjFrXdcSIhR5/NWXqnx3FhGYMdePaRFy1Mwz5cDGj/b1UaQENQcMpZG/CluwZqiDssvUICqa4EFYDA2lDNqwUtxR7Rxptkt7RNadC4+YqJSfvlm+FYTrbCbwDB+vOe78JzYv+Nb/Ek3f7pEWmcGzHfbmk0hW/3u0Oglu02PsfKQIDAQAB";
        mHelper = new IabHelper(MainActivity.context, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });

        /*try {
            Bundle activeSubs = mService.getPurchases(2, "com.jayant.www.qrscanner","subs", null);
            if (activeSubs == null){
                buy.setVisibility(View.VISIBLE);
            }
            else {
                buy.setVisibility(View.GONE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listView = view.findViewById(R.id.listView);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        record();


        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, history);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                final String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemValue));
                getActivity().startActivity(browserIntent);

                // Show Alert
                //Toast.makeText(getActivity(), "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();

            }

        });

        buy = view.findViewById(R.id.button);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });


        return view;
    }

    private void record() {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "QrScanner");
            if (!root.exists()) {
                root.mkdirs();
            }
            File history_file = new File(root, "history.txt");
            inputStreamCounter = new FileInputStream(history_file);
            bufferedReaderCounter = new BufferedReader(new InputStreamReader(inputStreamCounter));

            inputStreamLoader = new FileInputStream(history_file);
            bufferedReaderLoader = new BufferedReader(new InputStreamReader(inputStreamLoader));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (bufferedReaderCounter.readLine()!=null) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        history = new String[count];

        try {
            for (int i=0; i<count; i++) {
                history[i]  = bufferedReaderLoader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshList() {
        count = 0;
        inputStreamCounter = null;
        inputStreamLoader = null;
        bufferedReaderCounter = null;
        bufferedReaderLoader = null;
        record();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, history);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setupBilling();

        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(MainActivity.context, "Refreshed!", Toast.LENGTH_SHORT).show();
        Log.d("length", String.valueOf(history.length));
        for (String member : history){
            Log.i("Member name: ", member);
        }
    }

    public void buy() {
        try {
            mHelper.launchPurchaseFlow(getActivity(), ITEM_SKU, 10001, mPurchaseFinishedListener, "history_purchase_token");

        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                Log.d("c:", " Purchase Failed ");
            }
            else if ( (purchase.getSku().equals(ITEM_SKU)) ) {

                Log.d(TAG, "successful");

                //Consume purchase
                try {
                    consumeItem();
                    buy.setVisibility(View.GONE);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    public static void consumeItem() throws IabHelper.IabAsyncInProgressException {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    static IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    static IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                Log.d("c:", "consumed");
            } else {
                // handle error
                Log.d("c:", "Error consuming purchase");
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }


}
