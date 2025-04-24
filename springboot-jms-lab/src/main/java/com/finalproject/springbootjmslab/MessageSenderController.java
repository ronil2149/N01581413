package com.finalproject.springbootjmslab;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lab")
public class MessageSenderController {

    private final JmsTemplate jmsTemplate;

    public MessageSenderController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        jmsTemplate.convertAndSend("lab-queue", message);
        return "Message sent: " + message;
    }
}
