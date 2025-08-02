package com.example.excelimport.model;

import jakarta.persistence.*;

@Entity
@Table(name = "besetzungs_eintrag")
public class BesetzungsEintrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orgEinheit;
    private String beschreibung;

    private double ist;
    private double soll;

    public BesetzungsEintrag() {}

    public BesetzungsEintrag(String orgEinheit, String beschreibung, double ist, double soll) {
        this.orgEinheit = orgEinheit;
        this.beschreibung = beschreibung;
        this.ist = ist;
        this.soll = soll;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrgEinheit() { return orgEinheit; }
    public void setOrgEinheit(String orgEinheit) { this.orgEinheit = orgEinheit; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public double getIst() { return ist; }
    public void setIst(double ist) { this.ist = ist; }

    public double getSoll() { return soll; }
    public void setSoll(double soll) { this.soll = soll; }

    @Transient
    public double getDifferenz() {
        return ist - soll;
    }

    @Transient
    public String getStatus() {
        double diff = getDifferenz();
        if (diff > 0) return "Überbesetzt";
        if (diff < 0) return "Unterbesetzt";
        return "Ausgeglichen";
    }
}
