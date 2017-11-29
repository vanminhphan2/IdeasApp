package com.example.rio.ideasapp.View;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rio.ideasapp.Model.DanhMuc;
import com.example.rio.ideasapp.Model.Favorite;
import com.example.rio.ideasapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by User on 10/12/2017.
 */

public class CustomView extends ArrayAdapter<DanhMuc>{

    Context mcontext;
    LayoutInflater inflater;
    ArrayList<DanhMuc> listdanhmuc;
    String Title;
    Favorite favorite;


    private DatabaseReference mData;
    private FirebaseAuth mAuth;

    //khởi tạo danh sách
    String[] list = {"Khoa học", "Thiên nhiên", "Cuộc sống", "Vũ khí","Xe"};
    Integer[] images = {R.drawable.img_khoahoc, R.drawable.img_thiennhien,R.drawable.img_cuocsong,R.drawable.img_vukhi,
            R.drawable.img_xe};

    public CustomView(@NonNull Context context, @LayoutRes int resource, ArrayList<DanhMuc> list) {
        super(context, resource);
        this.mcontext = context;
        this.listdanhmuc = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(final int position, View convertview, ViewGroup parent)
    {
        final ViewHolder holder;
        if (convertview == null)
        {
            convertview = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.textdetail = (TextView)convertview.findViewById(R.id.txtdetail);
            holder.imagedanhmuc = (ImageView)convertview.findViewById(R.id.imgdanhmuc);
            holder.cbdanhmuc = (CheckBox)convertview.findViewById(R.id.cbCheck);
            convertview.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertview.getTag();
        }

        final DanhMuc danhmuc = listdanhmuc.get(position);
        holder.textdetail.setText(list[position]);
        holder.imagedanhmuc.setImageResource(images[position]);
        holder.cbdanhmuc.setChecked(danhmuc.isShowcheck());
        //firebase
        mData= FirebaseDatabase.getInstance().getReference().child("Accounts");
        mAuth = FirebaseAuth.getInstance();

        holder.cbdanhmuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.cbdanhmuc.isChecked())
                {
                    listdanhmuc.get(position).setShowcheck(true);
                    Title = listdanhmuc.get(position).getName();
                    //Toast.makeText(mcontext, Title, Toast.LENGTH_SHORT).show();
                    //set value to firebase in here
                    FirebaseUser user = mAuth.getCurrentUser();
                    setFavorite(user,Title,true);
            }
                else {
                    listdanhmuc.get(position).setShowcheck(false);
                    Title = listdanhmuc.get(position).getName();
                    //unset value to firebase in here
                    FirebaseUser user = mAuth.getCurrentUser();
                    setFavorite(user,Title,false);
                    //Toast.makeText(mcontext, "unchecked \t" + Title, Toast.LENGTH_SHORT).show();

                }
            }
        });
        return convertview;
    }

    public void setFavorite(FirebaseUser user, String text, boolean result)
    {
        String us = (user.getEmail().replace(".",""));
        mData.child("Users").child(us).child("Favorite").child(text).setValue(result);
    }

    public int getCount(){
        return listdanhmuc.size();
    }


    static class ViewHolder{
        ImageView imagedanhmuc;
        TextView textdetail;
        CheckBox cbdanhmuc;
    }
}
