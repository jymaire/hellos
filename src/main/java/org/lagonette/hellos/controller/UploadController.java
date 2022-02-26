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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Controller
public class UploadController {
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
                final CsvToBean build = getBuild(file);
                List beans = build.parse();
                collectOnlineService.importPaymentsFromCSV(beans);
                model.addAttribute("status", true);
                model.addAttribute("message", "L'import a été réalisé avec succès");
            } catch (Exception ex) {
                LOGGER.error(ex.getStackTrace().toString());
                model.addAttribute("message", "An error occurred while processing the CSV file." + ex.getCause());
                model.addAttribute("status", false);
            }
        }

        return "file-upload-status";
    }

    private CsvToBean getBuild(MultipartFile file) throws IOException {
        final HeaderColumnNameMappingStrategy headerColumnNameMappingStrategy = new HeaderColumnNameMappingStrategy();
        headerColumnNameMappingStrategy.setType(Payment.class);
        return new CsvToBeanBuilder(new InputStreamReader(file.getInputStream()))
                .withSeparator(';')
                .withMappingStrategy(headerColumnNameMappingStrategy)
                .build();
    }
}