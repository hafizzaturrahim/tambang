package com.hafizzaturrahim.tambang.geotag;

/**
 * Created by Hafizh on 01/09/2017.
 */

public class Geotag {
    String id_marker;
    String id_user;
    String nama;
    Double lat;
    Double lng;
    String image;

    public Geotag() {
    }

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
}
