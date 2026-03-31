package com.hufs.haigram.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hufs.haigram.backend.service.DemoSocialService;

@Component
public class SeedDataInitializer implements ApplicationRunner {

    private final DemoSocialService socialService;

    public SeedDataInitializer(DemoSocialService socialService) {
        this.socialService = socialService;
    }

    @Override
    public void run(ApplicationArguments args) {
        socialService.seedIfEmpty();
    }
}
