package com.hafizzaturrahim.tambang.tracking;

/**
 * Created by Hafizh on 31/08/2017.
 */

public class Tracking {
    String id_tracking;
    String id_user;
    String nama;
    String tanggal;

    public Tracking() {
    }

    public String getId_tracking() {
        return id_tracking;
    }

    public void setId_tracking(String id_tracking) {
        this.id_tracking = id_tracking;
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
