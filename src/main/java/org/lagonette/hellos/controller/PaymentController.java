package org.lagonette.hellos.controller;

import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.lagonette.hellos.service.CyclosService;
import org.lagonette.hellos.service.HelloAssoService;
import org.lagonette.hellos.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PaymentController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final PaymentRepository paymentRepository;
    private final CyclosService cyclosService;
    private final HelloAssoService helloAssoService;
    private final PaymentService paymentService;

    public PaymentController(PaymentRepository paymentRepository, CyclosService cyclosService, HelloAssoService helloAssoService, PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.cyclosService = cyclosService;
        this.helloAssoService = helloAssoService;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String payments(Model model) {
        List<Payment> all = paymentRepository.findAll();
        model.addAttribute("payments", all);
        return "list";
    }

    @Transactional
    @RequestMapping(value = "/list", method = RequestMethod.POST, params = "delete")
    public String delete(@RequestParam(name = "id") Integer id, Model model) {
        paymentRepository.deleteById(id);
        return "redirect:/list";
    }

    @Transactional
    @RequestMapping(value = "/list", method = RequestMethod.POST, params = "credit")
    public String credit(@RequestParam(name = "id") Integer id, Model model) {
        ProcessResult processResult = new ProcessResult();
        paymentService.creditAccount(processResult, id);
        return "redirect:/list";
    }
}
