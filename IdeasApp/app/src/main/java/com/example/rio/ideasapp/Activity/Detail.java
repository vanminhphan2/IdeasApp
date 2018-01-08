package com.example.rio.ideasapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.Model.Comment;
import com.example.rio.ideasapp.Model.Message;
import com.example.rio.ideasapp.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by User on 11/4/2017.
 */

public class Detail extends Activity {

    private TextView chude, noidung, nhanvat;
    private ImageView imagepost,avatar2, follow;
    private EditText comment;
    private Button sendcmt;
    private ListView viewcomment;
    private String post_key, user_cmt;
    CommentAdapter adapter;
    private ArrayList<Comment> commentslist;
    private boolean following = false;
    private String nameuser;



    //Firebase
    DatabaseReference mData, mdata;
    FirebaseAuth mAuth;
    String user2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        //Firebase
        mData = FirebaseDatabase.getInstance().getReference().child("ListPost");
        mdata = FirebaseDatabase.getInstance().getReference().child("Accounts").child("Users");
        post_key = getIntent().getExtras().getString("bd_id");
        user_cmt = getIntent().getExtras().getString("user_cmt");


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserC = mAuth.getCurrentUser();
        user2 = (UserC.getEmail().replace(".",""));

        //declare var
        chude = (TextView)findViewById(R.id.txtchude2);
        noidung = (TextView)findViewById(R.id.txtpost_text2);
        nhanvat = (TextView)findViewById(R.id.txtusername2);
        imagepost = (ImageView)findViewById(R.id.post_img2);
        avatar2 = (ImageView)findViewById(R.id.imgavar2);
        comment = (EditText)findViewById(R.id.edtcomment);
        sendcmt = (Button)findViewById(R.id.btnrating);
        follow = (ImageView)findViewById(R.id.btnfollow);
        //set up view for item bai dang
        mData.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_content =  dataSnapshot.child("Content").getValue().toString();
                String post_img =  dataSnapshot.child("Image").getValue().toString();
                final String post_user =  dataSnapshot.child("User").getValue().toString();
                String post_feilds =  dataSnapshot.child("ChuDe").getValue().toString();
                String post_name =  dataSnapshot.child("UserName").getValue().toString();

                //set value string
                chude.setText(post_feilds);
                noidung.setText(post_content);
                nhanvat.setText(post_name);
                Picasso.with(Detail.this).load(post_img).into(imagepost);
                nameuser = post_user;

                // set avatar image
                final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                final String[] dataimg = {null};
                mdata.child(post_user).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataimg[0] = dataSnapshot.getValue().toString();
                        // Reference to an image file in Firebase Storage
                        if (dataimg != null ) {
                            StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                            // Load the image using Glide - load image from data to image view
                            Glide.with(Detail.this)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .into(avatar2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // setup button follow
                if (user2.equals(post_name))
                {
                    follow.setVisibility(View.GONE);
                }
                else {
                    mdata.child(user2).child("Following").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(post_user))
                            {
                                follow.setBackgroundResource(R.drawable.ic_following);
                                following = true;

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //list comment
        adapter = new CommentAdapter(Detail.this, new ArrayList<Comment>()) ;
        viewcomment = (ListView)findViewById(R.id.listcmt);
        viewcomment.setAdapter(adapter);
        // message adapter
        final String[] listusercmt = {};
        int d =0;


        loadComment();
        // button send comment clicked
        sendcmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment newcomment = new Comment();
                Message message = new Message();
                //set comment to firebase
                newcomment.setCommentText(comment.getText().toString());
                newcomment.setUid(user2);
                //set message to firebase
                message.setuid(user2);
                message.setKey(post_key);
                message.setView("false");
                mData.child(post_key).child("Comment").push().setValue(newcomment);
                if(user2.equals(nameuser)) // user2 is user login -- nameuser is user of baidang
                {
                    DatabaseReference notifi = mdata.child(user_cmt).child("Message").push();
                    notifi.setValue(message);
                    mdata.child(user_cmt).child("Message").child("Value").setValue("false");
                }
                else
                {
                    DatabaseReference notifi = mdata.child(nameuser).child("Message").push();
                    notifi.setValue(message);
                    mdata.child(nameuser).child("Message").child("Value").setValue("false");
                }
                // set text null after send comment
                comment.setText("");
            }
        });

