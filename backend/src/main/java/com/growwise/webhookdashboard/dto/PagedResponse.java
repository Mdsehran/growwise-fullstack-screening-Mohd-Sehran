package com.growwise.webhookdashboard.dto;

import java.util.List;

/**
 * Generic pagination envelope returned by GET /api/admin/webhook-attempts.
 * Carries the page slice plus enough metadata (page, size, totalElements,
 * totalPages) for the frontend to render "Previous/Next/Page X of Y"
 * controls without a second round-trip.
 */
public class PagedResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
