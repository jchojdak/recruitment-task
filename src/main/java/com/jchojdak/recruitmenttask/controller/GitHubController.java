package com.jchojdak.recruitmenttask.controller;

import com.jchojdak.recruitmenttask.dto.RepoDto;
import com.jchojdak.recruitmenttask.service.GitHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Path: REST /api/v1/github
@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
@Tag(name = "GitHub API Task", description = "Recruitment task.")
public class GitHubController {

    private static final Logger logger = LoggerFactory.getLogger(GitHubController.class);

    private final GitHubService githubService;

    // Path: GET /api/v1/github/{login}/repos
    @Operation(
            summary = "Get non-fork repositories for a GitHub user",
            description = "Returns all non-fork repositories for a given GitHub user along with their branches"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of repositories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid GitHub API token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User or repositories not found", content = @Content)
    })
    @GetMapping("/{login}/repos")
    public ResponseEntity<List<RepoDto>> getNonForkRepositories(@Parameter(description = "GitHub login", required = true) @PathVariable String login) {
        logger.info("Received GET request for /github/{}/repos", login);
        List<RepoDto> repositories = githubService.getNonForkRepositories(login);
        return ResponseEntity.ok(repositories);
    }
}
