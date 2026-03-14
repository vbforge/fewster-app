package com.vladproduction.fewster.repository;

import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    // Find by original URL (for global duplicate checking - keep for backward compatibility)
    Optional<UrlEntity> findByOriginalUrl(String originalUrl);

    // Find by short URL (for redirection)
    Optional<UrlEntity> findByShortUrl(String shortUrl);

    // Check if short URL exists (for global uniqueness)
    boolean existsByShortUrl(String shortUrl);

    // Find all URLs for a specific user
    List<UrlEntity> findByUser(User user);

    // Find URLs by user ID
    List<UrlEntity> findByUserId(Long userId);

    // Check if URL belongs to specific user
    Optional<UrlEntity> findByIdAndUser(Long id, User user);

    // Check if URL belongs to specific user by ID
    Optional<UrlEntity> findByIdAndUserId(Long id, Long userId);

    // Find by original URL and user (for duplicate checking per user)
    Optional<UrlEntity> findByOriginalUrlAndUser(String originalUrl, User user);

    // Check if original URL exists for a specific user
    boolean existsByOriginalUrlAndUser(String originalUrl, User user);

}
