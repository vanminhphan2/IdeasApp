package com.example.rio.ideasapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder localStringBuilder = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String str;
            int j;
            if ((j = (str = Integer.toHexString(arr[i])).length()) == 1) {
                str = "0" + str;
            }
            if (j > 2) {
                str = str.substring(j - 2, j);
            }
            localStringBuilder.append(str.toUpperCase());
            if (i < arr.length - 1) {
                localStringBuilder.append(':');
            }
        }
        return localStringBuilder.toString();
    }
    public static String getSHA1(Context context) {
        String str = null;
        try {
            for (Signature sig : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures) {
                MessageDigest localMessageDigest;
                (localMessageDigest = MessageDigest.getInstance("SHA1")).update(sig.toByteArray());
                str = byte2HexFormatted(localMessageDigest.digest());
            }
        } catch (Exception e) {
            Log.e("Tag", e.toString());
        }
        return str;
    }
    DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("Tag", getSHA1(this));

        mData= FirebaseDatabase.getInstance().getReference();
        mData.child("Hoten").setValue("Rio Phan");
    }

}
