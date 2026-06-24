package com.mycompany.the_coffee_farm;

public class SanPham {

    private String idSanPham;
    private String tenSanPham;
    private double giaBan;
    private String donViTinh;
    private int idLoai;
    private String imageUrl;

    public SanPham() {
    }

    public SanPham(String idSanPham, String tenSanPham, double giaBan, String donViTinh, int idLoai, String imageUrl) {
        this.idSanPham = idSanPham;
        this.tenSanPham = tenSanPham;
        this.giaBan = giaBan;
        this.donViTinh = donViTinh;
        this.idLoai = idLoai;
        this.imageUrl = imageUrl;
    }

    public String getIdSanPham() {
        return idSanPham;
    }

    public void setIdSanPham(String idSanPham) {
        this.idSanPham = idSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public int getIdLoai() {
        return idLoai;
    }

    public void setIdLoai(int idLoai) {
        this.idLoai = idLoai;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
