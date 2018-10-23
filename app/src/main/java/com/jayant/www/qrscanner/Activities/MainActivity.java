package com.jayant.www.qrscanner.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.jayant.www.qrscanner.Fragments.HistoryFragment;
import com.jayant.www.qrscanner.Fragments.ReadFragment;
import com.jayant.www.qrscanner.Fragments.ScanFragment;
import com.jayant.www.qrscanner.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 3;
    private PagerAdapter mPagerAdapter;

    private ViewPager vpPager;

    private Handler mHandler = new Handler();

    private Handler scrollHandler = new Handler();
    private NestedScrollView mScrollView;
    private int scroll;

    private TextView tv;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        requestPermissions();

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setOffscreenPageLimit(3);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mPagerAdapter);

    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 1 - This will show FirstFragment
                    return ScanFragment.newInstance();
                case 1: // Fragment # 2 - This will show SecondFragment
                    return ReadFragment.newInstance();
                case 2: // Fragment # 3 - This will show ThirdFragment
                    return HistoryFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: // Fragment # 1 - This will show FirstFragmentTitle
                    return "Scan Code";
                case 1: // Fragment # 2 - This will show SecondFragmentTitle
                    return "Scan Image";
                case 2: // Fragment # 3 - This will show ThirdFragmentTitle
                    return "History";
                default:
                    return null;
            }
        }

    }


    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    Log.i("r: ", "Permission has been denied by user");
                } else {
                    Log.i("r: ", "Permission has been granted by user");
                }
                return;
            }

            case 2: {
                if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    Log.i("r: ", "Permission has been denied by user");
                } else {
                    Log.i("r: ", "Permission has been granted by user");
                }
                return;
            }

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    public void browse(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
