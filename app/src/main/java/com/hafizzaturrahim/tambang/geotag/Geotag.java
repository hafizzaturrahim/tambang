package com.hafizzaturrahim.tambang.geotag;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hafizh on 01/09/2017.
 */

public class Geotag implements Parcelable{
    String id_marker;
    String id_user;
    String nama;
    Double lat;
    Double lng;
    String image;
    String deskripsi;

    public Geotag() {
    }

    protected Geotag(Parcel in) {
        id_marker = in.readString();
        id_user = in.readString();
        nama = in.readString();
        image = in.readString();
        deskripsi = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Geotag> CREATOR = new Creator<Geotag>() {
        @Override
        public Geotag createFromParcel(Parcel in) {
            return new Geotag(in);
        }

        @Override
        public Geotag[] newArray(int size) {
            return new Geotag[size];
        }
    };

    public String getId_marker() {
        return id_marker;
    }

    public void setId_marker(String id_marker) {
        this.id_marker = id_marker;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_marker);
        parcel.writeString(id_user);
        parcel.writeString(nama);
        parcel.writeString(image);
        parcel.writeString(deskripsi);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }
}
