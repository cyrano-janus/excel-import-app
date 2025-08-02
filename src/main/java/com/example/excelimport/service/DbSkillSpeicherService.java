// --- DbSkillSpeicherService.java ---
package com.example.excelimport.service;

import com.example.excelimport.model.SkillEintrag;
import com.example.excelimport.repository.SkillEintragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dbSkillService")
@ConditionalOnProperty(name = "datenquelle.typ", havingValue = "database")
public class DbSkillSpeicherService implements ISkillSpeicherService {

    @Autowired
    private SkillEintragRepository repo;

    @Override
    public List<SkillEintrag> ladeSkills() {
        return repo.findAll();
    }

    @Override
    public void speichereSkills(List<SkillEintrag> eintraege) {
        repo.saveAll(eintraege);
    }
}
