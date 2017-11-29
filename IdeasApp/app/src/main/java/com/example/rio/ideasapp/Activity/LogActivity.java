package com.example.rio.ideasapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rio.ideasapp.MainActivity;
import com.example.rio.ideasapp.Model.Status;
import com.example.rio.ideasapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogActivity extends Activity {

    EditText edtusername;
    EditText edtassword;
    TextView txtRegis;
    Button btnlogin;

    Status status;



    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    FirebaseUser aduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log);

        mAuth = FirebaseAuth.getInstance();
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");

        Anhxa();
        txtRegis.setPaintFlags(txtRegis.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }
    // function anh xa
    public void Anhxa()
    {
        edtusername = (EditText) findViewById(R.id.edtusername);
        edtassword = (EditText) findViewById(R.id.edtpassword);
        txtRegis = (TextView) findViewById(R.id.txtRegister);
        btnlogin = (Button) findViewById(R.id.btnlogin);
    }

    //set status on for user
    public void setstatus(FirebaseUser user)
    {
        String s = (user.getEmail().replace(".", ""));
        mData.child("Users").child(s).child("usTrangthai").setValue(status.setOn);
    }
    private void Login()
    {
        final String email= edtusername.getText().toString();
        String password= edtassword.getText().toString();
        if(email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(LogActivity.this,"Không bỏ trống ô nào!",Toast.LENGTH_LONG).show();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (email.replace(".", "").equals("admin@gmailcom"))
                                {
                                    Toast.makeText(LogActivity.this,"ADMIN !", Toast.LENGTH_LONG).show();
                                    // intent to class MainActivity
                                    Intent tomain = new Intent(LogActivity.this, MainActivity.class);
                                    tomain.putExtra("name", email);
                                    startActivity(tomain);
                                }
                                else {
                                    // intent to class HelloActivity

                                    Toast.makeText(LogActivity.this,"Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                                    setstatus(user);
                                    Intent intent = new Intent(LogActivity.this, HelloActivity.class);
                                    intent.putExtra("name", email);
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(LogActivity.this,"Đăng nhập thất bại!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }
}
