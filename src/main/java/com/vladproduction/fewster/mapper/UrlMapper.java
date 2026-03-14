package com.vladproduction.fewster.mapper;

import com.vladproduction.fewster.dto.UrlDTO;
import com.vladproduction.fewster.entity.UrlEntity;

/**
 * Stateless mapper between {@link UrlEntity} and {@link UrlDTO}.
 *
 * Changes from original:
 *   - toDTO now maps clickCount and createdAt so the dashboard can display them.
 *   - toEntity intentionally does NOT map clickCount or createdAt — those are
 *     managed exclusively by the entity lifecycle (@PreUpdate, incrementClickCount).
 */
public class UrlMapper {

    private UrlMapper() {
        // utility class — no instances
    }

    /** Entity → DTO (full read mapping including stats fields). */
    public static UrlDTO toDTO(UrlEntity entity) {
        if (entity == null) {
            return null;
        }
        UrlDTO dto = new UrlDTO();
        dto.setId(entity.getId());
        dto.setOriginalUrl(entity.getOriginalUrl());
        dto.setShortUrl(entity.getShortUrl());
        dto.setClickCount(entity.getClickCount());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    /**
     * DTO → Entity (inbound mapping for create / update).
     *
     * clickCount and createdAt are intentionally excluded:
     *   - createdAt is set in the UrlEntity no-arg constructor.
     *   - clickCount is managed by UrlEntity.incrementClickCount().
     * Allowing clients to set these via a DTO would be a data-integrity risk.
     */
    public static UrlEntity toEntity(UrlDTO dto) {
        if (dto == null) {
            return null;
        }
        UrlEntity entity = new UrlEntity();
        entity.setId(dto.getId());
        entity.setOriginalUrl(dto.getOriginalUrl());
        entity.setShortUrl(dto.getShortUrl());
        return entity;
    }
}
