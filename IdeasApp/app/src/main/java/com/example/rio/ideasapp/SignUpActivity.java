package com.example.rio.ideasapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText edtusername;
    EditText edtassword;
    Button btnsignup;
    Button btnback;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });
    }

    public void Anhxa()
    {
        edtusername = (EditText) findViewById(R.id.edtusername);
        edtassword = (EditText) findViewById(R.id.edtpassword);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btnback = (Button) findViewById(R.id.btnback);
    }
    private void SignUp()
    {
        String email= edtusername.getText().toString();
        String password= edtassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,"Đăng kí thành công!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this,"Lỗi", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
