package com.example.rio.ideasapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;


public class MainActivity extends AppCompatActivity {

    ImageView menu;
    ImageView search;
    ImageView message;
    ImageView user;
    EditText input;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

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


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Log.e("Tag", getSHA1(this));

        mData= FirebaseDatabase.getInstance().getReference();
        mData.child("Hoten").setValue("Rio Phan");
        final NavigationView navmenu = (NavigationView)findViewById(R.id.menuview);
        navmenu.setItemIconTintList(null);



        // drawer menu
        drawerLayout = (DrawerLayout) findViewById(R.id.layout_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close );
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // toolbar menu
        /*Toolbar menubar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(menubar);
        menu = (ImageView)findViewById(R.id.menu);
        search = (ImageView)findViewById(R.id.search);
        message = (ImageView)findViewById(R.id.message);
        user = (ImageView)findViewById(R.id.user);
        input = (EditText)findViewById(R.id.edtinput);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navmenu.setVisibility(View.VISIBLE);
            }
        });*/
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

}
