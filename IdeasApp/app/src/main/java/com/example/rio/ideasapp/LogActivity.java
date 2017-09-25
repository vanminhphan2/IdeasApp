package com.example.rio.ideasapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogActivity extends AppCompatActivity {

    EditText edtusername;
    EditText edtassword;
    Button btnsignup;
    Button btnlogin;
    String username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Anhxa();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    public void Anhxa()
    {
        edtusername = (EditText) findViewById(R.id.edtusername);
        edtassword = (EditText) findViewById(R.id.edtpassword);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btnlogin = (Button) findViewById(R.id.btnlogin);
    }
}
