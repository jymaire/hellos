package org.lagonette.hellos.controller;

import org.lagonette.hellos.bean.AdminConfig;
import org.lagonette.hellos.service.ConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.lagonette.hellos.service.ConfigurationService.*;

@Controller
public class AdminController {
    private final ConfigurationService configurationService;

    public AdminController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public AdminConfig configuration(Model model) {
        var all = configurationService.findAll();
        AdminConfig adminConfig = new AdminConfig(Boolean.parseBoolean(all.get(PAYMENT_CYCLOS_ENABLED)), Boolean.parseBoolean(all.get(PAYMENT_AUTOMATIC_ENABLED)), all.get(MAIL_RECIPIENT));
        model.addAttribute("adminConfig", adminConfig);
        return adminConfig;
    }

    @Transactional
    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String save(Model model, @ModelAttribute AdminConfig adminConfig) {
        configurationService.save(Boolean.valueOf(adminConfig.isPaymentCyclosEnabled()).toString(), Boolean.valueOf(adminConfig.isPaymentAutomaticEnabled()).toString(), adminConfig.getMailRecipient());
        return "redirect:/admin";
    }
}
