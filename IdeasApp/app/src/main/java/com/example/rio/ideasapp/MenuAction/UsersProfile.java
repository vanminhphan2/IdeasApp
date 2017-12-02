package com.example.rio.ideasapp.MenuAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.Activity.CreateNewPost;
import com.example.rio.ideasapp.Activity.Detail;
import com.example.rio.ideasapp.MainActivity;
import com.example.rio.ideasapp.Model.BaiDang;
import com.example.rio.ideasapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.example.rio.ideasapp.R.id.listrp;


/**
 * Created by User on 10/11/2017.
 */

public class UsersProfile extends Activity
{
    private ImageView avatar;
    private Button setting, back, create;
    private TextView displayname, follow, so;

    private RecyclerView recyclerView;
    private Query userquery;
    private  boolean Rating = false, report = false;
    private int i=0;
    public static ArrayAdapter<String> rpadapter;
    String[] listrep = {"Report", "Delete"};


    //firebase
    DatabaseReference mData, postdata ;
    FirebaseAuth mAuth;
    String user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.user_profile);

        //


        //Firebase
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        postdata = FirebaseDatabase.getInstance().getReference().child("ListPost");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        user = (User.getEmail().replace(".",""));
        userquery = postdata.orderByChild("User").equalTo(user);

        // create var
        Toolbar toolbarmenu = (Toolbar)findViewById(R.id.tbuser);


        //displayname
        mData.child("Users").child(user).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nametemp = dataSnapshot.getValue().toString();
                displayname = (TextView)findViewById(R.id.txtdisplayname);
                displayname.setText(nametemp);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        so = (TextView)findViewById(R.id.txtsoluong);
        follow = (TextView)findViewById(R.id.txtfollow);


        //xử lý button tạo bài đăng (chưa có layout- hôm sau làm)
        create = (Button)findViewById(R.id.btncreate);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocreatepost = new Intent(UsersProfile.this, CreateNewPost.class);
                startActivity(tocreatepost);
            }
        });
        //xử lý button quay lại
        back = (Button)findViewById(R.id.btnback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backhome = new Intent(UsersProfile.this, MainActivity.class);
                startActivity(backhome);
            }
        });
        // xử lý button setting
        setting = (Button) findViewById(R.id.btnsetting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tosetting = new Intent(UsersProfile.this, SettingProfile.class);
                startActivity(tosetting);
            }
        });

        //declare image avatar
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
                    Glide.with(UsersProfile.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(avatar);
                }
                if (dataimg[0] == "null")
                {
                    avatar.setImageResource(R.drawable.default_avatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // item list bai dang
        recyclerView = (RecyclerView)findViewById(R.id.listbaidang);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listrep );

    }

    // run view
    @Override
    protected  void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<BaiDang, BaiDangViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BaiDang, BaiDangViewHolder>(

                BaiDang.class,R.layout.list_baidang,BaiDangViewHolder.class,userquery
        ) {
            @Override
            protected void populateViewHolder(final BaiDangViewHolder viewHolder, final BaiDang model, int position) {
                final String post_key = getRef(position).getKey();

                String value = model.getImage().toString();
                if (value == "null")
                {
                    viewHolder.setContent(model.getContent(), 30, View.TEXT_ALIGNMENT_CENTER);
                    viewHolder.setUserName(model.getUserName());
                    viewHolder.setImage(getApplicationContext(), model.getImage(),View.GONE);
                    viewHolder.setChuDe(model.getChuDe());
                    viewHolder.setRating(post_key, user);
                    viewHolder.setSum(model.getSum());

                }
                else {
                    viewHolder.setContent(model.getContent(), 18, View.TEXT_ALIGNMENT_TEXT_START);
                    viewHolder.setUserName(model.getUserName());
                    viewHolder.setImage(getApplicationContext(), model.getImage(), View.VISIBLE);
                    viewHolder.setChuDe(model.getChuDe());
                    viewHolder.setRating(post_key, user);
                    viewHolder.setSum(model.getSum());

                }
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent todetail = new Intent(UsersProfile.this, Detail.class);
                        todetail.putExtra("bd_id", post_key);
                        startActivity(todetail);
                    }
                });
                viewHolder.rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Rating = true;
                        postdata.child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (Rating) {
                                    if (dataSnapshot.hasChild("Ratings")) {
                                        if (dataSnapshot.child("Ratings").hasChild(user)) {
                                            String sum =  dataSnapshot.child("Sum").getValue().toString();
                                            i = Integer.parseInt(sum)-1;
                                            String nextsum = String.valueOf(i);
                                            postdata.child(post_key).child("Ratings").child(user).removeValue();
                                            postdata.child(post_key).child("Sum").setValue(nextsum);
                                            viewHolder.setRating(post_key, user);
                                            Rating =false;
                                        } else {
                                            String sum =  dataSnapshot.child("Sum").getValue().toString();
                                            i = Integer.parseInt(sum) +1;
                                            String nextsum = String.valueOf(i);
                                            postdata.child(post_key).child("Ratings").child(user).setValue(true);
                                            postdata.child(post_key).child("Sum").setValue(nextsum);
                                            viewHolder.setRating(post_key, user);
                                            Rating =false;
                                        }
                                    } else {
                                        int k=1;
                                        i=k;
                                        String nextsum = String.valueOf(i);
                                        postdata.child(post_key).child("Ratings").child(user).setValue(true);
                                        postdata.child(post_key).child("Sum").setValue(nextsum);
                                        viewHolder.setRating(post_key, user);
                                        Rating =false;
                                    }

                                    viewHolder.setSum(model.getSum());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                viewHolder. reportclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (report == false)
                        {
                            viewHolder.setReport(post_key, report, rpadapter, user, model.getUser());
                            report = true;
                        }
                        else {
                            viewHolder.setReport(post_key, report, rpadapter, user, model.getUser());
                            report = false;
                        }

                    }
                });
            }
        };

            recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    //setup view
    public static class BaiDangViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ImageView rating, reportclick;
        public  ListView reportlist;
        DatabaseReference mdataRating;
        FirebaseAuth Auth;


        public BaiDangViewHolder(View itemview)
        {
            super(itemview);
            view = itemview;
            rating = (ImageView)view.findViewById(R.id.imgrating);
            reportclick = (ImageView)view.findViewById(R.id.imgreport);
            reportlist = (ListView)view.findViewById(listrp);
            mdataRating = FirebaseDatabase.getInstance().getReference().child("ListPost");
            Auth = FirebaseAuth.getInstance();
            mdataRating.keepSynced(true);

        }

        public void setReport(final String post_key, boolean value, final ArrayAdapter<String> adapter, String email, String name)
        {
            if (value == false)
            {
                reportlist.setVisibility(View.VISIBLE);
                if (!email.equals("admin@gmailcom"))
                {
                    if (!email.equals(name))
                    {
                        ViewGroup.LayoutParams params = reportlist.getLayoutParams();
                        params.height = 150;
                        reportlist.setLayoutParams(params);
                        reportlist.requestLayout();
                        reportlist.setAdapter(adapter);
                        reportlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                mdataRating.child(post_key).child("Reported").setValue("true");
                            }
                        });
                    }
                    else {
                        reportlist.setAdapter(adapter);
                        reportlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String val = adapter.getItem(i).toString();
                                if (val.equals("Delete"))
                                {
                                    mdataRating.child(post_key).removeValue();
                                }
                                else
                                {
                                    mdataRating.child(post_key).child("Reported").setValue("true");
                                }
                            }
                        });
                    }
                }
                else {

                    reportlist.setAdapter(adapter);
                    reportlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String val = adapter.getItem(i).toString();
                            if (val.equals("Delete"))
                            {
                                mdataRating.child(post_key).removeValue();
                            }
                            else
                            {
                                mdataRating.child(post_key).child("Reported").removeValue();
                            }
                        }
                    });
                }

            }
            else
            {

                reportlist.setVisibility(View.GONE);
            }
        }

        public void setRating(String post_key, final String user)
        {
            mdataRating.child(post_key).child("Ratings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user))
                    {
                        rating.setBackgroundResource(R.drawable.icon_starting);
                    }
                    else {
                        rating.setBackgroundResource(R.drawable.icon_start);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setContent(String Content, float size, int type)
        {
            TextView bd_noidung = (TextView)view.findViewById(R.id.txtpost_text);
            bd_noidung.setTextSize(size);
            bd_noidung.setTextAlignment(type);
            bd_noidung.setText(Content);
        }

        public void setUserName(String UserName)
        {
            TextView bd_usname = (TextView)view.findViewById(R.id.txtusername);
            bd_usname.setText(UserName);
        }
        public void setImage( Context context,  String Image, int type )
        {
            ImageView bd_img = (ImageView)view.findViewById(R.id.post_img);

            bd_img.setVisibility(type);

            Picasso.with(context).load(Image).into(bd_img);
        }
        public void setChuDe(String ChuDe)
        {
            TextView bd_chude = (TextView)view.findViewById(R.id.txtchude);
            bd_chude.setText(ChuDe);
        }
        public void setSum(String Sum)
        {
            TextView bd_sum = (TextView)view.findViewById(R.id.txtrating);
            bd_sum.setText(Sum);
        }
    }
}
