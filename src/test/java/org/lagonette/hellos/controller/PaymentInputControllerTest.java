package org.lagonette.hellos.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lagonette.hellos.bean.PaymentStateEnum;
import org.lagonette.hellos.bean.helloasso.HelloAssoOrder;
import org.lagonette.hellos.bean.helloasso.HelloAssoPayer;
import org.lagonette.hellos.bean.helloasso.HelloAssoPayment;
import org.lagonette.hellos.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PaymentInputController.class)
public class PaymentInputControllerTest {

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        //Init MockMvc Object and build
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldHandleNewPaymentSuccessfully() throws Exception {
        // GIVEN
        HelloAssoPayment helloAssoPayment = new HelloAssoPayment();
        helloAssoPayment.setId("id1");
        helloAssoPayment.setState(PaymentStateEnum.Authorized);
        HelloAssoPayer payer = new HelloAssoPayer();
        payer.setEmail("email");
        payer.setFirstName("first");
        payer.setLastName("last");
        helloAssoPayment.setPayer(payer);
        HelloAssoOrder helloAssoOrder = new HelloAssoOrder();
        helloAssoOrder.setId(11);
        helloAssoPayment.setOrder(helloAssoOrder);
        helloAssoPayment.setPayer_first_name("first");
        helloAssoPayment.setPayer_last_name("last");
        ObjectMapper mapper = new ObjectMapper();
        String payment = mapper.writeValueAsString(helloAssoPayment);

        // WHEN
        mockMvc.perform((
                MockMvcRequestBuilders.post("/helloasso/payment")
                        .content(payment)
                        .contentType(APPLICATION_JSON)))
                .andExpect(status().isOk());
    }
}