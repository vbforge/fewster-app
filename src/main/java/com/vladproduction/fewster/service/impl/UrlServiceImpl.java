package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.DuplicateUrlException;
import com.vladproduction.fewster.exception.InvalidUrlException;
import com.vladproduction.fewster.exception.UrlNotFoundException;
import com.vladproduction.fewster.mapper.UrlMapper;
import com.vladproduction.fewster.repository.UrlRepository;
import com.vladproduction.fewster.security.AuthService;
import com.vladproduction.fewster.service.UrlService;
import com.vladproduction.fewster.utility.GlobalUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final AuthService authService;
    private final GlobalUtility globalUtility;

    public UrlServiceImpl(UrlRepository urlRepository,
                          AuthService authService,
                          GlobalUtility globalUtility) {
        this.urlRepository = urlRepository;
        this.authService = authService;
        this.globalUtility = globalUtility;
    }

    @Override
    @Transactional
    public UrlDTO create(String urlText, boolean isDemo) {
        log.info("Creating short URL for: {}", urlText);

        User currentUser = isDemo ? authService.getDemoUser() : authService.getCurrentUser();

        String normalizedUrl = globalUtility.normalizeUrl(urlText);

        if (globalUtility.isInvalidUrl(normalizedUrl)) {
            throw new InvalidUrlException(normalizedUrl);
        }

        // Return the existing entry if this user already shortened this URL
        if (globalUtility.originalUrlExistsForUser(normalizedUrl, currentUser)) {
            log.info("URL already exists for user '{}', returning existing entry", currentUser.getUsername());
            UrlEntity existing = urlRepository
                    .findByOriginalUrlAndUser(normalizedUrl, currentUser)
                    .orElseThrow(() -> UrlNotFoundException.byShortUrl(normalizedUrl));
            return UrlMapper.toDTO(existing);
        }

        String shortUrl = globalUtility.generateUniqueShortUrl(normalizedUrl);

        log.info("Saving URL entity — user: '{}', original: {}, short: {}",
                currentUser.getUsername(), normalizedUrl, shortUrl);

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(normalizedUrl);
        urlEntity.setShortUrl(shortUrl);
        urlEntity.setUser(currentUser);

        UrlEntity saved = urlRepository.save(urlEntity);
        log.info("URL saved with ID: {}", saved.getId());

        return UrlMapper.toDTO(saved);
    }

    @Override
    public List<UrlDTO> getAllUrlsForCurrentUser() {
        User currentUser = authService.getCurrentUser();
        log.debug("Fetching all URLs for user: '{}'", currentUser.getUsername());

        return urlRepository.findByUser(currentUser)
                .stream()
                .map(UrlMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UrlDTO getUrlById(Long id) {
        User currentUser = authService.getCurrentUser();

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> UrlNotFoundException.byIdForUser(id));

        return UrlMapper.toDTO(urlEntity);
    }

    @Override
    @Transactional
    public UrlDTO updateUrl(Long id, String newOriginalUrl) {
        User currentUser = authService.getCurrentUser();

        String normalizedUrl = globalUtility.normalizeUrl(newOriginalUrl);

        if (globalUtility.isInvalidUrl(normalizedUrl)) {
            throw new InvalidUrlException(normalizedUrl);
        }

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> UrlNotFoundException.byIdForUser(id));

        if (globalUtility.originalUrlExistsForUserExcluding(normalizedUrl, currentUser, id)) {
            throw new DuplicateUrlException(normalizedUrl, urlEntity.getShortUrl());
        }

        urlEntity.setOriginalUrl(normalizedUrl);

        UrlEntity updated = urlRepository.save(urlEntity);
        log.info("Updated URL ID: {} for user: '{}'", id, currentUser.getUsername());

        return UrlMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteUrl(Long id) {
        User currentUser = authService.getCurrentUser();

        UrlEntity urlEntity = urlRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> UrlNotFoundException.byIdForUser(id));

        urlRepository.delete(urlEntity);
        log.info("Deleted URL ID: {} for user: '{}'", id, currentUser.getUsername());
    }

    @Override
    @Transactional
    public String getOriginalByShortUrl(String shortUrl) {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> UrlNotFoundException.byShortUrl(shortUrl));

        urlEntity.incrementClickCount();
        urlRepository.save(urlEntity);

        log.info("Redirect: {} → {} (clicks: {})",
                shortUrl, urlEntity.getOriginalUrl(), urlEntity.getClickCount());

        return urlEntity.getOriginalUrl();
    }
}
