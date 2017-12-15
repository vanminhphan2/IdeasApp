package com.example.rio.ideasapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.rio.ideasapp.Activity.CreateNewPost;
import com.example.rio.ideasapp.Activity.Detail;
import com.example.rio.ideasapp.Activity.LogActivity;
import com.example.rio.ideasapp.MenuAction.SettingProfile;
import com.example.rio.ideasapp.MenuAction.UsersProfile;
import com.example.rio.ideasapp.Model.BaiDang;
import com.example.rio.ideasapp.Model.Message;
import com.example.rio.ideasapp.Model.Status;
import com.example.rio.ideasapp.View.MessageAdapter;
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



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //

    //
    TextView textCartItemCount;
    //layout
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private RecyclerView recyclerView;
    TextView dishoten;
    TextView diachi;
    ImageView avatar;
    private RecyclerView listmessage;


    private boolean Rating = false, report = false;

    private int i=0;
    private boolean messageclick = false;
    //Database
    DatabaseReference mData, postdata, searchpost;
    private FirebaseAuth mAuth;
    private String user;
    Query query, querynull, querymess, queryrep;

    //Data
    Status status;
    private String thanhpho=null;

    //search view
    private ListView listresult;
    ArrayAdapter<String> searchadapter;
    String[] listsearch = {"Khoa Học", "Thiên Nhiên", "Cuộc Sống", "Vũ Khí","Xe"};

    //
    public static ArrayAdapter<String> rpadapter;
    String[] listrep = {"Report", "Delete"};
    String[] listadmin = {"Delete", "Dismiss"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //

        // declare data
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        searchpost = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser UserC = mAuth.getCurrentUser();
        user = (UserC.getEmail().replace(".",""));
        postdata = FirebaseDatabase.getInstance().getReference().child("ListPost");
        //query = postdata.orderByChild("ChuDe").equalTo("Khoa Học");

        querynull = postdata;
        querymess = mData.child("Users").child(user).child("Message").orderByChild("view").equalTo("false");
        queryrep = postdata.orderByChild("Reported").equalTo("true");

        //
        final NavigationView navmenu = (NavigationView)findViewById(R.id.viewmenu);
        navmenu.setNavigationItemSelectedListener(this);

        // create drawer menu
        drawerLayout = (DrawerLayout)findViewById(R.id.layout_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close );
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //searchview
        listresult = (ListView)findViewById(R.id.search_suggest);
        searchadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listsearch );

        // declare var

        //view bai dang
        recyclerView = (RecyclerView)findViewById(R.id.grid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listmessage = (RecyclerView)findViewById(R.id.messageview);
        listmessage.setLayoutManager(new LinearLayoutManager(this));

        if (user.equals("admin@gmailcom"))
        {
            rpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listadmin );
            mData.child("Admins").child(user).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String adname = dataSnapshot.getValue().toString();
                    dishoten = (TextView)findViewById(R.id.txtplayname);
                    dishoten.setText(adname);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            navmenu.inflateHeaderView(R.layout.header_admin);
            navmenu.inflateMenu(R.menu.menu_admin);
            onStart(queryrep);
        }
        else {
            navmenu.inflateHeaderView(R.layout.header);
            navmenu.inflateMenu(R.menu.menu_item);
            rpadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,listrep );
            //get value for displayname from firebase
            mData.child("Users").child(user).child("Profiles").child("Hoten").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nametam = dataSnapshot.getValue().toString();
                    dishoten = (TextView)findViewById(R.id.txtplayname);
                    dishoten.setText(nametam);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //get value for diachi from firebase
            mData.child("Users").child(user).child("Profiles").child("Diachi").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String tptemp = dataSnapshot.getValue().toString();
                    thanhpho = tptemp;
                    diachi = (TextView)findViewById(R.id.txtdiachi);
                    diachi.setText(thanhpho);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // create storage reference
            final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            //declare image avatar
            final String[] dataimg = {"null"};
            mData.child("Users").child(user).child("Profiles").child("Avatar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataimg[0] = dataSnapshot.getValue().toString();
                    avatar = (ImageView)findViewById(R.id.imgavatar);
                    // Reference to an image file in Firebase Storage
                    if (dataimg[0] == "n")
                    {
                        avatar.setBackground(getDrawable(R.drawable.default_avatar));
                    }
                    else  {
                        StorageReference storageReference = mStorageRef.child("images/").child(dataimg[0]);
                        // Load the image using Glide - load image from data to image view
                        Glide.with(MainActivity.this)
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(avatar);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            onStart(querynull);

            // message view
            onStartMess(querymess);
        }


    }




    //set status on for user
    public void setoffstatus(FirebaseUser user)
    {
        String s = (user.getEmail().replace(".", ""));
        mData.child("Users").child(s).child("usTrangthai").setValue(status.setOFF);
    }

    //to create new post
    public void gotonewpost(MenuItem item)
    {
        Intent tonewpost = new Intent(MainActivity.this, CreateNewPost.class);
        startActivity(tonewpost);
    }
    //set query search
    public Query setquerySearch(String string)
    {
        query = postdata.orderByChild("ChuDe").equalTo(string);
        return query;
    }
    //to profile page when click item menu
    public void gotoprofile(MenuItem item)
    {
        Intent touserprofile = new Intent(MainActivity.this, UsersProfile.class);
        startActivity(touserprofile);
    }
    //to profile page when click item menu
    public void gotosetting(MenuItem item)
    {
        Intent tosetting = new Intent(MainActivity.this, SettingProfile.class);
        startActivity(tosetting);
    }
    //logout when click item menu
    public void logout(MenuItem item)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Bạn có chắc muốn đăng xuất?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = mAuth.getCurrentUser();
                setoffstatus(user);
                Intent intent = new Intent(MainActivity.this,LogActivity.class);
                startActivity(intent);}
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog thongbao = builder.create();
        thongbao.show();
    }
    //create menu search and notification
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


            getMenuInflater().inflate(R.menu.menu_main, menu);
            final MenuItem menuItem, searchitem;
            menuItem = menu.findItem(R.id.message);
            View actionView = MenuItemCompat.getActionView(menuItem);
            textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
            mData.child("Users").child(user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("Message")) {
                        mData.child("Users").child(user).child("Message").child("Value").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String check_value = dataSnapshot.getValue().toString();
                                if (check_value.equals("false")) {
                                    setupBadge(1);
                                } else {
                                    setupBadge(0);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        setupBadge(0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOptionsItemSelected(menuItem);
                }
            });

            searchitem = menu.findItem(R.id.searchview);
            SearchView actionsearch = (SearchView) searchitem.getActionView();
            actionsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    listresult.setVisibility(View.GONE);
                    if (s.equalsIgnoreCase("khoa học") || s.equalsIgnoreCase("khoa hoc") || s.equalsIgnoreCase("k")) {
                        Query kh = setquerySearch("Khoa Học");
                        onStart(kh);
                    }
                    if (s.equalsIgnoreCase("thiên nhiên") || s.equalsIgnoreCase("thien nhien") || s.equalsIgnoreCase("thien")) {
                        Query kh = setquerySearch("Thiên Nhiên");
                        onStart(kh);
                    } else {
                        Query newque = setquerySearch(s);
                        onStart(newque);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    searchadapter.getFilter().filter(s);
                    listresult.setVisibility(View.VISIBLE);
                    listresult.setAdapter(searchadapter);
                    listresult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String itemresult = searchadapter.getItem(i).toString();
                            Toast.makeText(MainActivity.this, itemresult, Toast.LENGTH_SHORT).show();
                            onQueryTextSubmit(itemresult);
                        }
                    });
                    return false;
                }
            });
            actionsearch.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    listresult.setVisibility(View.GONE);
                    return false;
                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.searchview)
        {
            return true;
        }
        if (item.getItemId() == R.id.message)
        {
            if (messageclick == false) {
                listmessage.setVisibility(View.VISIBLE);
                messageclick = true;
            }
            else
            {
                listmessage.setVisibility(View.GONE);
                messageclick = false;
            }
            return true;
        }
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //init data for search item

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //navigationmenu click item
    @SuppressWarnings("StatementWithEmtyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.profile)
        {

            return  true;
        }
        if (item.getItemId() == R.id.event)
        {

            return  true;
        }
        if (item.getItemId() == R.id.setting)
        {

            return  true;
        }
        if (item.getItemId() == R.id.logout)
        {
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // view bai dang
    protected  void onStart(Query query1) {
        super.onStart();

        FirebaseRecyclerAdapter<BaiDang, UsersProfile.BaiDangViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BaiDang, UsersProfile.BaiDangViewHolder>(

                BaiDang.class,R.layout.list_baidang,UsersProfile.BaiDangViewHolder.class,query1
        ) {
            @Override
            protected void populateViewHolder(final UsersProfile.BaiDangViewHolder viewHolder, final BaiDang model, int position) {
                final String post_key = getRef(position).getKey();

                String value = model.getImage().toString();
                if (value == "null")
                {
                    viewHolder.setContent(model.getContent(), 30, View.TEXT_ALIGNMENT_CENTER);
                    viewHolder.setUserName(model.getUserName());
                    viewHolder.setImage(getApplicationContext(), model.getImage(), View.GONE);
                    viewHolder.setChuDe(model.getChuDe());
                    viewHolder.setRating(post_key,user);
                    viewHolder.setSum(model.getSum());

                }
                else {
                    viewHolder.setContent(model.getContent(), 18, View.TEXT_ALIGNMENT_TEXT_START);
                    viewHolder.setUserName(model.getUserName());
                    viewHolder.setImage(getApplicationContext(), model.getImage(), View.VISIBLE);
                    viewHolder.setChuDe(model.getChuDe());
                    viewHolder.setRating(post_key,user);
                    viewHolder.setSum(model.getSum());
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent todetail = new Intent(MainActivity.this, Detail.class);
                        todetail.putExtra("bd_id", post_key);
                        startActivity(todetail);
                    }
                });

                //
                viewHolder.rating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
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

                viewHolder.reportclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.setReport(post_key, rpadapter, user, model.getUser());
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    // view message
    protected void onStartMess(Query newquery)
    {
        super.onStart();
        FirebaseRecyclerAdapter<Message, MessageAdapter> message_notifi = new FirebaseRecyclerAdapter<Message, MessageAdapter>(
                Message.class, R.layout.message, MessageAdapter.class, newquery
        ) {
            @Override
            protected void populateViewHolder(final MessageAdapter viewHolder, Message model, int position) {
                final String mess_key = getRef(position).getKey();
                final String post_key = model.getKey();
                viewHolder.setuid(model.getuid());
                viewHolder.setImage(MainActivity.this,model.getuid().toString());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mData.child("Users").child(user).child("Message").child(mess_key).child("view").setValue("true");
                        mData.child("Users").child(user).child("Message").child("Value").setValue("true");
                        Intent todetail = new Intent(MainActivity.this, Detail.class);
                        todetail.putExtra("bd_id", post_key);
                        startActivity(todetail);
                        listmessage.setVisibility(View.GONE);
                    }
                });
            }
        };
        listmessage.setAdapter(message_notifi);
    }

    //Badge count
    private void setupBadge(int i) {

        if (textCartItemCount != null) {
            if (i == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(i, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
