package com.example.rio.ideasapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.MenuAction.UsersProfile;
import com.example.rio.ideasapp.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by User on 10/31/2017.
 */

public class CreateNewPost extends Activity{
    Button post, addimg, cancel;
    ImageView imgavatar, imgcb;
    TextView username;
    EditText txtcontent;
    Spinner spinner;

    //
    private Uri imgUri2;
    private final int PICK_IMAGE = 100;
    private  boolean value = false;
    //
    private String idPostImage = UUID.randomUUID().toString();

    //Firebase
    DatabaseReference mData;
    DatabaseReference mdata;
    FirebaseAuth mAuth;
    private String user;
    StorageReference storageReference;
    FirebaseStorage storage;
    //
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_newpost);


        //Firebase
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        user = (User.getEmail().replace(".",""));
        mdata = FirebaseDatabase.getInstance().getReference().child("ListPost");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //
        progressDialog = new ProgressDialog(this);

        //declare image avatar
        // create storage reference
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //declare image avatar

        final String[] dataimg = {null};
        mData.child("Users").child(user).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataimg[0] = dataSnapshot.getValue().toString();
                imgavatar = (ImageView)findViewById(R.id.imgavar);
                // Reference to an image file in Firebase Storage
                if (dataimg != null ) {
                    StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                    // Load the image using Glide - load image from data to image view
                    Glide.with(CreateNewPost.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(imgavatar);
                }
                if(dataimg[0] == "null")
                {
                    imgavatar.setImageResource(R.drawable.default_avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //declare displayname
        mData.child("Users").child(user).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nametemp = dataSnapshot.getValue().toString();
                username = (TextView)findViewById(R.id.txtname);
                username.setText(nametemp);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //spiner list chu de
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spadapter = ArrayAdapter.createFromResource(this, R.array.chude_array, android.R.layout.simple_spinner_dropdown_item);
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spadapter);
        //declare edit text
        txtcontent = (EditText)findViewById(R.id.edcontent);

        //
        imgcb = (ImageView)findViewById(R.id.imgadd);


        // declare post
        post = (Button)findViewById(R.id.btndang);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushnewpost();
                Intent back = new Intent(CreateNewPost.this, UsersProfile.class);
                startActivity(back);
            }
        });



        //declare add image or video
        addimg = (Button)findViewById(R.id.btnadd);
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosimage();
            }
        });

        //declare cancel
        cancel = (Button)findViewById(R.id.btncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtoprofile = new Intent(CreateNewPost.this, UsersProfile.class);
                startActivity(backtoprofile);
            }
        });





    }

    //push new post
    public void pushnewpost()
    {
        progressDialog.setMessage("Please wait to posting...");
        progressDialog.show();
        final String us = user.toString();
        final String content = txtcontent.getText().toString();
        final String img_value = "null";
        final String usname = username.getText().toString();
        final String chude = spinner.getSelectedItem().toString();
        final String sum = "0";

        if (content != null)
        {
            if (imgUri2 != null) {
                StorageReference refstore = storageReference.child("PostImages/").child(imgUri2.getLastPathSegment());
                refstore.putFile(imgUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        DatabaseReference post = mdata.push();
                        post.child("Content").setValue(content);
                        post.child("Image").setValue(downloadUri.toString());
                        post.child("User").setValue(us);
                        post.child("UserName").setValue(usname);
                        post.child("ChuDe").setValue(chude);
                        post.child("Sum").setValue(sum);
                    }
                });
            }
            else {
                DatabaseReference post = mdata.push();
                post.child("Content").setValue(content);
                post.child("Image").setValue(img_value);
                post.child("User").setValue(us);
                post.child("UserName").setValue(usname);
                post.child("ChuDe").setValue(chude);
            }
        }
        // chua xong dieu kien
        else {
            Toast.makeText(CreateNewPost.this,"Them noi dung hoac hinh anh de post", Toast.LENGTH_SHORT ).show();
        }

    }

    //chọn ảnh đại
    private void choosimage()
    {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, ("Select Image")), PICK_IMAGE);
    }
    //result chọn ảnh đại
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == PICK_IMAGE) {
                imgUri2 = data.getData();
                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgcb.setImageBitmap(bm);
            }
        }
    }
}
