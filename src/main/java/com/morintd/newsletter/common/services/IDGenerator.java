package com.morintd.newsletter.common.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IDGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
