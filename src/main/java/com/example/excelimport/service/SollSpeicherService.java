package com.example.excelimport.service;

import com.example.excelimport.model.BesetzungsEintrag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SollSpeicherService {

    private static final String FILE_NAME = "sollbestand.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Double> ladeSollBestand() {
        try {
            File file = new File(FILE_NAME);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<Map<String, Double>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public void speichereSollBestand(List<BesetzungsEintrag> eintraege) {
        try {
            Map<String, Double> map = new HashMap<>();
            for (BesetzungsEintrag e : eintraege) {
                map.put(keyFor(e.getOrgEinheit(), e.getBeschreibung()), e.getSoll());
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String keyFor(String org, String besch) {
        return org + "|" + besch;
    }
}
