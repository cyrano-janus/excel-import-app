// --- ISkillSpeicherService.java ---
package com.example.excelimport.service;

import com.example.excelimport.model.SkillEintrag;
import java.util.List;

public interface ISkillSpeicherService {
    List<SkillEintrag> ladeSkills();
    void speichereSkills(List<SkillEintrag> eintraege);
}