package com.bpao.devfoliobuilderapi.application.dto.project;

import com.bpao.devfoliobuilderapi.domain.model.Project;
import com.bpao.devfoliobuilderapi.domain.model.Technology;

import java.util.List;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectResponse toResponse(Project project, List<Technology> technologies) {
        List<TechnologySummary> summaries = technologies.stream()
                .map(technology -> new TechnologySummary(
                        technology.getId(), technology.getName(), technology.getIconUrl()))
                .toList();
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getRepositoryUrl(),
                project.getLiveDemoUrl(),
                project.getImageUrl(),
                project.getCreatedAt(),
                summaries);
    }
}