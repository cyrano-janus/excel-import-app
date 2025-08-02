// --- SkillEintragRepository.java ---
package com.example.excelimport.repository;

import com.example.excelimport.model.SkillEintrag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillEintragRepository extends JpaRepository<SkillEintrag, Long> {
    Optional<SkillEintrag> findByOrgEinheitAndBeschreibung(String orgEinheit, String beschreibung);
}
