package com.assignment5.n01581413_ghoghari_assignment5.jms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageParser implements Serializable {
    private String name;
    private String message;
}
