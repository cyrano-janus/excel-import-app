// --- DbSollSpeicherService.java ---
package com.example.excelimport.service;

import com.example.excelimport.model.BesetzungsEintrag;
import com.example.excelimport.repository.BesetzungsEintragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dbSollService")
@ConditionalOnProperty(name = "datenquelle.typ", havingValue = "database")
public class DbSollSpeicherService implements ISollSpeicherService {

    @Autowired
    private BesetzungsEintragRepository repo;

    @Override
    public List<BesetzungsEintrag> ladeSollBestand() {
        return repo.findAll();
    }

    @Override
    public void speichereSollBestand(List<BesetzungsEintrag> eintraege) {
        repo.saveAll(eintraege);
    }
}