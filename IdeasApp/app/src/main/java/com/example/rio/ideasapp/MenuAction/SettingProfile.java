package com.example.rio.ideasapp.MenuAction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.MainActivity;
import com.example.rio.ideasapp.Model.DanhMuc;
import com.example.rio.ideasapp.R;
import com.example.rio.ideasapp.View.CustomView;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by User on 10/15/2017.
 */

public class SettingProfile extends Activity {
    ImageView coverimage;
    ImageView avatar;
    ImageView update;
    EditText name, diachi;
    Button back;
    RadioButton rbnam, rbnu;
    String idImage = UUID.randomUUID().toString();

    CustomView adapter;

    CircularProgressButton circlebutton;

    // biến chọn hình
    private final int PICK_IMAGE = 100;
    private Uri imgUri;

    //firebase
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String user;

    //khởi tạo danh sách
    String[] list = {"Khoa học", "Thiên nhiên", "Cuộc sống", "Vũ khí","Xe"};
    Integer[] images = {R.drawable.img_khoahoc, R.drawable.img_thiennhien,R.drawable.img_cuocsong,R.drawable.img_vukhi,
            R.drawable.img_xe};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.setting_profile);



        // create var
        Toolbar toolbarmenu = (Toolbar)findViewById(R.id.tbuser);
        coverimage = (ImageView)findViewById(R.id.coverimage);
        avatar = (ImageView)findViewById(R.id.imgavatar);
        rbnam = (RadioButton) findViewById(R.id.nam);
        rbnu = (RadioButton) findViewById(R.id.nu);

        // get value from data




        back = (Button)findViewById(R.id.btnback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toback = new Intent(SettingProfile.this, MainActivity.class);
                startActivity(toback);
            }
        });

        update = (ImageView)findViewById(R.id.updateimg);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosimageprofile();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        //firebase
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseUser UserC = mAuth.getCurrentUser();
        user = (UserC.getEmail().replace(".",""));

        //gridview bai dang
        DanhMuc danhMuc;
        ArrayList<DanhMuc> listdm = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            danhMuc = new DanhMuc();
            danhMuc.setName(list[i]);
            // get value from fire base and set check if Title is checked in here
            danhMuc.setShowcheck(false);
            listdm.add(danhMuc);
        }
        final GridView showgridview = (GridView) findViewById(R.id.listdmuc);
        adapter = new CustomView(this, R.layout.list_item, listdm);
        showgridview.setAdapter(adapter);

        //circle button
        circlebutton = (CircularProgressButton)findViewById(R.id.circlebtn);
        circlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<String,String,String> capnhat = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        try{
                            Thread.sleep(2500);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if (s.equals("done"))
                        {
                            uploadImage();
                            FirebaseUser user = mAuth.getCurrentUser();
                            setprofile(user);
                            Toast.makeText(SettingProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            circlebutton.doneLoadingAnimation(Color.parseColor("#FF51F430"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                        }
                    }


                };
                circlebutton.startAnimation();
                capnhat.execute();
            }
        });

        //get value gioi tinh
        mData.child("Users").child(user).child("Profiles").child("Gioitinh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String gioitinh = dataSnapshot.getValue().toString();

                if (gioitinh == "Nam")
                {
                    rbnam.setChecked(true);
                }
                if (gioitinh == "Nữ"){
                    rbnu.setChecked(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //get value for displayname from firebase

        mData.child("Users").child(user).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String disname = dataSnapshot.getValue().toString();
                name = (EditText)findViewById(R.id.editname);
                name.setText(disname);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get value for diachi from firebase

        mData.child("Users").child(user).child("Profiles").child("Diachi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String thanhpho = dataSnapshot.getValue().toString();
                diachi = (EditText)findViewById(R.id.editdiachi);
                diachi.setText(thanhpho);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        // create storage reference
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //declare image avatar

        final String[] dataimg = {null};
        mData.child("Users").child(user).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataimg[0] = dataSnapshot.getValue().toString();
                avatar = (ImageView)findViewById(R.id.imgavatar);
                // Reference to an image file in Firebase Storage
                if (dataimg != null ) {
                    StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                    // Load the image using Glide - load image from data to image view
                    Glide.with(SettingProfile.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //upload image
    public void uploadImage()
    {
        if (imgUri != null)
        {
            StorageReference ref = storageReference.child("images/" + idImage);
            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }


    }


    // setting profile
    public void setprofile(FirebaseUser user)
    {
        String us = (user.getEmail().replace(".",""));
        String ten = name.getText().toString();
        String tp = diachi.getText().toString();
        String gioitinh;
        if (rbnam.isChecked() == true)
        {
            gioitinh = rbnam.getText().toString();
        }
        else{
            gioitinh = rbnu.getText().toString();
        }
        String img;
        if (imgUri != null)
        {
            img = idImage;
        }
        else {
            img = "null";
        }

        //set value
        mData.child("Users").child(us).child("Profiles").child("Hoten").setValue(ten);
        mData.child("Users").child(us).child("Profiles").child("Diachi").setValue(tp);
        mData.child("Users").child(us).child("Profiles").child("Gioitinh").setValue(gioitinh);
        mData.child("Users").child(us).child("Profiles").child("Avatar").setValue(img);
        mData.child("Users").child(us).child("usInfo").setValue(true);
    }
    //choose image for avatar
    private void choosimageprofile()
    {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, ("Select Image")), PICK_IMAGE);
    }
    //choose image for coverimage
    private void chooseImage()
    {
        Intent chooseimage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseimage.putExtra("crop","true");
        chooseimage.putExtra("aspectX",1);
        chooseimage.putExtra("aspectY",1);
        chooseimage.putExtra("outputX",420);
        chooseimage.putExtra("outputY",205);
        chooseimage.putExtra("return-data", true);
        startActivityForResult(chooseimage,2);

    }

    @Override
    // funcion result for chooseimage
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == 2 && data != null) {
                Bundle extras = data.getExtras();
                Bitmap img = extras.getParcelable("data");
                coverimage.setImageBitmap(img);
            }
            if (requestCode == PICK_IMAGE) {
                imgUri = data.getData();
                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                avatar.setImageBitmap(bm);
            }
        }
    }
    //custom gridview

}
