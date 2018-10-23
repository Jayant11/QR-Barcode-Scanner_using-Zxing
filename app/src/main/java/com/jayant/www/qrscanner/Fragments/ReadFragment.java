package com.jayant.www.qrscanner.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jayant.www.qrscanner.Activities.MainActivity;
import com.jayant.www.qrscanner.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;

//   Created by cypher on 25/9/2018

public class ReadFragment extends Fragment {

    // newInstance constructor for creating fragment with arguments
    public static ReadFragment newInstance() {
        ReadFragment readFragment = new ReadFragment();
        return readFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        ImageButton choose = view.findViewById(R.id.button);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int PICK_REQUEST_CODE = 0;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/");
                startActivityForResult(intent, PICK_REQUEST_CODE);
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == 0)
        {
            if (resultCode == RESULT_OK)
            {
                Uri uri = intent.getData();
                String type = intent.getType();
                Log.i("TAG","Pick completed: "+ uri + " "+type);
                if (uri != null)
                {
                    String path = uri.toString();
                    if (path.toLowerCase().startsWith("file://"))
                    {
                        // Selected file/directory path is below
                        path = (new File(URI.create(path))).getAbsolutePath();
                        File file = new File(path);
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        String decoded=scanQRImage(bitmap);
                        Log.i("QrTest", "Decoded string="+decoded);

                        alert(decoded);

                    }

                }
            }
            else Log.i("TAG","Back from pick with cancel status");
        }
    }

    public static String scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        }
        catch (Exception e) {

            Log.e("QrTest", "Error decoding barcode", e);
            contents = "Image too large. Get a close shot";
        }
        return contents;
    }

    public static Result crop(Bitmap bmap) {
        Bitmap cropedImage = Bitmap.createBitmap(bmap, 0,0,500, 500);

        // using the cropedImage instead of image
        int[] intArray = new int[cropedImage.getWidth()*cropedImage.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        cropedImage.getPixels(intArray, 0, cropedImage.getWidth(), 0, 0, cropedImage.getWidth(), cropedImage.getHeight());

        LuminanceSource source = new RGBLuminanceSource(cropedImage.getWidth(), cropedImage.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        // barcode decoding
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try
        {
            result = reader.decode(bitmap);
        }
        catch (ReaderException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public void alert(final String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        if (result.equalsIgnoreCase("Image too large. Get a close shot")) {
            //
        }
        else {
            builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                    startActivity(browserIntent);
                }
            });
        }
        builder.setMessage(result);
        AlertDialog alert1 = builder.create();
        alert1.show();

        generateNoteOnSD(MainActivity.context, "history.txt", result);
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
