package com.example.rio.ideasapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.example.rio.ideasapp.MainActivity;
import com.example.rio.ideasapp.MenuAction.FirstEdit;
import com.example.rio.ideasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by vanmi on 29/09/2017.
 */

public class HelloActivity extends Activity{

    private String info = null;
    private String user;

    //firebase
    DatabaseReference mData;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hello);



        // declare data
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        user = (User.getEmail().replace(".",""));

        Thread timer = new Thread(){
            public void run()
            {
                try {
                    sleep(2300);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }finally {
                    mData.child("Users").child(user).child("usInfo").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            info = dataSnapshot.getValue().toString();
                            chuyen(info);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
            }
        };
        timer.start();
    }

    public void chuyen(String text)
    {
        if (text == "true")
        {
            Intent chuyen = new Intent(HelloActivity.this, MainActivity.class);
            //chuyen.putExtra("UsernameDN", packageFromCaller);
            startActivity(chuyen);
        }
        if (text == "false")
        {
            Intent chuyen = new Intent(HelloActivity.this, FirstEdit.class);
            //chuyen.putExtra("UsernameDN", packageFromCaller);
            startActivity(chuyen);
        }
    }
}
