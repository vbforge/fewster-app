package com.vladproduction.fewster.service;

import com.vladproduction.fewster.dto.UrlDTO;

import java.util.List;

public interface UrlService {

    UrlDTO create(String urlText, boolean isDemo);
    List<UrlDTO> getAllUrlsForCurrentUser();
    UrlDTO getUrlById(Long id);
    UrlDTO updateUrl(Long id, String newOriginalUrl);
    void deleteUrl(Long id);
    String getOriginalByShortUrl(String shortUrl);

}
