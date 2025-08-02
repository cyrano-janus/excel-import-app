package com.example.excelimport.service;

import com.example.excelimport.model.SkillEintrag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkillSpeicherService {

    private static final String FILE_NAME = "skills.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SkillEintrag> ladeSkills() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<List<SkillEintrag>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void speichereSkills(List<SkillEintrag> eintraege) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), eintraege);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
