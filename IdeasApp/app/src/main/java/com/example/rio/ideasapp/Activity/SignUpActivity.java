package com.example.rio.ideasapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rio.ideasapp.Model.Status;
import com.example.rio.ideasapp.Model.User;
import com.example.rio.ideasapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends Activity {

    EditText edtusername;
    EditText edtassword;
    Button btnsignup;
    Button btnback;

    Status status;

    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");

        // execute funtion register and login
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

    // create funtion Anhxa
    public void Anhxa()
    {
        edtusername = (EditText) findViewById(R.id.edtusername);
        edtassword = (EditText) findViewById(R.id.edtpassword);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btnback = (Button) findViewById(R.id.btnback);
    }

    //create user
    private void CreateNewUser(FirebaseUser user)
    {
        String Email = user.getEmail();
        String x = Email.replace(".","");
        User u = new User(Email,status.setOFF,false);
        mData.child("Users").child(x).setValue(u);
    }

    private void SignUp()
    {
        String email= edtusername.getText().toString();
        String password= edtassword.getText().toString();
        //check valid isEmpty
        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(SignUpActivity.this,"Không bỏ trống ô nào!",Toast.LENGTH_LONG).show();
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //show toast message success
                                CreateNewUser(task.getResult().getUser());
                                Toast.makeText(SignUpActivity.this,"Đăng kí thành công!", Toast.LENGTH_LONG).show();
                                // come back form login when register successful
                                Intent backlogin = new Intent(SignUpActivity.this, LogActivity.class);
                                startActivity(backlogin);
                            } else {
                                // show toast message not success
                                Toast.makeText(SignUpActivity.this,"Đăng ký thất bại!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
