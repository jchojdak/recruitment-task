# Recruitment task

## Task summary

The goal is to expose a REST API that lists all **non-fork GitHub repositories** for a given user, including for each repository:

- Repository Name
- Owner Login
- Branches:
    - Branch Name
    - Last Commit SHA

In case the GitHub user does not exist, the API must return a `404` response in the following format:

```json
{
  "status": 404,
  "message": "Entity not found"
}
```

## Short information about the solution

- Built with **Java 21** and **Spring Boot 3.5.3**
- Uses **RestTemplate** for communication with the GitHub API
- No use of:
  - WebFlux
  - DDD / Hexagonal architecture
  - Pagination
- Includes only **one integration test** covering the happy path (JUnit, WireMock and RestAssured)

## Configuration

GitHub API settings are provided via application.yml:

```yaml
github:
  api:
    base-url: https://api.github.com
    token: ${GITHUB_API_TOKEN:DEFAULT_GITHUB_API_TOKEN}
    version: 2022-11-28
```

Set the GitHub API token as an environment variable.

## CI/CD – GitHub Actions
The project includes a simple GitHub Actions workflow for building and testing on push or pull request to the master branch.