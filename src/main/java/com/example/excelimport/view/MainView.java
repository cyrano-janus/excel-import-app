package com.example.excelimport.view;

import com.example.excelimport.repository.BesetzungsEintragRepository;
import com.example.excelimport.model.BesetzungsEintrag;
import com.example.excelimport.model.SkillEintrag;
import com.example.excelimport.service.ExcelImportService;
import com.example.excelimport.export.ExcelExportService;
import com.example.excelimport.service.SollSpeicherService;
import com.example.excelimport.service.SkillSpeicherService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@Route("")
public class MainView extends VerticalLayout {

    private final ExcelImportService importService;
    private final SollSpeicherService sollSpeicherService;
    private final SkillSpeicherService skillSpeicherService;
    private final BesetzungsEintragRepository testRepository;

    private List<String[]> csvData = new ArrayList<>();
    private List<BesetzungsEintrag> besetzungsListe = new ArrayList<>();
    private List<SkillEintrag> skillListe = new ArrayList<>();

    private final Grid<String[]> csvGrid = new Grid<>();
    private final Grid<BesetzungsEintrag> sollGrid = new Grid<>();
    private final Grid<BesetzungsEintrag> analyseGrid = new Grid<>();
    private final Grid<SkillEintrag> skillGrid = new Grid<>();

    private final Map<String, Character> separatorOptions = new LinkedHashMap<>();

    public MainView(@Autowired ExcelImportService importService,
                    @Autowired SollSpeicherService sollSpeicherService,
                    @Autowired SkillSpeicherService skillSpeicherService,
                    @Autowired BesetzungsEintragRepository testRepository) {
        this.importService = importService;
        this.sollSpeicherService = sollSpeicherService;
        this.skillSpeicherService = skillSpeicherService;
        this.testRepository = testRepository;

        setSizeFull();
        setSpacing(true);

        Tab importTab = new Tab("CSV Import");
        Tab teamTab = new Tab("Soll-Bestand pflegen");
        Tab analyseTab = new Tab("Besetzungsanalyse");
        Tab skillTab = new Tab("Skills");

        Tabs tabs = new Tabs(importTab, teamTab, analyseTab, skillTab);
        Div importContent = createImportTab();
        Div teamContent = createTeamTab();
        Div analyseContent = createAnalyseTab();
        Div skillContent = createSkillTab();

        Map<Tab, Div> tabToContent = Map.of(
                importTab, importContent,
                teamTab, teamContent,
                analyseTab, analyseContent,
                skillTab, skillContent
        );

        tabs.addSelectedChangeListener(event -> {
            tabToContent.values().forEach(content -> content.setVisible(false));
            tabToContent.get(tabs.getSelectedTab()).setVisible(true);

            if (event.getSelectedTab() == analyseTab) {
                analyseGrid.setItems(besetzungsListe);
            } else if (event.getSelectedTab() == skillTab) {
                skillGrid.setItems(skillListe);
            }
        });

        add(new H1("Barrierefreie Projektverwaltung"),
            tabs, importContent, teamContent, analyseContent, skillContent);

        tabToContent.values().forEach(content -> content.setVisible(false));
        importContent.setVisible(true);
    }

    // ───────────── Tab 1: CSV Import ─────────────
    private Div createImportTab() {
        Div layout = new Div();
        layout.setSizeFull();

        separatorOptions.put("Semikolon (;)", ';');
        separatorOptions.put("Komma (,)", ',');
        separatorOptions.put("Tabulator (\\t)", '\t');
        separatorOptions.put("Pipe (|)", '|');

        ComboBox<String> separatorSelector = new ComboBox<>("Wähle Separator");
        separatorSelector.setItems(separatorOptions.keySet());
        separatorSelector.setValue("Semikolon (;)");

        Checkbox headerCheckbox = new Checkbox("Erste Zeile ist Spaltenüberschrift");
        headerCheckbox.setValue(true);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".csv");

        Button processButton = new Button("Datei verarbeiten", event -> {
            String selectedSeparator = separatorSelector.getValue();
            char separatorChar = separatorOptions.get(selectedSeparator);

            csvData = importService.parseCsv(buffer.getInputStream(), separatorChar);
            besetzungsListe = groupCsvToBesetzungsEintraege(csvData);

            // CSV-Grid anzeigen
            csvGrid.removeAllColumns();
            if (!csvData.isEmpty()) {
                if (headerCheckbox.getValue()) {
                    String[] headers = csvData.get(0);
                    createCsvGridColumns(headers);
                    csvGrid.setItems(csvData.subList(1, csvData.size()));
                } else {
                    createCsvGridColumns(csvData.get(0).length);
                    csvGrid.setItems(csvData);
                }
            }

            // Soll-Grid aktualisieren
            sollGrid.setItems(besetzungsListe);

            // Skills initialisieren (falls neue Sachgebiete auftauchen)
            updateSkillListFromCSV();
            skillGrid.setItems(skillListe);
        });

        csvGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        csvGrid.setSizeFull();

        layout.add(new H2("CSV Import"), separatorSelector, headerCheckbox, upload, processButton, csvGrid);
        layout.setHeightFull();
        return layout;
    }

    private void createCsvGridColumns(int columnCount) {
        csvGrid.removeAllColumns();
        for (int i = 0; i < columnCount; i++) {
            final int colIndex = i;
            csvGrid.addColumn(row -> colIndex < row.length ? row[colIndex] : "")
                   .setHeader("Spalte " + (colIndex + 1))
                   .setSortable(true);
        }
    }

    private void createCsvGridColumns(String[] headers) {
        csvGrid.removeAllColumns();
        for (int i = 0; i < headers.length; i++) {
            final int colIndex = i;
            String header = headers[i] != null ? headers[i] : "Spalte " + (colIndex + 1);
            csvGrid.addColumn(row -> colIndex < row.length ? row[colIndex] : "")
                   .setHeader(header)
                   .setSortable(true);
        }
    }

    // ───────────── Tab 2: Soll-Bestand pflegen ─────────────
    private Div createTeamTab() {
        Div layout = new Div();
        layout.setSizeFull();

        sollGrid.addColumn(BesetzungsEintrag::getOrgEinheit)
                .setHeader("Org.-Einheit").setSortable(true);
        sollGrid.addColumn(BesetzungsEintrag::getBeschreibung)
                .setHeader("Beschreibung").setSortable(true);
        sollGrid.addColumn(e -> String.format("%.2f", e.getIst()))
                .setHeader("IST").setSortable(true);

        Grid.Column<BesetzungsEintrag> sollColumn =
                sollGrid.addColumn(e -> String.format("%.2f", e.getSoll()))
                        .setHeader("SOLL").setSortable(true);

        Binder<BesetzungsEintrag> binder = new Binder<>(BesetzungsEintrag.class);
        Editor<BesetzungsEintrag> editor = sollGrid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        NumberField sollField = new NumberField();
        binder.forField(sollField)
              .withConverter(Double::doubleValue, Double::doubleValue)
              .bind(BesetzungsEintrag::getSoll, BesetzungsEintrag::setSoll);
        sollColumn.setEditorComponent(sollField);

        sollGrid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            sollField.focus();
        });

        editor.addSaveListener(event -> sollSpeicherService.speichereSollBestand(besetzungsListe));

        sollGrid.getElement().addEventListener("keydown", e -> editor.save())
                .setFilter("event.key === 'Enter'");
        sollGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        sollGrid.setSizeFull();
       
        layout.add(new H2("Soll-Bestand pflegen"),
                   new Paragraph("Doppelklicke in eine SOLL-Zelle, um sie zu bearbeiten:"), sollGrid);
        layout.setHeightFull();
        return layout;
    }

    // ───────────── Tab 3: Analyse ─────────────
    private Div createAnalyseTab() {
        Div layout = new Div();
        layout.setSizeFull();

        analyseGrid.addColumn(BesetzungsEintrag::getOrgEinheit)
                .setHeader("Org.-Einheit").setSortable(true);
        analyseGrid.addColumn(BesetzungsEintrag::getBeschreibung)
                .setHeader("Beschreibung").setSortable(true);
        analyseGrid.addColumn(e -> String.format("%.2f", e.getIst()))
                .setHeader("IST").setSortable(true);
        analyseGrid.addColumn(e -> String.format("%.2f", e.getSoll()))
                .setHeader("SOLL").setSortable(true);
        analyseGrid.addColumn(e -> String.format("%.2f", e.getDifferenz()))
                .setHeader("Differenz").setSortable(true);
        analyseGrid.addColumn(BesetzungsEintrag::getStatus)
                .setHeader("Status").setSortable(true);

        analyseGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        analyseGrid.setSizeFull();

        Button exportButton = new Button("Export nach Excel");

        StreamResource resource = new StreamResource("besetzungsanalyse.xlsx", () ->
            ExcelExportService.generateExcel(analyseGrid.getListDataView().getItems().toList())
        );

        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.add(exportButton);

        // Test-Button zur Datenbankspeicherung
        Button testButton = new Button("Testeintrag speichern", e -> {
            BesetzungsEintrag test = new BesetzungsEintrag("Test-Referat", "Test-Sachgebiet", 3.0, 5.0);
            testRepository.save(test);
            System.out.println("Testeintrag gespeichert in DB: " + test.getId());
        });

        layout.add(new H2("Besetzungsanalyse"), downloadLink, testButton,analyseGrid);
        layout.setHeightFull();
        return layout;
}

    // ───────────── Tab 4: Skills ─────────────
    private Div createSkillTab() {
        Div layout = new Div();
        layout.setSizeFull();

        skillGrid.addColumn(SkillEintrag::getOrgEinheit)
                .setHeader("Org.-Einheit").setSortable(true);
        skillGrid.addColumn(SkillEintrag::getBeschreibung)
                .setHeader("Sachgebiet").setSortable(true);
        skillGrid.addColumn(SkillEintrag::getBeschreibungstext)
                .setHeader("Beschreibungstext").setSortable(true);
        skillGrid.addColumn(e -> String.join(", ", e.getSkills()))
                .setHeader("Skills").setSortable(true);

        TextField beschreibungField = new TextField("Beschreibungstext");
        TextField skillsField = new TextField("Skills (Komma-getrennt)");
        Button saveButton = new Button("Änderungen speichern", event -> {
            SkillEintrag selected = skillGrid.asSingleSelect().getValue();
            if (selected != null) {
                selected.setBeschreibungstext(beschreibungField.getValue());
                List<String> skillList = Arrays.stream(skillsField.getValue().split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                selected.setSkills(skillList);
                skillSpeicherService.speichereSkills(skillListe);
                skillGrid.getDataProvider().refreshAll();
            }
        });

        skillGrid.addSelectionListener(event -> {
            SkillEintrag selected = skillGrid.asSingleSelect().getValue();
            if (selected != null) {
                beschreibungField.setValue(Optional.ofNullable(selected.getBeschreibungstext()).orElse(""));
                skillsField.setValue(String.join(", ", selected.getSkills()));
            }
        });

        skillGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        skillGrid.setSizeFull();

        layout.add(new H2("Skills verwalten"), skillGrid,
                   new HorizontalLayout(beschreibungField, skillsField, saveButton));
        layout.setHeightFull();
        return layout;
    }

    // ───────────── CSV-Gruppierung ─────────────
    private List<BesetzungsEintrag> groupCsvToBesetzungsEintraege(List<String[]> csvData) {
        if (csvData.isEmpty()) return new ArrayList<>();

        Map<String, Double> gespeicherteSoll = sollSpeicherService.ladeSollBestand();

        String[] headers = csvData.get(0);
        int orgIndex = findColumnIndex(headers, "Org-Einheit");
        int beschIndex = findColumnIndex(headers, "Beschreibung");
        int istIndex = findColumnIndex(headers, "IST");

        Map<String, BesetzungsEintrag> map = new HashMap<>();
        for (int i = 1; i < csvData.size(); i++) {
            String[] row = csvData.get(i);
            String org = row[orgIndex];
            String besch = row[beschIndex];
            double ist = parseDouble(row[istIndex]);

            String key = SollSpeicherService.keyFor(org, besch);
            double soll = gespeicherteSoll.getOrDefault(key, 0.0);

            map.compute(key, (k, v) -> {
                if (v == null) return new BesetzungsEintrag(org, besch, ist, soll);
                v.setIst(v.getIst() + ist);
                return v;
            });
        }
        return new ArrayList<>(map.values());
    }

    // ───────────── Skills aus CSV initialisieren ─────────────
    private void updateSkillListFromCSV() {
        List<SkillEintrag> gespeicherteSkills = skillSpeicherService.ladeSkills();
        Map<String, SkillEintrag> skillMap = new HashMap<>();
        for (SkillEintrag se : gespeicherteSkills) {
            String key = se.getOrgEinheit() + "|" + se.getBeschreibung();
            skillMap.put(key, se);
        }

        for (BesetzungsEintrag be : besetzungsListe) {
            String key = be.getOrgEinheit() + "|" + be.getBeschreibung();
            if (!skillMap.containsKey(key)) {
                skillMap.put(key, new SkillEintrag(be.getOrgEinheit(), be.getBeschreibung()));
            }
        }
        skillListe = new ArrayList<>(skillMap.values());
        skillSpeicherService.speichereSkills(skillListe);
    }

    // ───────────── Hilfsmethoden ─────────────
    private int findColumnIndex(String[] headers, String name) {
        String targetNormalized = name.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        for (int i = 0; i < headers.length; i++) {
            String colNormalized = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (colNormalized.equals(targetNormalized)) return i;
        }
        return -1;
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
