package com.example.excelimport.model;

public class LehrpfadEintrag {
    private String orgEinheit;
    private String beschreibung;
    private String mermaidCode;

    public LehrpfadEintrag() {}
    public LehrpfadEintrag(String orgEinheit, String beschreibung, String mermaidCode) {
        this.orgEinheit = orgEinheit;
        this.beschreibung = beschreibung;
        this.mermaidCode = mermaidCode;
    }

    public String getOrgEinheit() { return orgEinheit; }
    public void setOrgEinheit(String orgEinheit) { this.orgEinheit = orgEinheit; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getMermaidCode() { return mermaidCode; }
    public void setMermaidCode(String mermaidCode) { this.mermaidCode = mermaidCode; }
}
