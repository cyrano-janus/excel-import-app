package com.example.excelimport.service;

import com.example.excelimport.model.LehrpfadEintrag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LehrpfadSpeicherService {

    private static final String FILE_NAME = "lehrpfade.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<LehrpfadEintrag> lade() {
        try {
            File f = new File(FILE_NAME);
            if (f.exists()) {
                return objectMapper.readValue(f, new TypeReference<List<LehrpfadEintrag>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void speichere(List<LehrpfadEintrag> eintraege) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), eintraege);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<LehrpfadEintrag> find(List<LehrpfadEintrag> list, String org, String beschr) {
        return list.stream()
            .filter(l -> l.getOrgEinheit().equals(org) && l.getBeschreibung().equals(beschr))
            .findFirst();
    }
}
