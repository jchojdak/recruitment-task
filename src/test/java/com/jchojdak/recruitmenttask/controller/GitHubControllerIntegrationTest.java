package com.jchojdak.recruitmenttask.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class GitHubControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", wireMock::baseUrl);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private String loadJson(String fileName) throws IOException {
        return Files.readString(Path.of("src/test/resources/__files/github/" + fileName), StandardCharsets.UTF_8);
    }

    @Test
    @Order(1)
    @DisplayName("Should return non-fork GitHub repositories with all branches and last commit SHA for given user")
    void givenExistingGitHubUser_whenGetNonForkRepositories_thenReturnNonForkReposWithBranchesAndLastCommitSha() throws Exception {
        // given
        String githubUser = "sampleuser";

        wireMock.stubFor(get(urlEqualTo("/users/" + githubUser + "/repos"))
                .willReturn(okJson(loadJson("user-repos.json"))));

        wireMock.stubFor(get(urlEqualTo("/repos/sampleuser/repo1/branches"))
                .willReturn(okJson(loadJson("repo1-branches.json"))));

        wireMock.stubFor(get(urlEqualTo("/repos/sampleuser/repo3/branches"))
                .willReturn(okJson(loadJson("repo3-branches.json"))));

        // when
        var response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/github/" + githubUser + "/repos");

        // then
        response
                .then()
                //.log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2)) // only non-fork repos
                .body("[0].name", is("repo1"))
                .body("[0].owner.login", is("sampleuser"))
                .body("[0].branches", hasSize(2))
                .body("[0].branches[0].name", is("main"))
                .body("[0].branches[0].commit.sha", is("abc123"))
                .body("[0].branches[1].name", is("dev"))
                .body("[0].branches[1].commit.sha", is("def456"))
                .body("[1].name", is("repo3"))
                .body("[1].owner.login", is("sampleuser"))
                .body("[1].branches", hasSize(1))
                .body("[1].branches[0].name", is("release"))
                .body("[1].branches[0].commit.sha", is("ghi789"));
    }
}