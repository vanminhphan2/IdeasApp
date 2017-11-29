package com.example.rio.ideasapp.Model;

/**
 * Created by User on 10/26/2017.
 */

public class Favorite {
    public boolean KhoaHoc;
    public boolean CuocSong;
    public boolean ThienNhien;
    public boolean Xe;
    public boolean Vukhi;

    public Favorite(boolean khoaHoc, boolean cuocSong, boolean thienNhien, boolean xe, boolean vukhi)
    {
        KhoaHoc = khoaHoc;
        CuocSong = cuocSong;
        ThienNhien = thienNhien;
        Xe = xe;
        Vukhi = vukhi;
    }
}
