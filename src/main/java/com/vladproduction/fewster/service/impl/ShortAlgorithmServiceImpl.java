package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.service.ShortAlgorithmService;
import com.vladproduction.fewster.utility.AlgorithmUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShortAlgorithmServiceImpl implements ShortAlgorithmService {

    @Value("${base.url.prefix}")
    private String baseUrl;

    private final AlgorithmUtility algorithmUtility;

    public ShortAlgorithmServiceImpl(AlgorithmUtility algorithmUtility) {
        this.algorithmUtility = algorithmUtility;
    }

    @Override
    public String makeShort(String text) {
        log.info("Starting algorithm to make url shorter");
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Provided url should be not empty");
        }

        log.info("Algorithm started...");

        String shortCode = algorithmUtility.generateShortCode(text);
        log.info("Successfully generated short code: {}", shortCode);

        String shortUrl = baseUrl + shortCode;
        log.info("Successfully made url shorter: {} -> {}", text, shortUrl);

        return shortUrl;
    }


}
