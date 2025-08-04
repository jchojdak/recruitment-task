package com.jchojdak.recruitmenttask.service;

import com.jchojdak.recruitmenttask.client.GitHubClient;
import com.jchojdak.recruitmenttask.dto.RepoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

    private final GitHubClient gitHubClient;

    public List<RepoDto> getNonForkRepositories(String login) {
        logger.info("Processing request to get non-fork repositories for user '{}'", login);
        var repositories = gitHubClient.fetchUserRepositories(login);

        if (repositories == null || repositories.isEmpty()) {
            logger.info("No repositories found for user '{}'", login);
            return List.of();
        }

        var filteredRepos = repositories.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    var branches = gitHubClient.fetchRepositoryBranches(repo.owner().login(), repo.name());
                    return new RepoDto(repo.name(), repo.fork(), repo.owner(), branches);
                })
                .toList();

        logger.info("Found {} non-fork repositories for user '{}'", filteredRepos.size(), login);
        return filteredRepos;
    }
}
