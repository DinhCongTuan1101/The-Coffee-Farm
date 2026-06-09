package com.mycompany.the_coffee_farm;

import java.util.ArrayList;
import java.util.List;

public class GioHang {

    public static class MonHang {

        public String tenMon;
        public int giaTien;
        public int soLuong;

        public MonHang(String tenMon, int giaTien, int soLuong) {
            this.tenMon = tenMon;
            this.giaTien = giaTien;
            this.soLuong = soLuong;
        }
    }

    public static List<MonHang> danhSachMua = new ArrayList<>();
}
