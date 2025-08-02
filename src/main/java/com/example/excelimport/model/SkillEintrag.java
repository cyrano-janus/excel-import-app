package com.example.excelimport.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skill_eintrag")
public class SkillEintrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orgEinheit;
    private String beschreibung;

    @Lob
    private String beschreibungstext;

    @ElementCollection
    @CollectionTable(name = "skills", joinColumns = @JoinColumn(name = "skill_eintrag_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    public SkillEintrag() {}

    public SkillEintrag(String orgEinheit, String beschreibung) {
        this.orgEinheit = orgEinheit;
        this.beschreibung = beschreibung;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrgEinheit() { return orgEinheit; }
    public void setOrgEinheit(String orgEinheit) { this.orgEinheit = orgEinheit; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getBeschreibungstext() { return beschreibungstext; }
    public void setBeschreibungstext(String beschreibungstext) { this.beschreibungstext = beschreibungstext; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
