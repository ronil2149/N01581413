package com.assignment5.n01581413_ghoghari_assignment5.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiverListener {

    @JmsListener(destination = "structured-queue")
    public void receiveStructuredMessage(MessageParser msg) {
        System.out.println("Received structured message:");
        System.out.println("Name: " + msg.getName());
        System.out.println("Message: " + msg.getMessage());
    }
}
