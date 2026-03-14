package com.vladproduction.fewster.utility;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.ShortUrlGenerationException;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.service.ShortAlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalUtility {

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    @Value("${generate.unique.short.url.maxAttempt}")
    private int maxAttempt;

    private final UrlRepository urlRepository;
    private final ShortAlgorithmService algorithmService;

    public GlobalUtility(UrlRepository urlRepository, ShortAlgorithmService algorithmService) {
        this.urlRepository = urlRepository;
        this.algorithmService = algorithmService;
    }

    /**
     * Helper method to generate globally unique short URL.
     * Short URLs must be unique across ALL users for redirection to work properly.
     */
    public String generateUniqueShortUrl(String originalUrl) {
        String shortUrl = algorithmService.makeShort(originalUrl);
        int attempts = 0;

        while (attempts < maxAttempt && urlRepository.existsByShortUrl(shortUrl)) {
            log.warn("Short URL collision detected: {}, regenerating... (attempt {})", shortUrl, attempts + 1);
            shortUrl = algorithmService.makeShort(originalUrl + "_" + attempts);
            attempts++;
        }

        if (attempts >= maxAttempt) {
            throw new ShortUrlGenerationException(
                    "Unable to generate unique short URL after " + maxAttempt + " attempts for URL: " + originalUrl);
        }

        log.info("Generated unique short URL: {} after {} attempts", shortUrl, attempts);
        return shortUrl;
    }

    /**
     * Check if original URL already exists for a specific user.
     * Prevents users from creating duplicate entries for the same original URL.
     */
    public boolean originalUrlExistsForUser(String originalUrl, User user) {
        return urlRepository.findByOriginalUrlAndUser(originalUrl, user).isPresent();
    }

    /**
     * Check if original URL exists for a specific user, excluding a specific URL ID.
     * Used for update operations where we want to allow updating to the same URL.
     */
    public boolean originalUrlExistsForUserExcluding(String originalUrl, User user, Long excludeUrlId) {
        return urlRepository.findByOriginalUrlAndUser(originalUrl, user)
                .filter(urlEntity -> !urlEntity.getId().equals(excludeUrlId))
                .isPresent();
    }

    /**
     * Check if URL is INVALID.
     * Returns true when the URL is null, blank, or missing http/https prefix.
     *
     * FIX: The original implementation had inverted logic — it returned true for
     * valid URLs and false for invalid ones, causing valid URLs to be rejected.
     */
    public boolean isInvalidUrl(String url) {
        return url == null
                || url.trim().isEmpty()
                || (!url.startsWith(HTTP) && !url.startsWith(HTTPS));
    }

    /**
     * Normalize URL to prevent duplicates from trailing slashes.
     * e.g. "https://example.com" and "https://example.com/" are treated as the same URL.
     */
    public String normalizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }

        String normalized = url.trim();

        // Remove trailing slash unless it is the protocol + domain root (slash count <= 2)
        if (normalized.endsWith("/") && normalized.length() > 8) {
            long slashCount = normalized.chars().filter(ch -> ch == '/').count();
            if (slashCount > 2) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
        }

        return normalized;
    }
}
