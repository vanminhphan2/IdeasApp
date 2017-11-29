package com.example.rio.ideasapp.Model;

/**
 * Created by User on 11/2/2017.
 */

public class BaiDang {
    private String Content;
    private String Image;
    private String User;
    private String UserName;
    private String ChuDe;
    private String Sum;

    public BaiDang(){}
    public BaiDang(String content,String image, String user, String name, String chuDe, String sum)
    {
        Content = content;
        Image = image;
        User = user;
        UserName = name;
        ChuDe = chuDe;
        Sum = sum;
    }

    public String getUser() {
        return User;
    }

    public String getImage() {
        return Image;
    }

    public String getContent() {
        return Content;
    }

    public void setUser(String user) {
        User = user;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setContent(String noidung) {
        Content = noidung;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getChuDe() {
        return ChuDe;
    }

    public void setChuDe(String chuDe) {
        ChuDe = chuDe;
    }

    public String getSum() {
        return Sum;
    }

    public void setSum(String sum) {
        Sum = sum;
    }
}
