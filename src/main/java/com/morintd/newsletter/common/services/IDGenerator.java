package com.morintd.newsletter.common.services;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IDGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
