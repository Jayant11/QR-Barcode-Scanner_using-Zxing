package com.jayant.www.qrscanner.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;
import com.jayant.www.qrscanner.Activities.MainActivity;
import com.jayant.www.qrscanner.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

//  Created by cypher on 25/9/2018

public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    // newInstance constructor for creating fragment with arguments
    public static ScanFragment newInstance() {
        ScanFragment scanFragment = new ScanFragment();
        return scanFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        scannerView = new ZXingScannerView(MainActivity.context);

        return scannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scannerView == null) {
            scannerView = new ZXingScannerView(MainActivity.context);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }


    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(ScanFragment.this);
            }
        });
        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myResult));
                startActivity(browserIntent);
            }
        });
        builder.setMessage(result.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();

        generateNoteOnSD(MainActivity.context, "history.txt", result.getText());
    }

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "QrScanner");
            if (!root.exists()) {
                root.mkdirs();
            }
            File history_file = new File(root, sFileName);
            FileWriter writer = new FileWriter(history_file, true);
            writer.append(sBody).append(System.getProperty("line.separator"));
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
