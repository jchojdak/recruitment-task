package com.jchojdak.recruitmenttask.client;

import com.jchojdak.recruitmenttask.dto.BranchDto;
import com.jchojdak.recruitmenttask.dto.RepoDto;
import com.jchojdak.recruitmenttask.exception.EntityNotFoundException;
import com.jchojdak.recruitmenttask.exception.InvalidGitHubTokenException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    private static final Logger logger = LoggerFactory.getLogger(GitHubClient.class);

    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.api.token}")
    private String token;

    @Value("${github.api.version}")
    private String apiVersion;

    private final RestTemplate restTemplate;

    private static final String NOT_FOUND_MESSAGE = "Entity not found";
    private static final String UNAUTHORIZED_MESSAGE = "Invalid GitHub token";

    public List<RepoDto> fetchUserRepositories(String login) {
        String url = String.format("%s/users/%s/repos", baseUrl, login);
        logger.info("Fetching repositories for user '{}'", login);
        return fetchData(url, new ParameterizedTypeReference<>() {});
    }

    public List<BranchDto> fetchRepositoryBranches(String owner, String repoName) {
        String url = String.format("%s/repos/%s/%s/branches", baseUrl, owner, repoName);
        logger.info("Fetching branches for repo '{}/{}'", owner, repoName);
        return fetchData(url, new ParameterizedTypeReference<>() {});
    }

    private HttpEntity<Void> createEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + token);
        headers.set("X-GitHub-Api-Version", apiVersion);

        return new HttpEntity<>(headers);
    }

    private <T> List<T> fetchData(String url, ParameterizedTypeReference<List<T>> responseType) {
        try {
            logger.debug("Calling GitHub API: {}", url);
            ResponseEntity<List<T>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    createEntity(),
                    responseType
            );
            logger.debug("GitHub API response: {}", response.getStatusCode());
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("GitHub API returned 404 NOT FOUND for URL: {}", url);
            throw new EntityNotFoundException(NOT_FOUND_MESSAGE);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("GitHub API returned 401 UNAUTHORIZED for token.");
            throw new InvalidGitHubTokenException(UNAUTHORIZED_MESSAGE);
        } catch (HttpClientErrorException e) {
            logger.error("GitHub API error: {} - {}", e.getStatusCode(), e.getMessage());
            throw e;
        }
    }
}