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
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.rio.ideasapp.MainActivity;
import com.example.rio.ideasapp.Model.DanhMuc;
import com.example.rio.ideasapp.R;
import com.example.rio.ideasapp.View.CustomView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by User on 10/19/2017.
 */

public class FirstEdit extends Activity {

    ImageView avatar;
    EditText hoten, thanhpho;
    RadioButton rbnam, rbnu;
    private String value;
    String idImage = UUID.randomUUID().toString();


    CircularProgressButton circlebutton;
    CustomView adapter;


    private final int PICK_IMAGE = 100;
    private Uri imgUri;

    //firebase
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;

    //khởi tạo danh sách
    String[] list = {"Khoa học", "Thiên nhiên", "Cuộc sống", "Vũ khí","Xe"};
    Integer[] images = {R.drawable.img_khoahoc, R.drawable.img_thiennhien,R.drawable.img_cuocsong,R.drawable.img_vukhi,
            R.drawable.img_xe};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_edit);

        //firebase
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //declare
        hoten = (EditText) findViewById(R.id.edthoten);
        thanhpho = (EditText) findViewById(R.id.edtTp);
        rbnam = (RadioButton) findViewById(R.id.nam);
        rbnu = (RadioButton) findViewById(R.id.nu);
        avatar = (ImageView) findViewById(R.id.imgavatar);

        //avatar event click
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosimageprofile();
            }
        });

        //gridview danh muc
        DanhMuc danhMuc;
        ArrayList<DanhMuc> listdm = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            danhMuc = new DanhMuc();
            danhMuc.setName(list[i]);
            danhMuc.setShowcheck(false);
            listdm.add(danhMuc);
        }
        final GridView showgrid = (GridView) findViewById(R.id.grid);
        adapter = new CustomView(this, R.layout.list_item, listdm);
        showgrid.setAdapter(adapter);




            //circle button
            circlebutton = (CircularProgressButton) findViewById(R.id.circlebtn);
            circlebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AsyncTask<String, String, String> capnhat = new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return "done";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if (s.equals("done")) {
                                uploadImage();
                                FirebaseUser user = mAuth.getCurrentUser();
                                setprofile(user);
                                Toast.makeText(FirstEdit.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                circlebutton.doneLoadingAnimation(Color.parseColor("#FF51F430"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));

                            }
                        }
                    };
                    circlebutton.startAnimation();
                    capnhat.execute();
                    // dừng 2.5s để button cập nhật chạy hết
                    Thread time = new Thread() {
                        public void run() {
                            try {
                                sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                Intent tomaina = new Intent(FirstEdit.this, MainActivity.class);
                                //tomaina.putExtra("UsernameDN", packageFromCaller);
                                startActivity(tomaina);
                            }
                        }
                    };
                    time.start();
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

    // set profile value
    public void setprofile(FirebaseUser user)
    {
        String us = (user.getEmail().replace(".",""));
        String name = hoten.getText().toString();
        String tp = thanhpho.getText().toString();
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

        mData.child("Users").child(us).child("Profiles").child("Hoten").setValue(name);
        mData.child("Users").child(us).child("Profiles").child("Diachi").setValue(tp);
        mData.child("Users").child(us).child("Profiles").child("Gioitinh").setValue(gioitinh);
        mData.child("Users").child(us).child("Profiles").child("Avatar").setValue(img);
        mData.child("Users").child(us).child("usInfo").setValue(true);
    }

    //chọn ảnh đại diện
    private void choosimageprofile()
    {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, ("Select Image")), PICK_IMAGE);
    }
    //result chọn ảnh đại diện
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
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
}
