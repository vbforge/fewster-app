package com.vladproduction.fewster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data transfer object for a shortened URL.
 *
 * Used in both directions:
 *   - Inbound  (create / update): only {@code originalUrl} is populated by the client.
 *   - Outbound (read / response): all fields are populated from the entity.
 *
 * Changes from original:
 *   - Added {@code clickCount} and {@code createdAt} for dashboard display.
 *   - Added {@code shortCode} helper (the 6-char code extracted from shortUrl).
 *   - Added URL format validation on {@code originalUrl}.
 */
@Getter
@Setter
public class UrlDTO {

    private Long id;

    @NotBlank(message = "Original URL is required")
    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Pattern(
            regexp = "^https?://.+",
            message = "URL must start with http:// or https://"
    )
    private String originalUrl;

    private String shortUrl;

    /** Number of times this short URL has been followed. Populated on read. */
    private Long clickCount;

    /** Timestamp when this entry was created. Populated on read. */
    private LocalDateTime createdAt;

    /**
     * Convenience accessor — returns the short code portion of {@code shortUrl}
     * (everything after the last '/'), or the full shortUrl if no slash is found.
     * Useful in Thymeleaf templates: {@code urlDTO.shortCode}.
     */
    public String getShortCode() {
        if (shortUrl == null || shortUrl.isBlank()) {
            return "";
        }
        int lastSlash = shortUrl.lastIndexOf('/');
        return lastSlash >= 0 ? shortUrl.substring(lastSlash + 1) : shortUrl;
    }
}
