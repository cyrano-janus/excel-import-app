// --- BesetzungsEintragRepository.java ---
package com.example.excelimport.repository;

import com.example.excelimport.model.BesetzungsEintrag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BesetzungsEintragRepository extends JpaRepository<BesetzungsEintrag, Long> {
    Optional<BesetzungsEintrag> findByOrgEinheitAndBeschreibung(String orgEinheit, String beschreibung);
}
