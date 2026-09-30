package com.bpao.devfoliobuilderapi.domain.model;

import com.bpao.devfoliobuilderapi.domain.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de dominio para la tabla projects.
 */
@Getter
@Builder
@AllArgsConstructor
public class Project {

    private final Long id;
    private final Long portfolioId;
    private final String title;
    private final String description;
    private final String repositoryUrl;
    private final String liveDemoUrl;
    private final String imageUrl;
    private final boolean visible;
    private final Instant createdAt;

    public static Project create(Long portfolioId, String title, String description,
                                 String repositoryUrl, String liveDemoUrl, String imageUrl,
                                 boolean visible) {
        if (title == null || title.isBlank()) {
            throw new DomainException("El titulo del proyecto no puede ser nulo ni vacio");
        }
        return builder()
                .portfolioId(portfolioId)
                .title(title.trim())
                .description(description)
                .repositoryUrl(repositoryUrl)
                .liveDemoUrl(liveDemoUrl)
                .imageUrl(imageUrl)
                .visible(visible)
                .createdAt(Instant.now())
                .build();
    }

    public Project withId(Long newId) {
        return builder()
                .id(newId)
                .portfolioId(portfolioId)
                .title(title)
                .description(description)
                .repositoryUrl(repositoryUrl)
                .liveDemoUrl(liveDemoUrl)
                .imageUrl(imageUrl)
                .visible(visible)
                .createdAt(createdAt)
                .build();
    }

    public Project withDetails(String newTitle, String newDescription,
                               String newRepositoryUrl, String newLiveDemoUrl, String newImageUrl,
                               boolean newVisible) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new DomainException("El titulo del proyecto no puede ser nulo ni vacio");
        }
        return builder()
                .id(id)
                .portfolioId(portfolioId)
                .title(newTitle.trim())
                .description(newDescription)
                .repositoryUrl(newRepositoryUrl)
                .liveDemoUrl(newLiveDemoUrl)
                .imageUrl(newImageUrl)
                .visible(newVisible)
                .createdAt(createdAt)
                .build();
    }
}