        // button follow
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child(post_key).child("User").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String email = dataSnapshot.getValue().toString();
                            // user can't following them self
                            if (user2.equals(email)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Detail.this);
                                View view = getLayoutInflater().inflate(R.layout.dialog_message, null);
                                ImageView imgdialogview = (ImageView) view.findViewById(R.id.imgdialog);
                                TextView viewdialog = (TextView) view.findViewById(R.id.txtdialog);
                                Button btndialog = (Button) view.findViewById(R.id.btnOk);
                                builder.setView(view);
                                final AlertDialog messagedialog = builder.create();
                                messagedialog.show();
                                btndialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        messagedialog.hide();
                                    }
                                });
                            } else {
                                if (following == true) {
                                    mdata.child(user2).child("Following").child(email).removeValue();
                                    follow.setBackgroundResource(R.drawable.ic_follow);
                                    following = false;


                                }
                                else {
                                    mdata.child(user2).child("Following").child(email).setValue(true);
                                    follow.setBackgroundResource(R.drawable.ic_following);
                                    following = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
        });

    }

    // load all comment
    private void loadComment()
    {
        commentslist = new ArrayList<Comment>();
        mData.child(post_key).child("Comment").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comment cmt = new Comment();
                cmt =dataSnapshot.getValue(Comment.class);
                display(cmt);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // scroll for listview
    private void scroll() {
        viewcomment.setSelection(viewcomment.getCount() - 1);
    }

    //display comment
    private void display(Comment comment){
        adapter.add(comment);
        adapter.notifyDataSetChanged();
        scroll();
    }

    //custom item for list comment
    public class CommentAdapter extends BaseAdapter{
        private List<Comment> listcomment;
        private Activity Context1;

        public CommentAdapter(Activity context, List<Comment> viewcomment)
        {
            this.Context1 = context;
            this.listcomment = viewcomment;
        }

        @Override
        public int getCount() {
            if (listcomment != null) {
                return listcomment.size();
            } else {
                return 0;
            }
        }

        @Override
        public Comment getItem(int position) {
            if (listcomment != null) {
                return listcomment.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder viewHolder;
            Comment comment = getItem(i);
            LayoutInflater layoutInflater = (LayoutInflater) Context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (view == null)
            {
                view = layoutInflater.inflate(R.layout.comment_item, null);
                viewHolder = createViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            // set avatar image
            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            final String[] dataimg = {null};
            String val = comment.getUid();
            //set name for user comment
            mdata.child(val).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String ten = dataSnapshot.getValue().toString();
                    viewHolder.username3.setText(ten);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // set image for user comment
            mdata.child(val).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataimg[0] = dataSnapshot.getValue().toString();
                    // Reference to an image file in Firebase Storage
                    if (dataimg != null ) {
                        StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                        // Load the image using Glide - load image from data to image view
                        Glide.with(Detail.this)
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(viewHolder.imgavatar3);
                    }
                    if (dataimg[0] == "null")
                    {
                        viewHolder.imgavatar3.setImageResource(R.drawable.default_avatar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            viewHolder.comment3.setText(comment.getCommentText());
            return view;
        }

        public void add(Comment comments) {
            listcomment.add(comments);
        }

        private ViewHolder createViewHolder(View v)
        {
            ViewHolder holder = new ViewHolder();
            holder.imgavatar3 = (ImageView)v.findViewById(R.id.imgavar3);
            holder.username3 = (TextView)v.findViewById(R.id.txtusername3);
            holder.comment3 = (TextView)v.findViewById(R.id.txtcomment3);
            return holder;
        }

        private class ViewHolder
        {
            public ImageView imgavatar3;
            public TextView username3;
            public TextView comment3;
        }
    }

}
