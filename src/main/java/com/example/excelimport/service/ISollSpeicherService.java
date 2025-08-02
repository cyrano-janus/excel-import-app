// --- ISollSpeicherService.java ---
package com.example.excelimport.service;


import com.example.excelimport.model.BesetzungsEintrag;
import java.util.List;

public interface ISollSpeicherService {
    List<BesetzungsEintrag> ladeSollBestand();
    void speichereSollBestand(List<BesetzungsEintrag> eintraege);
}

