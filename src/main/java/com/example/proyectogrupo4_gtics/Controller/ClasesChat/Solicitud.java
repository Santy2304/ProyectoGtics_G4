package com.example.proyectogrupo4_gtics.Controller.ClasesChat;

import java.util.ArrayList;

public class Solicitud {
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String sede;
    private String deliverHour;
    private String dni;
    private ArrayList<Medicamentos> listaMedicamentos;
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public ArrayList<Medicamentos> getListaMedicamentos() {
        return listaMedicamentos;
    }
    public void setListaMedicamentos(ArrayList<Medicamentos> listaMedicamentos) {
        this.listaMedicamentos = listaMedicamentos;
    }
    public String getSede() {
        return sede;
    }
    public void setSede(String sede) {
        this.sede = sede;
    }
    public String getDeliverHour() {
        return deliverHour;
    }
    public void setDeliverHour(String deliverHour) {
        this.deliverHour = deliverHour;
    }
}
