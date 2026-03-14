package com.vladproduction.fewster.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class AlgorithmUtility {

    @Value("${short.url.length}")
    private int shortUrlLength;

    @Value("${characters.string.literal}")
    private String characters;

    /**
     * Method for generate short code
     * hash-based approach
     */
    public String generateShortCode(String originalUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(originalUrl.getBytes());
            String base64Hash = Base64.getEncoder().encodeToString(hash);

            //clean up base64 string and take first 6 characters
            String cleanHash = base64Hash.replaceAll("[+/=]", "");
            return cleanHash.substring(0, Math.min(shortUrlLength, cleanHash.length()));

        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating hash", e);
            return generateSimpleShortCode(originalUrl);
        }
    }

    /**
     * method to generate simple short code,
     * used hashCode and convert to base62
     */
    public String generateSimpleShortCode(String originalUrl) {
        int hash = Math.abs(originalUrl.hashCode());
        StringBuilder shortCode = new StringBuilder();
        while (shortCode.length() < shortUrlLength) {
            shortCode.append(characters.charAt(hash % characters.length()));
            hash /= characters.length();
        }

        return shortCode.toString();
    }

}
