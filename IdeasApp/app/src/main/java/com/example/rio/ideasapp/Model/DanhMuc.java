package com.example.rio.ideasapp.Model;

/**
 * Created by User on 10/23/2017.
 */

public class DanhMuc {

    String name;
    boolean showcheck;

    public void setShowcheck(boolean showcheck1)
    {
        showcheck = showcheck1;
    }

    public boolean isShowcheck() {
        return showcheck;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String Name)
    {
        this.name = Name;
    }


}
