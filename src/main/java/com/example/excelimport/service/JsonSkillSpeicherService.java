// --- JsonSkillSpeicherService.java ---
package com.example.excelimport.service;

import com.example.excelimport.model.SkillEintrag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("jsonSkillService")
@ConditionalOnProperty(name = "datenquelle.typ", havingValue = "json", matchIfMissing = true)
public class JsonSkillSpeicherService implements ISkillSpeicherService {

    private static final String FILE_NAME = "skills.json";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<SkillEintrag> ladeSkills() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                return mapper.readValue(file, new TypeReference<>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void speichereSkills(List<SkillEintrag> eintraege) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), eintraege);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
