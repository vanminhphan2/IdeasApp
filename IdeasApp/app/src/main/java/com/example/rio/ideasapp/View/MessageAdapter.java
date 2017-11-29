package com.example.rio.ideasapp.View;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by User on 11/11/2017.
 */

public class MessageAdapter extends RecyclerView.ViewHolder {

    DatabaseReference mdata;
    View messview;


    public MessageAdapter(View itemView) {
        super(itemView);
        messview = itemView;
        mdata = FirebaseDatabase.getInstance().getReference().child("Accounts").child("Users");
    }

    public void setuid(String Uid)
    {
        final TextView mess_name = (TextView)messview.findViewById(R.id.txtmessname4);
        mdata.child(Uid).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mess_name.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setImage( Activity context, String Uid)
    {

        final ImageView mess_img = (ImageView)messview.findViewById(R.id.messavatar4);
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final String[] dataimg = {"null"};
        final Activity finalContext = context;
        mdata.child(Uid).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataimg[0] = dataSnapshot.getValue().toString();
                // Reference to an image file in Firebase Storage
                if (dataimg[0] == "null")
                {
                    mess_img.setBackgroundResource(R.drawable.default_avatar);
                }
                else  {
                    StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                    // Load the image using Glide - load image from data to image view

                    Glide.with(finalContext)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(mess_img);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public String setView(String view)
    {
        view = "false";
        return view;
    }
}

