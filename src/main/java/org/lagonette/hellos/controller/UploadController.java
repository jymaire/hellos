package org.lagonette.hellos.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.lagonette.hellos.bean.collectonline.Payment;
import org.lagonette.hellos.service.CollectOnlineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class UploadController {
    public static final char SEPARATOR = ';';
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final CollectOnlineService collectOnlineService;

    public UploadController(CollectOnlineService collectOnlineService) {
        this.collectOnlineService = collectOnlineService;
    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Merci de sélectionner un fichier CSV");
            model.addAttribute("status", false);
        } else {
            try {
                InputStream inputStream = file.getInputStream();
                final List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines().toList();
                String csvUpdated = fixCsvFile(lines);

                final CsvToBean build = getBuild(csvUpdated);
                List beans = build.parse();
                collectOnlineService.importPaymentsFromCSV(beans);
                model.addAttribute("status", true);
                model.addAttribute("message", "L'import a été réalisé avec succès");
            } catch (Exception ex) {
                LOGGER.error(ex.getStackTrace().toString());
                LOGGER.error(ex.toString());
                model.addAttribute("message", "An error occurred while processing the CSV file." + ex.getCause());
                model.addAttribute("status", false);
            }
        }

        return "file-upload-status";
    }

    /**
     * File from collect online is not a valid CSV file, so we fix it
     *
     * @param lines
     * @return
     */
    protected String fixCsvFile(List<String> lines) {
        StringBuilder csvUpdatedBuild = new StringBuilder();

        boolean firstLine = true;
        for (String line : lines) {
            if (firstLine) {
                int count = 0;
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == SEPARATOR) {
                        count++;
                    }
                }
                /*
                 * L'export collect online produit un csv non valide. Le nombre d'en tete est de 56 alors que le
                 * nombre de colonnes de données est de 57
                 * Donc correction de la structure de la ligne d'en tete en rajoutant un point virgule
                 */
                if (count == 56) {
                    line = line + ";";
                }
                firstLine = false;
            }
            /**
             *  Deuxième particularité des exports CSV de collectonline : parfois on trouve des "=" dans les données, au tout début.
             *  Ainsi que """ à la fin.
             *  Donc deuxième correction, remplacer les "=" par des " afin d'avoir un format valide
             *  Exemple d'extrait d'une ligne de données : [...];"=""CAM pour 20 euros""";[...]
             */
            line = line.replaceAll("\"=\"\"", "\"");
            line = line.replaceAll("\"\"\"", "\"");
            csvUpdatedBuild.append(line).append(System.getProperty("line.separator"));
            LOGGER.debug(line);
        }
        String csvUpdated = csvUpdatedBuild.toString();
        return csvUpdated;
    }

    private CsvToBean getBuild(String file) throws IOException {
        final HeaderColumnNameMappingStrategy headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategy();
        headerColumnNameMappingStrategy.setType(Payment.class);
        return new CsvToBeanBuilder(new StringReader(file))
                .withSeparator(SEPARATOR)
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .build();
    }
}