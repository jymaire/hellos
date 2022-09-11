package org.lagonette.hellos.controller;

import org.lagonette.hellos.bean.ProcessResult;
import org.lagonette.hellos.entity.Payment;
import org.lagonette.hellos.repository.PaymentRepository;
import org.lagonette.hellos.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public PaymentController(PaymentRepository paymentRepository, PaymentService paymentService) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String payments(Model model) {
        List<Payment> all = new ArrayList<>(paymentRepository.findAll());
        Collections.sort(all);
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
