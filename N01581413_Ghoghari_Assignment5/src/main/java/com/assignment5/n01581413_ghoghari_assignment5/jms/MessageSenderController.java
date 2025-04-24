package com.assignment5.n01581413_ghoghari_assignment5.jms;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/structured")
public class MessageSenderController {

    private final JmsTemplate jmsTemplate;

    public MessageSenderController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/send")
    public String sendStructuredMessage(@RequestBody MessageParser payload) {
        jmsTemplate.convertAndSend("structured-queue", payload);
        return "Structured message sent: " + payload;
    }
}
