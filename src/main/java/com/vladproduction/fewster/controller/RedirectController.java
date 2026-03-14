package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.exception.UrlNotFoundException;
import com.vladproduction.fewster.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Handles public short-URL redirection.
 *
 * GET /r/{shortCode} → 302 redirect to the original URL.
 *
 * Extracted from implicit controller handling into its own class so the
 * redirect concern is explicit and easy to find.
 *
 * The short URL is stored in the database as the full value including the
 * base prefix (e.g. "http://localhost:8080/r/abc123"). This controller
 * reconstructs that full value from the path variable and the configured
 * base.url.prefix before querying the repository.
 */
@Slf4j
@Controller
public class RedirectController {

    @Value("${base.url.prefix}")
    private String baseUrlPrefix;

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/r/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {
        // Reconstruct the full shortUrl as stored in the database
        String fullShortUrl = baseUrlPrefix + shortCode;

        try {
            String originalUrl = urlService.getOriginalByShortUrl(fullShortUrl);
            log.debug("Redirecting /r/{} → {}", shortCode, originalUrl);

            RedirectView view = new RedirectView(originalUrl);
            view.setExposeModelAttributes(false);
            return view;

        } catch (UrlNotFoundException ex) {
            log.warn("Short code not found: {}", shortCode);
            RedirectView notFound = new RedirectView("/?error=link-not-found");
            notFound.setExposeModelAttributes(false);
            return notFound;
        }
    }
}