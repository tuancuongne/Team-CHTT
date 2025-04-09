package com.example.cinemas;

public class Movie {
    private String Ten, Ddien, Dvien, Theloai, Ngonngu, Ngay, Thoiluong, Ndung, Anh, Id;

    public Movie() {}

    public Movie(String Anh, String Ngay, String Ten, String Ddien, String Dvien, String Theloai, String Ngonngu, String Thoiluong, String Ndung, String Id) {
        this.Anh = Anh;
        this.Ngay = Ngay;
        this.Ten = Ten;
        this.Ddien = Ddien;
        this.Dvien = Dvien;
        this.Theloai = Theloai;
        this.Ngonngu = Ngonngu;
        this.Thoiluong = Thoiluong;
        this.Ndung = Ndung;
        this.Id = Id;
    }

    public String getTen() { return Ten; }
    public void setTen(String Ten) { this.Ten = Ten; }

    public String getDdien() { return Ddien; }
    public void setDdien(String Ddien) { this.Ddien = Ddien; }

    public String getDvien() { return Dvien; }
    public void setDvien(String Dvien) { this.Dvien = Dvien; }

    public String getTheloai() { return Theloai; }
    public void setTheloai(String Theloai) { this.Theloai = Theloai; }

    public String getNgonngu() { return Ngonngu; }
    public void setNgonngu(String Ngonngu) { this.Ngonngu = Ngonngu; }

    public String getNgay() { return Ngay; }
    public void setNgay(String Ngay) { this.Ngay = Ngay; }

    public String getThoiluong() { return Thoiluong; }
    public void setThoiluong(String Thoiluong) { this.Thoiluong = Thoiluong; }

    public String getNdung() { return Ndung; }
    public void setNdung(String Ndung) { this.Ndung = Ndung; }

    public String getAnh() { return Anh; }
    public void setAnh(String Anh) { this.Anh = Anh; }

    public String getId() { return Id; }
    public void setId(String Id) { this.Id = Id; }
}